package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v7.widget.{CardView, RecyclerView}
import android.view.View.{OnClickListener, OnLongClickListener}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, UiContext}
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper
import macroid.FullDsl._

case class CollectionAdapter(var collection: Collection, heightCard: Int)
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderCollectionAdapter]
  with LauncherExecutor {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.card_item, parent, false).asInstanceOf[CardView]
    val adapter = new ViewHolderCollectionAdapter(view, heightCard)
    adapter.content.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = for {
        tag <- Option(v.getTag)
        pos = Int.unbox(tag)
        c <- collection.cards.lift(pos)
      } yield execute(c.intent)
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
    runUi(viewHolder.bind(card, position))
  }

  def addCards(cards: Seq[Card]) = {
    collection = collection.copy(cards = collection.cards ++ cards)
    val count = cards.length
    notifyItemRangeInserted(collection.cards.length - count, count)
  }

  def removeCard(card: Card) = {
    val position = collection.cards.indexOf(card)
    collection = collection.copy(cards = collection.cards.filterNot(c => card == c))
    notifyItemRangeRemoved(position, 1)
  }

  def updateCards(cards: Seq[Card]) = {
    collection = collection.copy(cards = cards)
    notifyItemRangeChanged(0, cards.length)
  }

}


