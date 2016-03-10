package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Context
import android.support.v7.widget.{CardView, RecyclerView}
import android.view.View.{OnClickListener, OnLongClickListener}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.analytics._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, UiContext}
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppCardType
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper

case class CollectionAdapter(var collection: Collection, heightCard: Int)
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderCollectionAdapter]
  with AnalyticDispatcher
  with LauncherExecutor { self =>

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.card_item, parent, false).asInstanceOf[CardView]
    val adapter = new ViewHolderCollectionAdapter(view, heightCard)
    adapter.content.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = for {
        tag <- Option(v.getTag)
        pos = Int.unbox(tag)
        card <- collection.cards.lift(pos)
      } yield {
        trackCard(card, OpenCardAction)
        execute(card.intent)
      }
    })
    adapter.content.setOnLongClickListener(new OnLongClickListener {
      override def onLongClick(v: View): Boolean = {
        for {
          tag <- Option(v.getTag)
          pos = Int.unbox(tag)
          c <- collection.cards.lift(pos)
          activity <- activity[CollectionsDetailsActivity]
        } yield activity.removeCard(c)
        false
      }
    })
    adapter
  }

  override def getItemCount: Int = collection.cards.size

  override def onBindViewHolder(viewHolder: ViewHolderCollectionAdapter, position: Int): Unit = {
    val card = collection.cards(position)
    viewHolder.bind(card, position).run
  }

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
}


