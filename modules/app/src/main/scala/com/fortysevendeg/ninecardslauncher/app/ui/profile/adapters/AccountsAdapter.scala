package com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters

import android.support.v7.widget.{PopupMenu, RecyclerView}
import android.view.{LayoutInflater, MenuItem, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.{AccountSync, Device, Header}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

case class AccountsAdapter(
  items: Seq[AccountSync],
  clickListener: (Int, Int, AccountSync) => Unit)(implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
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
        ViewHolderAccountsHeaderAdapter(view)
      case _ =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.profile_account_item, parent, false)
        ViewHolderAccountItemAdapter(view,
          (position: Int, itemId: Int, accountSync: AccountSync) => clickListener(position, itemId, accountSync))
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
  onClick: (Int, Int, AccountSync) => Unit)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
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
      (icon <~ ivSrc(R.drawable.icon_action_bar_options_dark) <~ On.click {
        icon <~ vPopupMenuShow(
          menu = if (isCurrent) R.menu.action_menu_current_account else R.menu.action_menu_other_accounts,
          onMenuItemClickListener = (item: MenuItem) => {
            onClick(getAdapterPosition, item.getItemId, accountSync)
            true
          }
        )
      })
  }

}