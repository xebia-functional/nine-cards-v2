package cards.nine.app.ui.collections.jobs

import android.support.v7.widget.RecyclerView.ViewHolder
import cards.nine.app.commons.{AppNineCardIntentConversions, Conversions}
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.{JobException, Jobs}
import cards.nine.app.ui.preferences.commons.Theme
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.types._
import cards.nine.process.commons.models.{Card, Collection}
import cats.syntax.either._
import macroid.ActivityContextWrapper
import monix.eval.Task

class SingleCollectionJobs(
  animateCards: Boolean,
  maybeCollection: Option[Collection],
  actions: SingleCollectionUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
    with Conversions
    with AppNineCardIntentConversions { self =>

  def initialize(sType: ScrollType): TaskService[Unit] = {
    val canScroll = maybeCollection exists (_.cards.length > numSpaces)
    for {
      theme <- getThemeTask
      _ <- actions.loadTheme(theme)
      _ <- actions.updateStatus(canScroll, sType)
      _ <- maybeCollection match {
        case Some(collection) => actions.initialize(animateCards, collection)
        case _ => actions.showEmptyCollection()
      }
    } yield ()
  }

  def startReorderCards(holder: ViewHolder): TaskService[Unit] =
    for {
      pulling <- actions.isToolbarPulling
      _ <- actions.startReorder(holder).resolveIf(!pulling, ())
    } yield ()

  def reorderCard(collectionId: Int, cardId: Int, position: Int): TaskService[Unit] =
    for {
      _ <- di.collectionProcess.reorderCard(collectionId, cardId, position)
      _ <- actions.reloadCards()
    } yield ()

  def moveToCollection(): TaskService[Unit] =
    for {
      collections <- di.collectionProcess.getCollections
      _ <- actions.moveToCollection(collections)
    } yield ()

  def addCards(cards: Seq[Card]): TaskService[Unit] =
    for {
      _ <- trackCards(cards, AddedToCollectionAction)
      _ <- actions.addCards(cards)
    } yield ()

  def removeCards(cards: Seq[Card]): TaskService[Unit] =
    for {
      _ <- trackCards(cards, RemovedFromCollectionAction)
      _ <- actions.removeCards(cards)
    } yield ()

  def reloadCards(cards: Seq[Card]): TaskService[Unit] = actions.reloadCards(cards)

  def bindAnimatedAdapter(): TaskService[Unit] = maybeCollection match {
    case Some(collection) => actions.bindAnimatedAdapter(animateCards, collection)
    case _ => TaskService.left(JobException("Collection not found"))
  }

  def saveEditedCard(collectionId: Int, cardId: Int, cardName: Option[String]): TaskService[Unit] =
    cardName match {
      case Some(name) if name.length > 0 =>
        for {
          card <- di.collectionProcess.editCard(collectionId, cardId, name)
          _ <- actions.reloadCard(card)
        } yield ()
      case _ => actions.showMessageFormFieldError
    }

  def showData(): TaskService[Unit] = maybeCollection match  {
    case Some(collection) => actions.showData(collection.cards.isEmpty)
    case _ => TaskService.left(JobException("Collection not found"))
  }

  def updateScroll(scrollY: Int): TaskService[Unit] = actions.updateVerticalScroll(scrollY)

  def setScrollType(scrollType: ScrollType): TaskService[Unit] = actions.scrollType(scrollType)

  def showGenericError(): TaskService[Unit] = actions.showContactUsError()

  private[this] def trackCards(cards: Seq[Card], action: Action): TaskService[Unit] = TaskService {
    val tasks = cards map { card =>
      trackCard(card, action).value
    }
    Task.gatherUnordered(tasks) map (_ => Either.right(()))
  }

  private[this] def trackCard(card: Card, action: Action): TaskService[Unit] = card.cardType match {
    case AppCardType =>
      for {
        collection <- actions.getCurrentCollection.resolveOption()
        maybeCategory = collection.appsCategory map (c => Option(AppCategory(c))) getOrElse {
          collection.moment flatMap (_.momentType) map MomentCategory
        }
        _ <- (action, card.packageName, maybeCategory) match {
          case (OpenCardAction, Some(packageName), Some(category)) =>
            di.trackEventProcess.openAppFromCollection(packageName, category)
          case (AddedToCollectionAction, Some(packageName), Some(category)) =>
            di.trackEventProcess.addAppToCollection(packageName, category)
          case (RemovedFromCollectionAction, Some(packageName), Some(category)) =>
            di.trackEventProcess.removeFromCollection(packageName, category)
          case _ => TaskService.empty
        }
      } yield ()
    case _ => TaskService.empty
  }

}