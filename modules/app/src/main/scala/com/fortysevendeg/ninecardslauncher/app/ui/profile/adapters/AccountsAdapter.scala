package com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.{Header, AccountSync}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

case class AccountsAdapter(items: Seq[AccountSync])(implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderAccountsAdapter] {

  private[this] val headerType = 1

  private[this] val itemType = 0

  override def getItemCount: Int = items.size

  override def onBindViewHolder(viewHolder: ViewHolderAccountsAdapter, position: Int): Unit =
    runUi(viewHolder.bind(items(position), position))

  override def onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolderAccountsAdapter =
    items(position).accountSyncType match {
      case `Header` =>
        android.util.Log.d("9Cards", s"----> Creating header item for ${items(position)}")
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.profile_account_item_header, parent, false)
        new ViewHolderAccountsHeaderAdapter(view)
      case _ =>
        android.util.Log.d("9Cards", s"----> Creating simple item for ${items(position)}")
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.profile_account_item, parent, false)
        new ViewHolderAccountItemAdapter(view)
    }

  override def getItemViewType(position: Int): Int =
    if (items(position).accountSyncType == Header) {
      android.util.Log.d("9Cards", s"----> Return type $headerType item for ${items(position)}")
      headerType
    } else {
      android.util.Log.d("9Cards", s"----> Return type $itemType item for ${items(position)}")
      itemType
    }

}

abstract class ViewHolderAccountsAdapter(content: View)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_]

  override def findViewById(id: Int): View = content.findViewById(id)

}

case class ViewHolderAccountsHeaderAdapter(content: View)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends ViewHolderAccountsAdapter(content) {

  lazy val titleView = Option(findView(TR.title))

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    {
      android.util.Log.d("9Cards", s"----> Binding header item $accountSync")
      titleView <~ tvText(accountSync.title)
    }

}

case class ViewHolderAccountItemAdapter(content: View)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends ViewHolderAccountsAdapter(content) {

  lazy val titleView = Option(findView(TR.title))

  lazy val subtitleView = Option(findView(TR.subtitle))

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    {
      android.util.Log.d("9Cards", s"----> Binding simple item $accountSync")
      (titleView <~ tvText(accountSync.title)) ~
        (subtitleView <~ (accountSync.subtitle match {
          case Some(s) => tvText(s) + vVisible
          case None => vGone
        }))
    }

}