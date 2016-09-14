package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Intent
import android.graphics.Bitmap
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.permissions.PermissionChecker
import com.fortysevendeg.ninecardslauncher.app.permissions.PermissionChecker.CallPhone
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.MomentReloadedActionFilter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.CollectionOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{Jobs, RequestCodes}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, MomentCollectionType, PhoneCardType, ShortcutCardType}
import com.fortysevendeg.ninecardslauncher.process.intents.LauncherExecutorProcessPermissionException
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class CollectionsPagerPresenter(
  actions: CollectionsPagerUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with NineCardIntentConversions { self =>

  val delay = 200

  val permissionChecker = new PermissionChecker

  var collections: Seq[Collection] = Seq.empty

  var statuses = CollectionsPagerStatuses()

  def initialize(indexColor: Int, icon: String, position: Int, isStateChanged: Boolean): Unit = {
    actions.initialize(indexColor, icon, isStateChanged).run
    Task.fork(di.collectionProcess.getCollections.value).resolveAsyncUi(
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
    Task.fork(di.collectionProcess.getCollectionById(collection.id).value).resolveAsync(
      onResult = (c) => c map (newCollection => if (newCollection.cards != collection.cards) {
        momentReloadBroadCastIfNecessary()
        actions.reloadCards(newCollection.cards, reloadFragment).run
      })
    )
  }

  def removeCards(): Unit = actions.getCurrentCollection match {
    case Some(collection) =>
      val currentCollectionId = collection.id
      val cards = filterSelectedCards(collection.cards)
      closeEditingMode()

      di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id)).value.resolveAsyncUi(
        onResult = (_) => {
          momentReloadBroadCastIfNecessary()
          actions.removeCards(cards)
        },
        onException = (_) => actions.showContactUsError)
    case _ => actions.showContactUsError.run
  }

  def moveToCollection(toCollectionId: Int, collectionPosition: Int): Unit =
    actions.getCurrentCollection match {
      case Some(collection) =>
        val currentCollectionId = collection.id
        val cards = filterSelectedCards(collection.cards)
        closeEditingMode()

        (for {
          _ <- di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id))
          _ <- di.collectionProcess.addCards(toCollectionId, cards map toAddCardRequest)
        } yield ()).value.resolveAsyncUi(
          onResult = (_) => {
            momentReloadBroadCastIfNecessary(Option(collectionPosition))
            actions.removeCards(cards) ~ actions.addCardsToCollection(collectionPosition, cards)
          },
          onException = (_) => actions.showContactUsError)
      case _ => actions.showContactUsError.run
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
    Task.fork(di.collectionProcess.getCollectionById(collection.id).value).resolveAsync(
      onResult = (c) => c foreach { col =>
        if (col.sharedCollectionId.isDefined) {
          col.getUrlSharedCollection foreach { text =>
            Task.fork(di.launcherExecutorProcess.launchShare(text).value).resolveAsyncUi(
              onException = _ => actions.showContactUsError)
          }
        } else {
          actions.showMessageNotPublishedCollectionError.run
        }
      })
  }


  def performCard(card : Card, position: Int): Unit = {
    statuses.collectionMode match {
      case EditingCollectionMode =>
        val positions = if (statuses.positionsEditing.contains(position)) {
          statuses.positionsEditing - position
        } else {
          statuses.positionsEditing + position
        }
        statuses = statuses.copy(positionsEditing = positions)
        if (statuses.positionsEditing.isEmpty) {
          closeEditingMode()
        } else {
          actions.reloadItemCollection(position).run
        }
      case NormalCollectionMode =>
        Task.fork(di.launcherExecutorProcess.execute(card.intent).value).resolveAsyncUi(
          onException = (throwable: Throwable) => throwable match {
            case e: LauncherExecutorProcessPermissionException if card.cardType == PhoneCardType =>
              statuses = statuses.copy(lastPhone = card.intent.extractPhone())
              Ui(permissionChecker.requestPermission(RequestCodes.phoneCallPermission, CallPhone))
            case _ => actions.showContactUsError
          })
    }
  }

  def requestPermissionsResult(
    requestCode: Int,
    permissions: Array[String],
    grantResults: Array[Int]): Unit =
    if (requestCode == RequestCodes.phoneCallPermission) {
      val result = permissionChecker.readPermissionRequestResult(permissions, grantResults)
      if (result.exists(_.hasPermission(CallPhone))) {
        statuses.lastPhone foreach { phone =>
          statuses = statuses.copy(lastPhone = None)
          Task.fork(di.launcherExecutorProcess.execute(phoneToNineCardIntent(phone)).value).resolveAsyncUi(
            onException = _ => actions.showContactUsError)
        }
      } else {
        statuses.lastPhone foreach { phone =>
          statuses = statuses.copy(lastPhone = None)
          Task.fork(di.launcherExecutorProcess.launchDial(Some(phone)).value).resolveAsyncUi(
            onException = _ => actions.showContactUsError)
        }
        actions.showNoPhoneCallPermissionError().run
      }
    }

  def addCards(cardsRequest: Seq[AddCardRequest]): Unit = actions.getCurrentCollection foreach { collection =>
    Task.fork(di.collectionProcess.addCards(collection.id, cardsRequest).value).resolveAsyncUi(
      onResult = (cards) => {
        momentReloadBroadCastIfNecessary()
        actions.addCards(cards)
      }
    )
  }

  def addShortcut(collectionId: Int, name: String, shortcutIntent: Intent, bitmap: Option[Bitmap]): Unit = {
    Task.fork(createShortcut(collectionId, name, shortcutIntent, bitmap).value).resolveAsyncUi(
      onResult = (cards) => {
        momentReloadBroadCastIfNecessary()
        actions.addCards(cards)
      }
    )
  }

  def scrollY(scroll: Int, dy: Int): Unit = actions.translationScrollY(scroll).run

  def openReorderMode(current: ScrollType, canScroll: Boolean): Unit = {
    statuses = statuses.copy(collectionMode = EditingCollectionMode)
    actions.openReorderModeUi(current, canScroll).run
  }

  def closeReorderMode(position: Int): Unit = {
    statuses = statuses.copy(positionsEditing = Set(position))
    actions.startEditing().run
  }

  def closeEditingMode(): Unit = {
    statuses = statuses.copy(collectionMode = NormalCollectionMode, positionsEditing = Set.empty)
    actions.closeEditingModeUi().run
  }

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
    val currentIsMoment = actions.getCurrentCollection exists (_.collectionType == MomentCollectionType)
    val otherIsMoment = (collectionPosition flatMap actions.getCollection) exists (_.collectionType == MomentCollectionType)
    if (currentIsMoment || otherIsMoment) sendBroadCast(BroadAction(MomentReloadedActionFilter.action))
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
    bitmap map (di.deviceProcess.saveShortcutIcon(_)) getOrElse TaskService(Task(Xor.right(""))) // We use a empty string because the UI will generate an image

  private[this] def filterSelectedCards(cards: Seq[Card]): Seq[Card] = cards.zipWithIndex flatMap {
    case (card, index) if statuses.positionsEditing.contains(index) => Option(card)
    case _ => None
  }

}

trait CollectionsPagerUiActions {

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

  def showNoPhoneCallPermissionError(): Ui[Any]

  def showCollections(collections: Seq[Collection], position: Int): Ui[Any]

  def reloadCards(cards: Seq[Card], reloadFragments: Boolean): Ui[Any]

  def addCards(cards: Seq[Card]): Ui[Any]

  def addCardsToCollection(collectionPosition: Int, cards: Seq[Card]): Ui[Any]

  def removeCards(cards: Seq[Card]): Ui[Any]

  def getCurrentCollection: Option[Collection]

  def getCollection(position: Int): Option[Collection]

  def translationScrollY(scroll: Int): Ui[Any]

  def openReorderModeUi(current: ScrollType, canScroll: Boolean): Ui[Any]

  def startEditing(): Ui[Any]

  def reloadItemCollection(position: Int): Ui[Any]

  def closeEditingModeUi(): Ui[Any]

  def notifyScroll(sType: ScrollType): Ui[Any]

  def pullCloseScrollY(scroll: Int, scrollType: ScrollType, close: Boolean): Ui[Any]

  def exitTransition: Ui[Any]

  def showMenuButton(autoHide: Boolean = true, collection: Collection): Ui[Any]

  def hideMenuButton: Ui[Any]
}

case class CollectionsPagerStatuses(
  collectionMode: CollectionMode = NormalCollectionMode,
  positionsEditing: Set[Int] = Set.empty,
  lastPhone: Option[String] = None)

sealed trait CollectionMode

case object NormalCollectionMode extends CollectionMode

case object EditingCollectionMode extends CollectionMode