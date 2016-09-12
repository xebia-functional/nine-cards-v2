package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v7.widget.{CardView, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.styles.CollectionAdapterStyles
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.ReorderItemTouchListener
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.{FontSize, IconsSize}
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui, _}

case class CollectionAdapter(var collection: Collection, heightCard: Int)
  (implicit activityContext: ActivityContextWrapper,
    uiContext: UiContext[_],
    theme: NineCardsTheme,
    collectionPresenter: CollectionPresenter,
    collectionsPagerPresenter: CollectionsPagerPresenter)
  extends RecyclerView.Adapter[ViewHolderCollectionAdapter]
  with ReorderItemTouchListener { self =>

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.card_item, parent, false)
    ViewHolderCollectionAdapter(
      content = view,
      heightCard = heightCard)
  }

  override def getItemCount: Int = collection.cards.size

  override def onBindViewHolder(viewHolder: ViewHolderCollectionAdapter, position: Int): Unit =
    viewHolder.bind(collection.cards(position)).run

  def addCards(cards: Seq[Card]) = {
    collection = collection.copy(cards = collection.cards ++ cards)
    val count = cards.length
    notifyItemRangeInserted(collection.cards.length - count, count)
  }

  def removeCard(card: Card) = {
    val position = collection.cards.indexOf(card)
    collection = collection.copy(cards = collection.cards.filterNot(c => card == c))
    notifyItemRemoved(position)
  }

  def updateCard(card: Card) = {
    val position = card.position
    collection = collection.copy(cards = collection.cards.updated(position, card))
    notifyItemChanged(position, card)
  }

  def updateCards(cards: Seq[Card]) = {
    collection = collection.copy(cards = cards)
    notifyItemRangeChanged(0, cards.length)
  }

  override def onItemMove(from: Int, to: Int): Unit = {
    collection = collection.copy(cards = collection.cards.reorder(from, to))
    notifyItemMoved(from, to)
  }
}

case class ViewHolderCollectionAdapter(
  content: CardView,
  heightCard: Int)
  (implicit context: ActivityContextWrapper,
    theme: NineCardsTheme,
    collectionPresenter: CollectionPresenter,
    collectionsPagerPresenter: CollectionsPagerPresenter)
  extends RecyclerView.ViewHolder(content)
  with CollectionAdapterStyles
  with TypedFindView {

  lazy val iconContent = Option(findView(TR.card_icon_content))

  lazy val icon = Option(findView(TR.card_icon))

  lazy val name = Option(findView(TR.card_text))

  lazy val badge = Option(findView(TR.card_badge))

  ((content <~ rootStyle(heightCard) <~ On.longClick {
    Ui {
      collectionPresenter.startReorderCards(this)
      true
    }
  }) ~ (iconContent <~ iconContentStyle(heightCard))).run

  def bind(card: Card)(implicit uiContext: UiContext[_]): Ui[_] =
    (content<~ On.click {
      Ui(collectionsPagerPresenter.launchCard(card))
    }) ~
      (icon <~ vResize(IconsSize.getIconApp) <~ iconCardTransform(card)) ~
      (name <~ tvText(card.term) <~ tvSizeResource(FontSize.getSizeResource) <~ nameStyle(card.cardType)) ~
      (badge <~ (getBadge(card.cardType) map {
        ivSrc(_) + vVisible
      } getOrElse vGone))

  private[this] def getBadge(cardType: CardType): Option[Int] = cardType match {
    case PhoneCardType => Option(R.drawable.badge_phone)
    case SmsCardType => Option(R.drawable.badge_sms)
    case EmailCardType => Option(R.drawable.badge_email)
    case _ => None
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
