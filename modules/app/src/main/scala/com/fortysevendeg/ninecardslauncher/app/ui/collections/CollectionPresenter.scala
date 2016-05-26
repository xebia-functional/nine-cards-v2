package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Context
import android.support.v7.widget.RecyclerView.ViewHolder
import com.fortysevendeg.ninecardslauncher.app.analytics.{NoValue, RemovedInCollectionAction, RemovedInCollectionValue, _}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import macroid.{ActivityContextWrapper, Ui}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppCardType

import scalaz.concurrent.Task

case class CollectionPresenter(
  animateCards: Boolean,
  maybeCollection: Option[Collection],
  actions: CollectionUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with AnalyticDispatcher { self =>

  override def getApplicationContext: Context = contextWrapper.application

  def initialize(sType: ScrollType): Unit = {
    val canScroll = maybeCollection exists (_.cards.length > numSpaces)
    (actions.updateStatus(canScroll, sType) ~
      (maybeCollection map { collection =>
        actions.initialize(animateCards, collection)
      } getOrElse actions.showEmptyCollection())).run
  }

  def startReorderCards(holder: ViewHolder): Unit = if (!actions.isPulling()) actions.startReorder(holder).run

  def reorderCard(collectionId: Int, cardId: Int, position: Int): Unit = {
    Task.fork(di.collectionProcess.reorderCard(collectionId, cardId, position).run).resolveAsyncUi(
      onResult = (_) => actions.reloadCards()
    )
  }

  def moveToCard(): Unit = actions.showMessageNotImplemented().run // TODO change that

  def editCard(): Unit = actions.showMessageNotImplemented().run // TODO change that

  def addCards(cards: Seq[Card]): Unit = {
    cards foreach (card => trackCard(card, AddedToCollectionAction))
    actions.addCards(cards).run
  }

  def removeCard(card: Card): Unit = {
    trackCard(card, RemovedInCollectionAction)
    actions.removeCard(card).run
  }

  def reloadCards(cards: Seq[Card]): Unit = actions.reloadCards(cards).run

  def bindAnimatedAdapter(): Unit = maybeCollection foreach { collection =>
    actions.bindAnimatedAdapter(animateCards, collection).run
  }

  def showData(): Unit = maybeCollection foreach (c => actions.showData(c.cards.isEmpty).run)

  private[this] def trackCard(card: Card, action: Action): Unit = card.cardType match {
    case AppCardType =>
      for {
        collection <- actions.getCurrentCollection()
        packageName <- card.packageName
      } yield {
        val maybeCategory = collection.appsCategory map (c => Option(AppCategory(c))) getOrElse {
          collection.moment flatMap (_.momentType) map MomentCategory
        }
        maybeCategory foreach { category =>
          self !>>
            TrackEvent(
              screen = CollectionDetailScreen,
              category = category,
              action = action,
              label = Some(ProvideLabel(packageName)),
              value = Some(action match {
                case OpenCardAction => OpenAppFromCollectionValue
                case AddedToCollectionAction => AddedToCollectionValue
                case RemovedInCollectionAction => RemovedInCollectionValue
                case _ => NoValue
              }))
        }
      }
    case _ =>
  }

}

trait CollectionUiActions {

  def initialize(animateCards: Boolean, collection: Collection): Ui[Any]

  def updateStatus(canScroll: Boolean, sType: ScrollType): Ui[Any]

  def startReorder(holder: ViewHolder): Ui[Any]

  def reloadCards(): Ui[Any]

  def bindAnimatedAdapter(animateCards: Boolean, collection: Collection): Ui[Any]

  def showMessageNotImplemented(): Ui[Any]

  def showEmptyCollection(): Ui[Any]

  def addCards(cards: Seq[Card]): Ui[Any]

  def removeCard(card: Card): Ui[Any]

  def reloadCards(cards: Seq[Card]): Ui[Any]

  def showData(emptyCollection: Boolean): Ui[Any]

  def isPulling(): Boolean

  def getCurrentCollection(): Option[Collection]

}