package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Intent
import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.MomentReloadedActionFilter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.CollectionOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, MomentCollectionType, ShortcutCardType}
import macroid.{ActivityContextWrapper, Ui}
import monix.eval.Task
import cats.syntax.either._

class CollectionsPagerPresenter(
  actions: CollectionsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with NineCardIntentConversions { self =>

  val delay = 200

  var collections: Seq[Collection] = Seq.empty

  def initialize(indexColor: Int, icon: String, position: Int, isStateChanged: Boolean): Unit = {
    actions.initialize(indexColor, icon, isStateChanged).run
    di.collectionProcess.getCollections.resolveAsyncUi2(
      onResult = (collections: Seq[Collection]) => actions.showCollections(collections, position),
      onException = (ex: Throwable) => actions.showContactUsError
    )
  }

  def resume(): Unit = di.observerRegister.registerObserver()

  def pause(): Unit = di.observerRegister.unregisterObserver()

  def back(): Unit = actions.back().run

  def destroy(): Unit = actions.destroy().run

  def resetAction(): Unit = actions.resetAction.run

  def destroyAction(): Unit = actions.destroyAction.run

  def reloadCards(reloadFragment: Boolean): Unit = actions.getCurrentCollection foreach { collection =>
    di.collectionProcess.getCollectionById(collection.id).resolveAsync2(
      onResult = (c) => c map (newCollection => if (newCollection.cards != collection.cards) {
        momentReloadBroadCastIfNecessary()
        actions.reloadCards(newCollection.cards, reloadFragment).run
      })
    )
  }

  def moveToCollection(collectionId: Int, collectionPosition: Int, card: Card): Unit = {

    removeCard(card)

    di.collectionProcess.addCards(collectionId, Seq(toAddCardRequest(card))).resolveAsyncUi2(
      onResult = (cards) => {
        momentReloadBroadCastIfNecessary()
        actions.addCardsToCollection(collectionPosition, cards)
      })

  }

  def showMessageNotImplemented(): Unit = actions.showMessageNotImplemented.run
  
  def showPublishCollectionWizard(): Unit = {
    actions.getCurrentCollection map { collection =>
      if (collection.cards.exists(_.cardType == AppCardType)) {
        actions.showPublishCollectionWizardDialog(collection).run
      } else {
        actions.showMessagePublishContactsCollectionError.run
      }
    } getOrElse actions.showContactUsError.run
  }

  def shareCollection(): Unit = actions.getCurrentCollection foreach { collection =>
    di.collectionProcess.getCollectionById(collection.id).resolveAsync2(
      onResult = (c) => c foreach { col =>
        if (col.sharedCollectionId.isDefined) {
          col.getUrlSharedCollection foreach { text =>
            di.launcherExecutorProcess.launchShare(text).resolveAsyncUi2(
              onException = _ => actions.showContactUsError)
          }
        } else {
          actions.showMessageNotPublishedCollectionError.run
        }
      })
  }

  def addCards(cardsRequest: Seq[AddCardRequest]): Unit = actions.getCurrentCollection foreach { collection =>
    di.collectionProcess.addCards(collection.id, cardsRequest).resolveAsyncUi2(
      onResult = (cards) => {
        momentReloadBroadCastIfNecessary()
        actions.addCards(cards)
      }
    )
  }

  def removeCard(card: Card): Unit = actions.getCurrentCollection foreach { collection =>
    di.collectionProcess.deleteCard(collection.id, card.id).resolveAsyncUi2(
      onResult = (_) => {
        momentReloadBroadCastIfNecessary()
        actions.removeCards(card)
      }
    )
  }

  def addShortcut(collectionId: Int, name: String, shortcutIntent: Intent, bitmap: Option[Bitmap]): Unit = {
    createShortcut(collectionId, name, shortcutIntent, bitmap).resolveAsyncUi2(
      onResult = (cards) => {
        momentReloadBroadCastIfNecessary()
        actions.addCards(cards)
      }
    )
  }

  def scrollY(scroll: Int, dy: Int): Unit = actions.translationScrollY(scroll).run

  def openReorderMode(current: ScrollType, canScroll: Boolean): Unit = actions.openReorderModeUi(current, canScroll).run

  def scrollType(sType: ScrollType): Unit = actions.notifyScroll(sType).run

  def emptyCollection(): Unit = actions.getCurrentCollection foreach { collection =>
    actions.showMenuButton(autoHide = false, collection).run
  }

  def firstItemInCollection(): Unit = actions.hideMenuButton.run

  def pullToClose(scroll: Int, scrollType: ScrollType, close: Boolean): Unit =
    actions.pullCloseScrollY(scroll, scrollType, close).run

  def close(): Unit = actions.exitTransition.run

  def startScroll(): Unit = actions.getCurrentCollection foreach { collection =>
    actions.showMenuButton(autoHide = true, collection).run
  }

  private[this] def momentReloadBroadCastIfNecessary(collectionPosition: Option[Int] = None) = {
    val isMoment = (collectionPosition match {
      case Some(position) => actions.getCollection(position)
      case _ => actions.getCurrentCollection
    }) exists (_.collectionType == MomentCollectionType)
    if (isMoment) sendBroadCast(BroadAction(MomentReloadedActionFilter.action))
  }

  private[this] def createShortcut(collectionId: Int, name: String, shortcutIntent: Intent, bitmap: Option[Bitmap]):
  TaskService[Seq[Card]] = for {
    path <- saveShortcutIcon(bitmap)
    addCardRequest = AddCardRequest(
      term = name,
      packageName = None,
      cardType = ShortcutCardType,
      intent = toNineCardIntent(shortcutIntent),
      imagePath = path)
    cards <- di.collectionProcess.addCards(collectionId, Seq(addCardRequest))
  } yield cards

  private[this] def saveShortcutIcon(bitmap: Option[Bitmap]): TaskService[String] =
    bitmap map (di.deviceProcess.saveShortcutIcon(_)) getOrElse TaskService(Task(Either.right(""))) // We use a empty string because the UI will generate an image

}

trait CollectionsUiActions {

  def initialize(indexColor: Int, icon: String, isStateChanged: Boolean): Ui[Any]

  def back(): Ui[Any]

  def destroy(): Ui[Any]

  def resetAction: Ui[Any]

  def destroyAction: Ui[Any]

  def showPublishCollectionWizardDialog(collection: Collection): Ui[Any]

  def showMessagePublishContactsCollectionError: Ui[Any]

  def showMessageNotPublishedCollectionError: Ui[Any]

  def showContactUsError: Ui[Any]

  def showMessageNotImplemented: Ui[Any]

  def showCollections(collections: Seq[Collection], position: Int): Ui[Any]

  def reloadCards(cards: Seq[Card], reloadFragments: Boolean): Ui[Any]

  def addCards(cards: Seq[Card]): Ui[Any]

  def addCardsToCollection(collectionPosition: Int, cards: Seq[Card]): Ui[Any]

  def removeCards(card: Card): Ui[Any]

  def getCurrentCollection: Option[Collection]

  def getCollection(position: Int): Option[Collection]

  def translationScrollY(scroll: Int): Ui[Any]

  def openReorderModeUi(current: ScrollType, canScroll: Boolean): Ui[Any]

  def notifyScroll(sType: ScrollType): Ui[Any]

  def pullCloseScrollY(scroll: Int, scrollType: ScrollType, close: Boolean): Ui[Any]

  def exitTransition: Ui[Any]

  def showMenuButton(autoHide: Boolean = true, collection: Collection): Ui[Any]

  def hideMenuButton: Ui[Any]
}
