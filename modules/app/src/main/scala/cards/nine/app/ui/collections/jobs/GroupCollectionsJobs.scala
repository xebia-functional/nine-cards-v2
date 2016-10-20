package cards.nine.app.ui.collections.jobs

import android.content.Intent
import android.graphics.Bitmap
import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.commons.action_filters.MomentReloadedActionFilter
import cards.nine.app.ui.commons.{BroadAction, JobException, Jobs, RequestCodes}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Card._
import cards.nine.models.types._
import cards.nine.models.{Card, CardData, Collection}
import cards.nine.process.accounts.CallPhone
import cats.implicits._
import macroid.ActivityContextWrapper

class GroupCollectionsJobs(actions: GroupCollectionsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with AppNineCardsIntentConversions { self =>

  val delay = 200

  var collections: Seq[Collection] = Seq.empty

  def initialize(indexColor: Int, icon: String, position: Int, isStateChanged: Boolean): TaskService[Unit] = {
    for {
      theme <- getThemeTask
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
      currentCollection <- fetchCurrentCollection
      databaseCollection <- di.collectionProcess.getCollectionById(currentCollection.id)
        .resolveOption(s"Can't find the collection with id ${currentCollection.id}")
      cardsAreDifferent = databaseCollection.cards != currentCollection.cards
      _ <- actions.reloadCards(databaseCollection.cards).resolveIf(cardsAreDifferent, ())
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(cardsAreDifferent && currentIsMoment, ())
    } yield databaseCollection.cards

  def editCard(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
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
      currentCollection <- fetchCurrentCollection
      currentCollectionId = currentCollection.id
      cards = filterSelectedCards(currentCollection.cards)
      _ <- closeEditingMode()
      _ <- di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id))
      _ <- actions.removeCards(cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment, ())
    } yield cards

  def moveToCollection(toCollectionId: Int, collectionPosition: Int): TaskService[Seq[Card]] =
    for {
      currentCollection <- fetchCurrentCollection
      toCollection <- actions.getCollection(collectionPosition)
        .resolveOption(s"Can't find the collection in the position $collectionPosition in the UI")
      currentCollectionId = currentCollection.id
      cards = filterSelectedCards(currentCollection.cards)
      otherIsMoment = toCollection.collectionType == MomentCollectionType
      _ <- closeEditingMode()
      // TODO We must to create a new methods for moving cards to collection in #828
      // We should change this calls when the method will be ready
      _ <- di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id))
      _ <- di.collectionProcess.addCards(toCollectionId, cards map (_.toData))
      _ <- actions.removeCards(cards)
      _ <- actions.addCardsToCollection(collectionPosition, cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment || otherIsMoment, ())
    } yield cards

  def savePublishStatus(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
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

  def addCards(cardsRequest: Seq[CardData]): TaskService[Seq[Card]] =
    for {
      currentCollection <- fetchCurrentCollection
      currentCollectionId = currentCollection.id
      cards <- di.collectionProcess.addCards(currentCollectionId, cardsRequest)
      _ <- actions.addCards(cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment, ())
    } yield cards

  def addShortcut(name: String, shortcutIntent: Intent, bitmap: Option[Bitmap]): TaskService[Seq[Card]] = {

    def createShortcut(collectionId: Int): TaskService[Seq[Card]] = for {
      path <- bitmap map (di.deviceProcess.saveShortcutIcon(_).map(Option(_))) getOrElse TaskService.right(None)
      cardData = CardData(
        term = name,
        packageName = None,
        cardType = ShortcutCardType,
        intent = toNineCardIntent(shortcutIntent),
        imagePath = path)
      cards <- di.collectionProcess.addCards(collectionId, Seq(cardData))
    } yield cards

    for {
      currentCollection <- fetchCurrentCollection
      cards <- createShortcut(currentCollection.id)
      _ <- actions.addCards(cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment, ())
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
    actions.startEditing(statuses.getPositionsSelected)
  }

  def closeEditingMode(): TaskService[Unit] = {
    statuses = statuses.copy(collectionMode = NormalCollectionMode, positionsEditing = Set.empty)
    actions.closeEditingModeUi()
  }

  def emptyCollection(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      _ <- actions.showMenuButton(autoHide = false, currentCollection.themedColorIndex)
    } yield ()

  def firstItemInCollection(): TaskService[Unit] = actions.hideMenuButton()

  def close(): TaskService[Unit] = actions.close()

  def startScroll(): TaskService[Unit] =
    for {
      currentCollection <-  fetchCurrentCollection
      _ <- actions.showMenuButton(autoHide = true, currentCollection.themedColorIndex)
    } yield ()

  def showGenericError(): TaskService[Unit] = actions.showContactUsError

  private[this] def filterSelectedCards(cards: Seq[Card]): Seq[Card] = cards.zipWithIndex flatMap {
    case (card, index) if statuses.positionsEditing.contains(index) => Option(card)
    case _ => None
  }

  private[this] def fetchCurrentCollection: TaskService[Collection] =
    actions.getCurrentCollection.resolveOption("Can't find the current collection in the UI")

  private[this] def collectionIsMoment(currentCollectionId: Int): TaskService[Boolean] =
    for {
    // TODO Create getMomentByCollectionId #975
      moments <- di.momentProcess.getMoments
      currentIsMoment = moments.exists(_.collectionId.contains(currentCollectionId))
    } yield currentIsMoment

}

sealed trait CollectionMode

case object NormalCollectionMode extends CollectionMode

case object EditingCollectionMode extends CollectionMode