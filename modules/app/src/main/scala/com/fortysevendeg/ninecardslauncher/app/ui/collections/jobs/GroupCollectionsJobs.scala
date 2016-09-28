package com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs

import android.content.Intent
import android.graphics.Bitmap
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.MomentReloadedActionFilter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{JobException, Jobs, RequestCodes}
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.Theme
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.{TaskService, _}
import com.fortysevendeg.ninecardslauncher.process.accounts.CallPhone
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{MomentCollectionType, ShortcutCardType}
import macroid.ActivityContextWrapper

class GroupCollectionsJobs(actions: GroupCollectionsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with NineCardIntentConversions { self =>

  val delay = 200

  var collections: Seq[Collection] = Seq.empty

  def initialize(indexColor: Int, icon: String, position: Int, isStateChanged: Boolean): TaskService[Unit] = {
    for {
      theme <- di.themeProcess.getTheme(Theme.getThemeFile(preferenceValues))
      _ <- actions.loadTheme(theme)
      _ <- actions.initialize(indexColor, icon, isStateChanged)
      collections <- di.collectionProcess.getCollections
      _ <- actions.showCollections(collections, position)
    } yield ()
  }

  def resume(): TaskService[Unit] = di.observerRegister.registerObserverTask()

  def pause(): TaskService[Unit] = di.observerRegister.unregisterObserverTask()

  def back(): TaskService[Unit] = actions.back()

  def destroy(): TaskService[Unit] = actions.destroy()

  def resetAction(): TaskService[Unit] = actions.resetAction

  def destroyAction(): TaskService[Unit] = actions.destroyAction

  def reloadCards(): TaskService[Seq[Card]] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      databaseCollection <- di.collectionProcess.getCollectionById(currentCollection.id).resolveOption()
      cardsAreDifferent = databaseCollection.cards != currentCollection.cards
      currentIsMoment = currentCollection.collectionType == MomentCollectionType
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(cardsAreDifferent && currentIsMoment, ())
      _ <- actions.reloadCards(databaseCollection.cards).resolveIf(cardsAreDifferent, ())
    } yield databaseCollection.cards

  def editCard(): TaskService[Unit] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      currentCollectionId = currentCollection.id
      cards = filterSelectedCards(currentCollection.cards)
      _ <- cards match {
        case head :: tail if tail.isEmpty =>
          closeEditingMode() *> actions.editCard(currentCollectionId, head.id, head.term)
        case _ => TaskService.left[Unit](JobException("You only can edit one card"))
      }
    } yield ()

  def removeCards(): TaskService[Seq[Card]] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      currentCollectionId = currentCollection.id
      cards = filterSelectedCards(currentCollection.cards)
      currentIsMoment = currentCollection.collectionType == MomentCollectionType
      _ <- closeEditingMode()
      _ <- di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id))
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment, ())
      _ <- actions.removeCards(cards)
    } yield cards

  def moveToCollection(toCollectionId: Int, collectionPosition: Int): TaskService[Seq[Card]] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      toCollection <- actions.getCollection(collectionPosition).resolveOption()
      currentCollectionId = currentCollection.id
      cards = filterSelectedCards(currentCollection.cards)
      currentIsMoment = currentCollection.collectionType == MomentCollectionType
      otherIsMoment = toCollection.collectionType == MomentCollectionType
      _ <- closeEditingMode()
      // TODO We must to create a new methods for moving cards to collection in #828
      // We should change this calls when the method will be ready
      _ <- di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id))
      _ <- di.collectionProcess.addCards(toCollectionId, cards map toAddCardRequest)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment || otherIsMoment, ())
      _ <- actions.removeCards(cards)
      _ <- actions.addCardsToCollection(collectionPosition, cards)
    } yield cards

  def savePublishStatus(): TaskService[Unit] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      _ <- TaskService.right(statuses = statuses.copy(publishStatus = currentCollection.publicCollectionStatus))
    } yield ()

  def performCard(card : Card, position: Int): TaskService[Unit] = {
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
          actions.reloadItemCollection(statuses.getPositionsSelected, position)
        }
      case NormalCollectionMode => di.launcherExecutorProcess.execute(card.intent)
    }
  }

  def requestCallPhonePermission(phone: Option[String]): TaskService[Unit] =  {
    statuses = statuses.copy(lastPhone = phone)
    di.userAccountsProcess.requestPermission(RequestCodes.phoneCallPermission, CallPhone)
  }

  def requestPermissionsResult(
    requestCode: Int,
    permissions: Array[String],
    grantResults: Array[Int]): TaskService[Unit] =
    if (requestCode == RequestCodes.phoneCallPermission) {
      for {
        result <- di.userAccountsProcess.parsePermissionsRequestResult(permissions, grantResults)
        hasCallPhonePermission = result.exists(_.hasPermission(CallPhone))
        _ <- (hasCallPhonePermission, statuses.lastPhone) match {
          case (true, Some(phone)) => di.launcherExecutorProcess.execute(phoneToNineCardIntent(None, phone))
          case (false, Some(phone)) =>
            di.launcherExecutorProcess.launchDial(Some(phone)) *>
              actions.showNoPhoneCallPermissionError()
          case _ => TaskService.empty
        }
        _ <- TaskService.right(statuses = statuses.copy(lastPhone = None))
      } yield ()
    } else {
      TaskService.empty
    }

  def addCards(cardsRequest: Seq[AddCardRequest]): TaskService[Seq[Card]] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      currentCollectionId = currentCollection.id
      currentIsMoment = currentCollection.collectionType == MomentCollectionType
      cards <- di.collectionProcess.addCards(currentCollectionId, cardsRequest)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment, ())
      _ <- actions.addCards(cards)
    } yield cards

  def addShortcut(name: String, shortcutIntent: Intent, bitmap: Option[Bitmap]): TaskService[Seq[Card]] = {

    def createShortcut(collectionId: Int): TaskService[Seq[Card]] = for {
      path <- bitmap map (di.deviceProcess.saveShortcutIcon(_).map(Option(_))) getOrElse TaskService.right(None)
      addCardRequest = AddCardRequest(
        term = name,
        packageName = None,
        cardType = ShortcutCardType,
        intent = toNineCardIntent(shortcutIntent),
        imagePath = path)
      cards <- di.collectionProcess.addCards(collectionId, Seq(addCardRequest))
    } yield cards

    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      currentIsMoment = currentCollection.collectionType == MomentCollectionType
      cards <- createShortcut(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment, ())
      _ <- actions.addCards(cards)
    } yield cards
  }

  def openReorderMode(current: ScrollType, canScroll: Boolean): TaskService[Unit] =
    for {
      _ <- statuses.collectionMode match {
        case EditingCollectionMode => actions.closeEditingModeUi()
        case _ => TaskService.right(statuses = statuses.copy(collectionMode = EditingCollectionMode))
      }
      _ <- actions.openReorderModeUi(current, canScroll)
    } yield ()


  def closeReorderMode(position: Int): TaskService[Unit] = {
    statuses = statuses.copy(positionsEditing = Set(position))
    // TODO call to actions.scrollIdle() in ToolbarJobs
    actions.startEditing(statuses.getPositionsSelected)
  }

  def closeEditingMode(): TaskService[Unit] = {
    statuses = statuses.copy(collectionMode = NormalCollectionMode, positionsEditing = Set.empty)
    actions.closeEditingModeUi()
  }

  def emptyCollection(): TaskService[Unit] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      _ <- actions.showMenuButton(autoHide = false, currentCollection.themedColorIndex)
    } yield ()

  def firstItemInCollection(): TaskService[Unit] = actions.hideMenuButton()

  def close(): TaskService[Unit] = actions.close()

  def startScroll(): TaskService[Unit] =
    for {
      currentCollection <-  actions.getCurrentCollection.resolveOption()
      _ <- actions.showMenuButton(autoHide = true, currentCollection.themedColorIndex)
    } yield ()

  def showGenericError(): TaskService[Unit] = actions.showContactUsError

  private[this] def filterSelectedCards(cards: Seq[Card]): Seq[Card] = cards.zipWithIndex flatMap {
    case (card, index) if statuses.positionsEditing.contains(index) => Option(card)
    case _ => None
  }

}

sealed trait CollectionMode

case object NormalCollectionMode extends CollectionMode

case object EditingCollectionMode extends CollectionMode