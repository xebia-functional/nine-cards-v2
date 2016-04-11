package com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.{Device, AccountSync, AccountSyncType, Header}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

case class AccountsAdapter(
  items: Seq[AccountSync],
  clickListener: (Int, AccountSync) => Unit)(implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderAccountsAdapter] {

  private[this] val headerType = 0

  private[this] val itemType = 1

  override def getItemCount: Int = items.size

  override def onBindViewHolder(viewHolder: ViewHolderAccountsAdapter, position: Int): Unit =
    viewHolder.bind(items(position), position).run

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAccountsAdapter =
    viewType match {
      case `headerType` =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.profile_account_item_header, parent, false)
        new ViewHolderAccountsHeaderAdapter(view)
      case _ =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.profile_account_item, parent, false)
        new ViewHolderAccountItemAdapter(view,
          (position: Int, accountSync: AccountSync) => Ui(clickListener(position, accountSync)))
    }

  override def getItemViewType(position: Int): Int =
    if (items(position).accountSyncType == Header) headerType else itemType

}

abstract class ViewHolderAccountsAdapter(content: View)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_]

  override def findViewById(id: Int): View = content.findViewById(id)

}

case class ViewHolderAccountsHeaderAdapter(content: View)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends ViewHolderAccountsAdapter(content) {

  lazy val title = Option(findView(TR.title))

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    title <~ tvText(accountSync.title)

}

case class ViewHolderAccountItemAdapter(
  content: View,
  onClick: (Int, AccountSync) => Ui[_])(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends ViewHolderAccountsAdapter(content) {

  lazy val title = Option(findView(TR.profile_account_title))

  lazy val subtitle = Option(findView(TR.profile_account_subtitle))

  lazy val icon = Option(findView(TR.profile_account_action))

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_] = {
    val isCurrent = accountSync.accountSyncType match {
      case d: Device => d.current
      case _ => false
    }
    (title <~ tvText(accountSync.title)) ~
      (subtitle <~ tvText(accountSync.subtitle getOrElse "")) ~
      (icon <~ ivSrc(if (isCurrent) {
        R.drawable.icon_account_sync
      } else {
        R.drawable.icon_account_delete
      }) <~ On.click {
        onClick(getAdapterPosition, accountSync)
      })
  }

}