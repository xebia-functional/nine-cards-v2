package cards.nine.app.ui.profile.adapters

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.preferences.commons.ShowPrintInfoOptionInAccounts
import cards.nine.app.ui.profile.AccountsAdapterStyles
import cards.nine.app.ui.profile.adapters.AccountOptions._
import cards.nine.app.ui.profile.models.{AccountSync, Device, Header}
import cards.nine.models.NineCardsTheme
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class AccountsAdapter(
  items: Seq[AccountSync],
  clickListener: (AccountOption, AccountSync) => Unit)(implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
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
        ViewHolderAccountItemAdapter(view, clickListener)
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

case class ViewHolderAccountsHeaderAdapter(content: View)(implicit context: ActivityContextWrapper, val theme: NineCardsTheme)
  extends ViewHolderAccountsAdapter(content)
  with AccountsAdapterStyles {

  lazy val title = findView(TR.title)

  (title <~ subtitleTextStyle).run

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    title <~ tvText(accountSync.title)

}

case class ViewHolderAccountItemAdapter(
  content: View,
  onClick: (AccountOption, AccountSync) => Unit)(implicit context: ActivityContextWrapper, val theme: NineCardsTheme)
  extends ViewHolderAccountsAdapter(content)
  with AccountsAdapterStyles {

  lazy val currentAccountOptions = Seq(
    (CopyOption, resGetString(R.string.menuAccountCopy)),
    (SyncOption, resGetString(R.string.menuAccountSync)),
    (ChangeNameOption, resGetString(R.string.menuAccountChangeName)))

  lazy val otherAccountOptions = Seq(
    (CopyOption, resGetString(R.string.menuAccountCopy)),
    (DeleteOption, resGetString(R.string.menuAccountDelete)),
    (ChangeNameOption, resGetString(R.string.menuAccountChangeName)))

  def menuOptions(isCurrent: Boolean): Seq[(AccountOption, String)] =
    if (isCurrent) currentAccountOptions else otherAccountOptions

  lazy val title = findView(TR.profile_account_title)

  lazy val printDriveInfo = (PrintInfoOption, resGetString(R.string.menuAccountPrintInfo))

  lazy val showPrintDriveInfo = ShowPrintInfoOptionInAccounts.readValue

  lazy val subtitle = findView(TR.profile_account_subtitle)

  lazy val device = findView(TR.profile_account_device)

  lazy val icon = findView(TR.profile_account_action)

  ((title <~ titleTextStyle) ~
    (subtitle <~ subtitleTextStyle) ~
    (device <~ iconStyle) ~
    (icon <~ iconStyle)).run

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_] = {

    val isCurrent = accountSync.accountSyncType match {
      case d: Device => d.current
      case _ => false
    }

    val menuSeq = if (showPrintDriveInfo) menuOptions(isCurrent) :+ printDriveInfo else menuOptions(isCurrent)

    (title <~ tvText(accountSync.title)) ~
      (subtitle <~ tvText(accountSync.subtitle getOrElse "")) ~
      (icon <~ ivSrc(R.drawable.icon_account_options) <~
        On.click {
          icon <~
            vListThemedPopupWindowShow(
              values = menuSeq map {
                case (_, name) => name
              },
              onItemClickListener = (position) => {
                menuSeq.lift(position) foreach {
                  case (option, _) => onClick(option, accountSync)
                }
              },
              width = Option(resGetDimensionPixelSize(R.dimen.width_list_popup_menu)))
        })
  }

}

object AccountOptions {

  sealed trait AccountOption

  case object CopyOption extends AccountOption

  case object SyncOption extends AccountOption

  case object DeleteOption extends AccountOption

  case object ChangeNameOption extends AccountOption

  case object PrintInfoOption extends AccountOption

}