package com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters

import android.support.v7.widget.{CardView, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.profile.AccountsAdapterStyles
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, _}

case class AccountsAdapter(items: Seq[String])(implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderPublicationsAdapter] {

  override def getItemCount: Int = items.size

  override def onBindViewHolder(viewHolder: ViewHolderPublicationsAdapter, position: Int): Unit =
    runUi(viewHolder.bind(items(position), position))

  override def onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolderPublicationsAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.profile_account_item, parent, false).asInstanceOf[CardView]
    new ViewHolderPublicationsAdapter(view)
  }
}

case class ViewHolderAccountsAdapter(content: View)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with AccountsAdapterStyles
  with TypedFindView {

  lazy val cardTitle = Option(findView(TR.title))

  runUi(content <~ rootStyle())

  def bind(title: String, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    cardTitle <~ tvText(title)

  override def findViewById(id: Int): View = content.findViewById(id)

}