package cards.nine.app.ui.launcher.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.action_filters.MomentReloadedActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData}
import cards.nine.app.ui.launcher.{AddItemMode, ReorderMode}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.commons.ops.SeqOps._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.commons.services.TaskService
import cards.nine.models.types._
import cards.nine.models._
import macroid.{ActivityContextWrapper, Ui}

class DragJobs(
  val mainAppDrawerUiActions: MainAppDrawerUiActions,
  val navigationUiActions: NavigationUiActions,
  val dockAppsUiActions: DockAppsUiActions,
  val workspaceUiActions: WorkspaceUiActions,
  val dragUiActions: DragUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  def startAddItemToCollection(app: ApplicationData): TaskService[Unit] = startAddItemToCollection(toCardData(app))

  def startAddItemToCollection(contact: Contact): TaskService[Unit] = startAddItemToCollection(toCardData(contact))

  def draggingAddItemTo(position: Int): TaskService[Unit] = TaskService.right {
    statuses = statuses.updateCurrentPosition(position)
  }

  def draggingAddItemToPreviousScreen(position: Int): TaskService[Unit] =
    for {
      _ <- dragUiActions.goToPreviousScreenAddingItem()
      _ <- TaskService.right(statuses.updateCurrentPosition(position))
    } yield ()

  def draggingAddItemToNextScreen(position: Int): TaskService[Unit] =
    for {
      _ <- dragUiActions.goToNextScreenAddingItem()
      _ <- TaskService.right(statuses.updateCurrentPosition(position))
    } yield ()

  def endAddItemToCollection(): TaskService[Unit] = {
    val collectionTasks = (dragUiActions.dom.getCollection(statuses.currentDraggingPosition), statuses.cardAddItemMode) match {
      case (Some(collection: Collection), Some(card: CardData)) =>
        for {
          _ <- di.collectionProcess.addCards(collection.id, Seq(card))
          _ <- navigationUiActions.showAddItemMessage(collection.name)
          _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
        } yield ()
      case _ => TaskService.empty
    }
    for {
      _ <- collectionTasks
      _ <- TaskService.right(statuses = statuses.reset())
      _ <- dragUiActions.endAddItem()
    } yield ()
  }

  def endAddItemToDockApp(position: Int): TaskService[Unit] = {

    def createOrUpdateDockApp(card: CardData, dockType: DockType) =
      for {
        _ <- di.deviceProcess.createOrUpdateDockApp(card.term, dockType, card.intent, card.imagePath getOrElse "", position)
        _ <- dockAppsUiActions.reloadDockApps(DockAppData(card.term, dockType, card.intent, card.imagePath getOrElse "", position))
      } yield ()

    val dockAppsTasks = statuses.cardAddItemMode match {
      case Some(card: CardData) =>
        card.cardType match {
          case AppCardType => createOrUpdateDockApp(card, AppDockType)
          case ContactCardType => createOrUpdateDockApp(card, ContactDockType)
          case _ => navigationUiActions.showContactUsError()
        }
      case _ => navigationUiActions.showContactUsError()
    }
    for {
      _ <- dockAppsTasks
      _ <- TaskService.right(statuses = statuses.reset())
      _ <- dragUiActions.endAddItem()
    } yield ()
  }
  //actions.showContactUsError())

  def endAddItem(): TaskService[Unit] = if (statuses.mode == AddItemMode) {
    statuses = statuses.reset()
    dragUiActions.endAddItem()
  } else {
    TaskService.empty
  }

  def uninstallInAddItem(): TaskService[Unit] = {
    val launchTask = statuses.cardAddItemMode match {
      case Some(card: CardData) if card.cardType == AppCardType =>
        card.packageName match {
          case Some(packageName) => di.launcherExecutorProcess.launchUninstall(packageName)
          case _ => TaskService.empty
        }
      case _ => TaskService.empty
    }
    for {
      _ <- launchTask
      _ <- TaskService.right(statuses = statuses.reset())
      _ <- dragUiActions.endAddItem()
    } yield ()
  }

  def settingsInAddItem(): TaskService[Unit] = {
    val launchTask = statuses.cardAddItemMode match {
      case Some(card: CardData) if card.cardType == AppCardType =>
        card.packageName match {
          case Some(packageName) => di.launcherExecutorProcess.launchSettings(packageName)
          case _ => TaskService.empty
        }
      case _ => TaskService.empty
    }
    for {
      _ <- launchTask
      _ <- TaskService.right(statuses = statuses.reset())
      _ <- dragUiActions.endAddItem()
    } yield ()
  }

  def startReorder(maybeCollection: Option[Collection], position: Int): TaskService[Unit] =
    maybeCollection match {
      case Some(collection) =>
        for {
          _ <- TaskService.right(statuses = statuses.startReorder(collection, position))
          _ <- dragUiActions.startReorder()
        } yield ()
      case _ => navigationUiActions.showContactUsError()
    }

  def draggingReorderTo(position: Int): TaskService[Unit] = TaskService.right {
    statuses = statuses.updateCurrentPosition(position)
  }

  def draggingReorderToNextScreen(position: Int): TaskService[Unit] =
    for {
      _ <- dragUiActions.goToNextScreenReordering()
      _ <- TaskService.right(statuses = statuses.updateCurrentPosition(position))
    } yield ()

  def draggingReorderToPreviousScreen(position: Int): TaskService[Unit] =
    for {
      _ <- dragUiActions.goToPreviousScreenReordering()
      _ <- TaskService.right(statuses = statuses.updateCurrentPosition(position))
    } yield ()

  def dropReorder(): TaskService[Unit] = if (statuses.mode == ReorderMode) {

    def reorderPositions() = {
      val from = statuses.startPositionReorderMode
      val to = statuses.currentDraggingPosition
      if (from != to) {
        for {
          _ <- di.collectionProcess.reorderCollection(from, to)
          _ <- workspaceUiActions.reloadWorkspaces(reorderCollectionsInCurrentData(from, to))
        } yield ()
//        onException = (_) => {
//          val data = reloadCollectionsInCurrentData
//          actions.reloadWorkspaces(data) ~ actions.showContactUsError()
//        }
      } else {
        workspaceUiActions.reloadWorkspaces(reloadCollectionsInCurrentData)
      }
    }

    for {
      _ <- dragUiActions.endReorder()
      _ <- reorderPositions()
      _ <- TaskService.right(statuses = statuses.reset())
    } yield ()
  } else {
    TaskService.empty
  }

  def removeCollectionInReorderMode(): TaskService[Unit] =
    statuses.collectionReorderMode match {
      case Some(collection) =>
        if (dragUiActions.dom.canRemoveCollections) {
          navigationUiActions.showDialogForRemoveCollection(collection)
        } else {
          navigationUiActions.showMinimumOneCollectionMessage()
        }
      case _ => navigationUiActions.showContactUsError()
    }

  def editCollectionInReorderMode(): TaskService[Unit] =
    statuses.collectionReorderMode match {
      case Some(collection) =>
//        val view = dragUiActions.dom.collectionActionsPanel.leftActionView
//        val collectionMap = Map(collectionId -> collection.id.toString)
//        val bundle = dragUiActions.dom.createBundle(Option(view), theme.getIndexColor(collection.themedColorIndex), collectionMap)
        navigationUiActions.launchCreateOrCollection(null)
      case None => navigationUiActions.showContactUsError()
    }

  private[this] def startAddItemToCollection(card: CardData): TaskService[Unit] = {
    statuses = statuses.startAddItem(card)
    for {
      _ <- mainAppDrawerUiActions.close()
      _ <- navigationUiActions.goToCollectionWorkspace().resolveIf(!mainAppDrawerUiActions.dom.isCollectionWorkspace, ())
      _ <- dragUiActions.startAddItem(card.cardType)
    } yield ()
  }

  private[this] def reorderCollectionsInCurrentData(from: Int, to: Int): Seq[LauncherData] = {
    val cols = dragUiActions.dom.getData flatMap (_.collections)
    val collections = cols.reorder(from, to).zipWithIndex map {
      case (collection, index) => collection.copy(position = index)
    }
    createLauncherDataCollections(collections)
  }

  private[this] def reloadCollectionsInCurrentData: Seq[LauncherData] = {
    val collections = dragUiActions.dom.getData flatMap (_.collections)
    createLauncherDataCollections(collections)
  }

  private[this] def createLauncherDataCollections(collections: Seq[Collection]): Seq[LauncherData] = {
    collections.grouped(numSpaces).toList.zipWithIndex map {
      case (data, index) => LauncherData(CollectionsWorkSpace, collections = data, positionByType = index)
    }
  }

}
