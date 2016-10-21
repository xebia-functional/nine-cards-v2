package cards.nine.app.ui.profile.adapters

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.components.widgets.tweaks.CollectionCheckBoxTweaks._
import cards.nine.app.ui.profile.SubscriptionsAdapterStyles
import cards.nine.app.ui.profile.ops.SubscriptionOps._
import cards.nine.models.Subscription
import cards.nine.process.theme.models.{ThemeType, NineCardsTheme}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class SubscriptionsAdapter(
  subscriptions: Seq[Subscription],
  onSubscribe: (String, Boolean) => Unit)(implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderSubscriptionsAdapter] {

  override def getItemCount: Int = subscriptions.size

  override def onBindViewHolder(viewHolder: ViewHolderSubscriptionsAdapter, position: Int): Unit =
    viewHolder.bind(subscriptions(position), position).run

  override def onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolderSubscriptionsAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.profile_subscription_item, parent, false)
    ViewHolderSubscriptionsAdapter(view, onSubscribe)
  }
}

case class ViewHolderSubscriptionsAdapter(
  content: View,
  onSubscribe: (String, Boolean) => Unit)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], val theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView
  with SubscriptionsAdapterStyles {

  lazy val root = findView(TR.subscriptions_item_layout)

  lazy val iconContent = findView(TR.subscriptions_item_content)

  lazy val checkbox = findView(TR.collection_subscription_checkbox)

  lazy val name = findView(TR.subscriptions_item_name)

  lazy val apps = findView(TR.subscriptions_item_apps)

  ((name <~ titleTextStyle) ~
    (apps <~ subtitleTextStyle)).run

  def bind(subscription: Subscription, position: Int)(implicit uiContext: UiContext[_]): Ui[_] = {
    val subscriptionColor = theme.getIndexColor(subscription.themedColorIndex)
    (checkbox <~ ccbInitialize(subscription.getIconSubscriptionDetail, subscriptionColor, theme.parent, defaultCheck = subscription.subscribed)) ~
      (name <~ tvText(resGetString(subscription.name) getOrElse subscription.name)) ~
      (apps <~ tvText(resGetString(R.string.installed_apps_number, subscription.apps.toString))) ~
      (content <~ On.click(
        Ui(ccbDoCheck(subscriptionColor, theme.parent, !subscription.subscribed)) ~
        Ui(onSubscribe(subscription.sharedCollectionId, !subscription.subscribed))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}