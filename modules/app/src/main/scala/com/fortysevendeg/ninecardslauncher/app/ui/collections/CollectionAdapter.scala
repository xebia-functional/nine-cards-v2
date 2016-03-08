package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Context
import android.support.v7.widget.{CardView, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.analytics._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.ReorderItemTouchListener
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView, R}
import macroid.{Ui, ActivityContextWrapper}
import macroid.FullDsl._
import com.fortysevendeg.ninecardslauncher2.TypedResource._

case class CollectionAdapter(var collection: Collection, heightCard: Int)
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderCollectionAdapter]
  with AnalyticDispatcher
  with ReorderItemTouchListener
  with LauncherExecutor { self =>

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.card_item, parent, false)
    new ViewHolderCollectionAdapter(
      content = view,
      heightCard = heightCard,
      onClick = (position: Int) => Ui {
        collection.cards.lift(position) foreach (card => execute(card.intent))
      })
  }

  override def getItemCount: Int = collection.cards.size

  override def onBindViewHolder(viewHolder: ViewHolderCollectionAdapter, position: Int): Unit =
    runUi(viewHolder.bind(collection.cards(position)))

  def addCards(cards: Seq[Card]) = {
    cards foreach (card => trackCard(card, AddedToCollectionAction))
    collection = collection.copy(cards = collection.cards ++ cards)
    val count = cards.length
    notifyItemRangeInserted(collection.cards.length - count, count)
  }

  def removeCard(card: Card) = {
    trackCard(card, RemovedInCollectionAction)
    val position = collection.cards.indexOf(card)
    collection = collection.copy(cards = collection.cards.filterNot(c => card == c))
    notifyItemRangeRemoved(position, 1)
  }

  def updateCards(cards: Seq[Card]) = {
    collection = collection.copy(cards = cards)
    notifyItemRangeChanged(0, cards.length)
  }

  private[this] def trackCard(card: Card, action: Action) = card.cardType match {
    case AppCardType =>
      for {
        category <- collection.appsCategory
        packageName <- card.packageName
      } yield {
        self !>>
          TrackEvent(
            screen = CollectionDetailScreen,
            category = AppCategory(category),
            action = action,
            label = Some(ProvideLabel(packageName)),
            value = Some(action match {
              case OpenCardAction => OpenAppFromCollectionValue
              case AddedToCollectionAction => AddedToCollectionValue
              case RemovedInCollectionAction => RemovedInCollectionValue
              case _ => NoValue
            }))
      }
    case _ =>
  }

  override def getApplicationContext: Context = activityContext.bestAvailable

  override def onItemMove(from: Int, to: Int): Unit = {
    collection = collection.copy(cards = collection.cards.reorder(from, to))
    notifyItemMoved(from, to)
  }
}

case class ViewHolderCollectionAdapter(
  content: CardView,
  heightCard: Int,
  onClick: (Int) => Ui[_])(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with CollectionAdapterStyles
  with TypedFindView {

  lazy val iconContent = Option(findView(TR.card_icon_content))

  lazy val icon = Option(findView(TR.card_icon))

  lazy val name = Option(findView(TR.card_text))

  lazy val badge = Option(findView(TR.card_badge))

  runUi(
    (content <~ rootStyle(heightCard) <~ On.click {
      onClick(getAdapterPosition)
    }) ~ (iconContent <~ iconContentStyle(heightCard)))

  def bind(card: Card)(implicit uiContext: UiContext[_]): Ui[_] =
    (icon <~ iconCardTransform(card)) ~
      (name <~ tvText(card.term)) ~
      (badge <~ (getBadge(card.cardType) map {
        ivSrc(_) + vVisible
      } getOrElse vGone)) ~
      (name <~ nameStyle(card.cardType))

  private[this] def getBadge(cardType: CardType): Option[Int] = cardType match {
    case PhoneCardType => Option(R.drawable.badge_phone)
    case SmsCardType => Option(R.drawable.badge_sms)
    case EmailCardType => Option(R.drawable.badge_email)
    case _ => None
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
