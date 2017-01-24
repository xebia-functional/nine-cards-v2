/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.launcher.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.action_filters.MomentReloadedActionFilter
import cards.nine.app.ui.commons.{BroadAction, JobException, Jobs}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData}
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.app.ui.launcher.{AddItemMode, ReorderMode}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.ops.SeqOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Application.ApplicationDataOps
import cards.nine.models._
import cards.nine.models.types._
import cats.implicits._
import macroid.ActivityContextWrapper

class DragJobs(
    val mainAppDrawerUiActions: AppDrawerUiActions,
    val navigationUiActions: NavigationUiActions,
    val dockAppsUiActions: DockAppsUiActions,
    val workspaceUiActions: WorkspaceUiActions,
    val dragUiActions: DragUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
    extends Jobs
    with Conversions {

  def startAddItemToCollection(app: ApplicationData): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.addAppToCollection(app.packageName)
      _ <- startAddItemToCollection(app.toCardData)
    } yield ()

  def startAddItemToCollection(contact: Contact): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.addContactToCollection()
      _ <- startAddItemToCollection(toCardData(contact))
    } yield ()

  def startAddItemToCollection(dockAppData: DockAppData): TaskService[Unit] =
    toCardData(dockAppData) match {
      case Some(cardType) =>
        for {
          _ <- di.deviceProcess.deleteDockAppByPosition(dockAppData.position)
          _ <- startAddItemToCollectionFromDockApps(cardType)
        } yield ()
      case _ => TaskService.left(JobException("Dock type unsupported"))
    }

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
    val collectionTasks =
      (dragUiActions.dom.getCollection(statuses.currentDraggingPosition), statuses.cardAddItemMode) match {
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

  def changePositionDockApp(from: Int, to: Int): TaskService[Unit] = {
    for {
      dockApps <- di.deviceProcess.getDockApps
      maybeDockAppFrom = dockApps find (_.position == from)
      _ <- maybeDockAppFrom match {
        case Some(dockApp) =>
          di.deviceProcess.createOrUpdateDockApp(
            dockApp.name,
            dockApp.dockType,
            dockApp.intent,
            dockApp.imagePath,
            to) *>
            dockAppsUiActions.reloadDockApps(dockApp.toData.copy(position = to))
        case _ => TaskService.empty
      }
    } yield ()
  }

  def endAddItemToDockApp(position: Int): TaskService[Unit] = {

    def createOrUpdateDockApp(card: CardData, dockType: DockType) =
      for {
        _ <- di.deviceProcess.createOrUpdateDockApp(
          card.term,
          dockType,
          card.intent,
          card.imagePath getOrElse "",
          position)
        _ <- dockAppsUiActions.reloadDockApps(
          DockAppData(card.term, dockType, card.intent, card.imagePath getOrElse "", position))
      } yield ()

    val dockAppsTasks = statuses.cardAddItemMode match {
      case Some(card: CardData) =>
        card.cardType match {
          case AppCardType     => createOrUpdateDockApp(card, AppDockType)
          case ContactCardType => createOrUpdateDockApp(card, ContactDockType)
          case _               => navigationUiActions.showContactUsError()
        }
      case _ => navigationUiActions.showContactUsError()
    }
    for {
      _ <- dockAppsTasks
      _ <- TaskService.right(statuses = statuses.reset())
      _ <- dragUiActions.endAddItem()
    } yield ()
  }

  def endAddItem(): TaskService[Unit] =
    if (statuses.mode == AddItemMode) {
      statuses = statuses.reset()
      dragUiActions.endAddItem()
    } else {
      TaskService.empty
    }

  def uninstallInAddItem(): TaskService[Unit] = {
    val launchTask = statuses.cardAddItemMode match {
      case Some(card: CardData) if card.cardType == AppCardType =>
        card.packageName match {
          case Some(packageName) =>
            di.launcherExecutorProcess.launchUninstall(packageName)
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
          case Some(packageName) =>
            di.launcherExecutorProcess.launchSettings(packageName)
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

  def dropReorder(): TaskService[Unit] =
    if (statuses.mode == ReorderMode) {

      def reorderPositions() = {
        val from = statuses.startPositionReorderMode
        val to   = statuses.currentDraggingPosition
        if (from != to) {
          for {
            _ <- di.trackEventProcess.reorderCollection()
            _ <- di.collectionProcess.reorderCollection(from, to)
            _ <- workspaceUiActions.reloadWorkspaces(reorderCollectionsInCurrentData(from, to))
          } yield ()
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

  def dropReorderException() =
    workspaceUiActions.reloadWorkspaces(reloadCollectionsInCurrentData) *>
      navigationUiActions.showContactUsError()

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

  private[this] def startAddItemToCollection(card: CardData): TaskService[Unit] = {
    statuses = statuses.startAddItem(card)
    for {
      _ <- mainAppDrawerUiActions.close()
      _ <- navigationUiActions
        .goToCollectionWorkspace()
        .resolveIf(!mainAppDrawerUiActions.dom.isCollectionWorkspace, ())
      _ <- dragUiActions.startAddItem(card.cardType)
    } yield ()
  }

  private[this] def startAddItemToCollectionFromDockApps(card: CardData): TaskService[Unit] = {
    statuses = statuses.startAddItem(card)
    for {
      _ <- navigationUiActions
        .goToCollectionWorkspace()
        .resolveIf(!mainAppDrawerUiActions.dom.isCollectionWorkspace, ())
      _ <- dragUiActions.startAddItemFromDockApp(card.cardType)
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

  private[this] def createLauncherDataCollections(
      collections: Seq[Collection]): Seq[LauncherData] = {
    collections.grouped(numSpaces).toList.zipWithIndex map {
      case (data, index) =>
        LauncherData(CollectionsWorkSpace, collections = data, positionByType = index)
    }
  }

}
