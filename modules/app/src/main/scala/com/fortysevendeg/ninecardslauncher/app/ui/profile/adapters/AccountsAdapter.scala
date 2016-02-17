package com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.profile.AccountsAdapterStyles
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

  override def onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolderAccountsAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(getItemViewLayout(position), parent, false)
    new ViewHolderAccountsAdapter(view)
  }

  override def getItemViewType(position: Int): Int =
    if (items(position).accountSyncType == Header) headerType else itemType

  private[this] def getItemViewLayout(position: Int): Int =
    getItemViewType(position) match {
      case `headerType` => R.layout.profile_account_item_header
      case _ => R.layout.profile_account_item
    }

}

case class ViewHolderAccountsAdapter(content: View)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with AccountsAdapterStyles
  with TypedFindView {

  lazy val titleView = Option(findView(TR.title))

  lazy val subtitleView = Option(findView(TR.subtitle))

  runUi(content <~ rootStyle())

  def bind(accountSync: AccountSync, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    (titleView <~ tvText(accountSync.title)) ~
      (accountSync.subtitle map (s => subtitleView <~ tvText(s)) getOrElse Ui.nop)

  override def findViewById(id: Int): View = content.findViewById(id)

}