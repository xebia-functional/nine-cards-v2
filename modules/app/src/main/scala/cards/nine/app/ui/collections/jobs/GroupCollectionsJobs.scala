package cards.nine.app.ui.collections.jobs

import android.content.Intent
import android.graphics.Bitmap
import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.jobs.uiactions.{GroupCollectionsUiActions, NavigationUiActions, ScrollType, ToolbarUiActions}
import cards.nine.app.ui.commons.action_filters.MomentReloadedActionFilter
import cards.nine.app.ui.commons.{BroadAction, JobException, Jobs, RequestCodes}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Card._
import cards.nine.models.types._
import cards.nine.models.{Card, CardData, Collection}
import cats.implicits._
import macroid.ActivityContextWrapper

class GroupCollectionsJobs(
  val groupCollectionsUiActions: GroupCollectionsUiActions,
  val toolbarUiActions: ToolbarUiActions,
  val navigationUiActions: NavigationUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with AppNineCardsIntentConversions { self =>

  val delay = 200

  var collections: Seq[Collection] = Seq.empty

  def initialize(initialColor: Int, icon: String, position: Int, isStateChanged: Boolean): TaskService[Unit] = {
    for {
      _ <- toolbarUiActions.initialize(initialColor, icon, isStateChanged)
      theme <- getThemeTask
      _ <- TaskService.right(statuses = statuses.copy(theme = theme))
      _ <- groupCollectionsUiActions.initialize()
      collections <- di.collectionProcess.getCollections
      _ <- groupCollectionsUiActions.showCollections(collections, position)
    } yield ()
  }

  def resume(): TaskService[Unit] = di.observerRegister.registerObserverTask()

  def pause(): TaskService[Unit] = di.observerRegister.unregisterObserverTask()

  def back(): TaskService[Unit] = groupCollectionsUiActions.back()

  def destroy(): TaskService[Unit] = groupCollectionsUiActions.destroy()

  def resetAction(): TaskService[Unit] = groupCollectionsUiActions.resetAction

  def destroyAction(): TaskService[Unit] = groupCollectionsUiActions.destroyAction

  def reloadCards(): TaskService[Seq[Card]] =
    for {
      currentCollection <- fetchCurrentCollection
      databaseCollection <- di.collectionProcess.getCollectionById(currentCollection.id)
        .resolveOption(s"Can't find the collection with id ${currentCollection.id}")
      cardsAreDifferent = databaseCollection.cards != currentCollection.cards
      _ <- groupCollectionsUiActions.reloadCards(databaseCollection.cards).resolveIf(cardsAreDifferent, ())
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
          closeEditingMode() *> groupCollectionsUiActions.editCard(currentCollectionId, head.id, head.term)
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
      _ <- groupCollectionsUiActions.removeCards(cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment, ())
    } yield cards

  def moveToCollection(toCollectionId: Int, collectionPosition: Int): TaskService[Seq[Card]] =
    for {
      currentCollection <- fetchCurrentCollection
      toCollection <- groupCollectionsUiActions.getCollection(collectionPosition)
        .resolveOption(s"Can't find the collection in the position $collectionPosition in the UI")
      currentCollectionId = currentCollection.id
      cards = filterSelectedCards(currentCollection.cards)
      otherIsMoment = toCollection.collectionType == MomentCollectionType
      _ <- closeEditingMode()
      // TODO We must to create a new methods for moving cards to collection in #828
      // We should change this calls when the method will be ready
      _ <- di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id))
      _ <- di.collectionProcess.addCards(toCollectionId, cards map (_.toData))
      _ <- groupCollectionsUiActions.removeCards(cards)
      _ <- groupCollectionsUiActions.addCardsToCollection(collectionPosition, cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment || otherIsMoment, ())
    } yield cards

  def savePublishStatus(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      _ <- TaskService.right(statuses = statuses.copy(publishStatus = currentCollection.publicCollectionStatus))
    } yield ()

  def performCard(card : Card, position: Int): TaskService[Unit] = {

    def sendTrackEvent() = {
      val packageName = card.packageName getOrElse ""
      val maybeCollection = groupCollectionsUiActions.dom.getCurrentCollection

      def trackMomentIfNecessary(collectionId: Option[Int]) = collectionId match {
        case Some(id) =>
          for {
            maybeMoment <- di.momentProcess.getMomentByCollectionId(id)
            _  <- maybeMoment match {
              case Some(moment) => di.trackEventProcess.openAppFromCollection(packageName, MomentCategory(moment.momentType))
              case _ => TaskService.empty
            }
          } yield ()
        case _ => TaskService.empty
      }

      for {
        _ <- maybeCollection flatMap (_.appsCategory) match {
          case Some(category) => di.trackEventProcess.openAppFromCollection(packageName, AppCategory(category))
          case _ => TaskService.empty
        }
        _ <- trackMomentIfNecessary(maybeCollection.map(_.id))
      } yield ()
    }

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
          groupCollectionsUiActions.reloadItemCollection(statuses.getPositionsSelected, position)
        }
      case NormalCollectionMode => di.launcherExecutorProcess.execute(card.intent) *> sendTrackEvent()

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
              groupCollectionsUiActions.showNoPhoneCallPermissionError()
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
      _ <- groupCollectionsUiActions.addCards(cards)
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
      _ <- groupCollectionsUiActions.addCards(cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action)).resolveIf(currentIsMoment, ())
    } yield cards
  }

  def openReorderMode(current: ScrollType, canScroll: Boolean): TaskService[Unit] =
    for {
      _ <- statuses.collectionMode match {
        case EditingCollectionMode => groupCollectionsUiActions.closeEditingModeUi()
        case _ => TaskService.right(statuses = statuses.copy(collectionMode = EditingCollectionMode))
      }
      _ <- groupCollectionsUiActions.openReorderModeUi(current, canScroll)
    } yield ()


  def closeReorderMode(position: Int): TaskService[Unit] = {
    statuses = statuses.copy(positionsEditing = Set(position))
    groupCollectionsUiActions.startEditing(statuses.getPositionsSelected)
  }

  def closeEditingMode(): TaskService[Unit] = {
    statuses = statuses.copy(collectionMode = NormalCollectionMode, positionsEditing = Set.empty)
    groupCollectionsUiActions.closeEditingModeUi()
  }

  def emptyCollection(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      _ <- groupCollectionsUiActions.showMenuButton(autoHide = false, indexColor = currentCollection.themedColorIndex)
    } yield ()

  def firstItemInCollection(): TaskService[Unit] = groupCollectionsUiActions.hideMenuButton()

  def close(): TaskService[Unit] = groupCollectionsUiActions.close()

  def showMenu(openMenu: Boolean = false): TaskService[Unit] =
    for {
      currentCollection <-  fetchCurrentCollection
      _ <- groupCollectionsUiActions.showMenuButton(autoHide = true, openMenu = openMenu, currentCollection.themedColorIndex)
    } yield ()

  def showGenericError(): TaskService[Unit] = groupCollectionsUiActions.showContactUsError

  private[this] def filterSelectedCards(cards: Seq[Card]): Seq[Card] = cards.zipWithIndex flatMap {
    case (card, index) if statuses.positionsEditing.contains(index) => Option(card)
    case _ => None
  }

  private[this] def fetchCurrentCollection: TaskService[Collection] =
    groupCollectionsUiActions.getCurrentCollection.resolveOption("Can't find the current collection in the UI")

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