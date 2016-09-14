package com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.profile._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

case class EmptyProfileAdapter(tab: ProfileTab)(implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderEmptyProfileAdapter] {

  val emptyElement = 1

  override def getItemCount: Int = emptyElement

  override def onBindViewHolder(viewHolder: ViewHolderEmptyProfileAdapter, position: Int): Unit =
    viewHolder.bind(tab).run

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEmptyProfileAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.empty_profile_item, parent, false)
    new ViewHolderEmptyProfileAdapter(view)
  }

}

case class ViewHolderEmptyProfileAdapter(
  content: View)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], val theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView
  with EmptyProfileAdapterStyles {

  lazy val root = findView(TR.profile_empty_item)

  lazy val emptyProfileMessage = findView(TR.profile_empty_message)

  lazy val messagePublicationsText = Html.fromHtml(resGetString(R.string.emptyPublishedCollectionsMessage))

  lazy val messageSubscriptionsText = Html.fromHtml(resGetString(R.string.emptySubscriptionsMessage))

  lazy val messageAccountsText = Html.fromHtml(resGetString(R.string.emptySubscriptionsMessage))

  ((root <~ rootStyle()) ~
    (emptyProfileMessage <~ textStyle)).run

  def bind(tab: ProfileTab)(implicit uiContext: UiContext[_]): Ui[_] = {

    val textTweak = tab match {
      case PublicationsTab => tvText(messagePublicationsText)
      case SubscriptionsTab => tvText(messageSubscriptionsText)
      case AccountsTab => tvText(messageAccountsText)
    }

    emptyProfileMessage <~
      textTweak
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}