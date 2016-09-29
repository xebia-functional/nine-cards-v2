package cards.nine.app.ui.profile.adapters

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.profile._
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class EmptyProfileAdapter(
  tab: ProfileTab,
  error: Boolean,
  reload: () => Unit)(implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderEmptyProfileAdapter] {

  val emptyElement = 1

  override def getItemCount: Int = emptyElement

  override def onBindViewHolder(viewHolder: ViewHolderEmptyProfileAdapter, position: Int): Unit =
    viewHolder.bind(tab, error).run

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEmptyProfileAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.empty_profile_item, parent, false)
    new ViewHolderEmptyProfileAdapter(view, reload)
  }

}

case class ViewHolderEmptyProfileAdapter(
  content: View,
  reload: () => Unit)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], val theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView
  with EmptyProfileAdapterStyles {

  lazy val root = findView(TR.profile_empty_item)

  lazy val emptyProfileImage = findView(TR.profile_empty_image)

  lazy val emptyProfileMessage = findView(TR.profile_empty_message)

  lazy val emptyProfileButton = findView(TR.profile_empty_button)

  lazy val messagePublicationsText = resGetString(R.string.emptyPublishedCollectionsMessage)

  lazy val messageSubscriptionsText = resGetString(R.string.emptySubscriptionsMessage)

  lazy val messageAccountsText = resGetString(R.string.emptySubscriptionsMessage)

  lazy val messageAccountsErrorText = resGetString(R.string.errorConnectingGoogle)

  lazy val messagePublicationsErrorText = resGetString(R.string.errorLoadingPublishedCollections)

  lazy val messageSubscriptionsErrorText = resGetString(R.string.errorLoadingSubscriptions)

  ((root <~ rootStyle) ~
    (emptyProfileImage <~ imageStyle) ~
    (emptyProfileMessage <~ textStyle) ~
    (emptyProfileButton <~ buttonStyle)).run

  def bind(tab: ProfileTab, error: Boolean)(implicit uiContext: UiContext[_]): Ui[_] = {

    val messageText = tab match {
      case PublicationsTab if error => messagePublicationsErrorText
      case PublicationsTab => messagePublicationsText
      case SubscriptionsTab if error => messageSubscriptionsErrorText
      case SubscriptionsTab => messageSubscriptionsText
      case AccountsTab if error => messageAccountsErrorText
      case AccountsTab => messageAccountsText
    }

    (emptyProfileImage <~ ivSrc(if (error) R.drawable.placeholder_error else R.drawable.placeholder_empty)) ~
      (emptyProfileMessage <~ tvText(Html.fromHtml(messageText))) ~
      (emptyProfileButton <~ (if (error) On.click(Ui(reload())) else vInvisible))

  }

  override def findViewById(id: Int): View = content.findViewById(id)

}