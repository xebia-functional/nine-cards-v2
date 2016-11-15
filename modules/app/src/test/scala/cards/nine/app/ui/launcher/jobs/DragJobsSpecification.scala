package cards.nine.app.ui.launcher.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.{BroadAction, JobException}
import cards.nine.app.ui.launcher.{ReorderMode, NormalMode, AddItemMode}
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{CollectionTestData, DockAppTestData}
import cards.nine.models.DockAppData
import cards.nine.models.types._
import cards.nine.process.collection.CollectionProcess
import cards.nine.process.device.DeviceProcess
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.app.ui.launcher.LauncherActivity._

trait DragJobsSpecification extends TaskServiceSpecification
  with Mockito {

  trait DragJobsScope
    extends Scope
      with LauncherTestData
      with DockAppTestData
      with CollectionTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockLauncherDOM = mock[LauncherDOM]

    val mockAppDrawerUiActions = mock[AppDrawerUiActions]

    mockAppDrawerUiActions.dom returns mockLauncherDOM

    val mockNavigationUiActions = mock[NavigationUiActions]

    val mockDockAppsUiActions = mock[DockAppsUiActions]

    val mockWorkspaceUiActions = mock[WorkspaceUiActions]

    val mockDragUiActions = mock[DragUiActions]

    mockDragUiActions.dom returns mockLauncherDOM

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val dragJobs = new DragJobs(mockAppDrawerUiActions, mockNavigationUiActions, mockDockAppsUiActions, mockWorkspaceUiActions, mockDragUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def sendBroadCastTask(broadAction: BroadAction) = TaskService.empty

    }
  }

}

class DragJobsSpec
  extends DragJobsSpecification {

  sequential
  "startAddItemToCollection" should {

    "Added an Application to Collection when is collection in workspace" in new DragJobsScope {

      mockTrackEventProcess.addAppToCollection(any) returns serviceRight(Unit)
      mockAppDrawerUiActions.close() returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns true
      mockDragUiActions.startAddItem(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(applicationData).mustRightUnit

      there was one(mockTrackEventProcess).addAppToCollection(applicationData.packageName)
      there was one(mockAppDrawerUiActions).close()
      there was one(mockDragUiActions).startAddItem(AppCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }

    "Added an Application to Collection when isn't collection in workspace" in new DragJobsScope {

      mockTrackEventProcess.addAppToCollection(any) returns serviceRight(Unit)
      mockAppDrawerUiActions.close() returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns false
      mockNavigationUiActions.goToCollectionWorkspace() returns serviceRight(Unit)
      mockDragUiActions.startAddItem(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(applicationData).mustRightUnit

      there was one(mockTrackEventProcess).addAppToCollection(applicationData.packageName)
      there was one(mockAppDrawerUiActions).close()
      there was one(mockDragUiActions).startAddItem(AppCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }
  }

  "startAddItemToCollection" should {
    "Added a Contact to Collection when is collection in workspace" in new DragJobsScope {

      mockTrackEventProcess.addContactToCollection() returns serviceRight(Unit)
      mockAppDrawerUiActions.close() returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns true
      mockDragUiActions.startAddItem(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(contact).mustRightUnit

      there was one(mockTrackEventProcess).addContactToCollection()
      there was one(mockAppDrawerUiActions).close()
      there was one(mockDragUiActions).startAddItem(ContactCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }

    "Added an Contact to Collection when isn't collection in workspace" in new DragJobsScope {

      mockTrackEventProcess.addContactToCollection() returns serviceRight(Unit)
      mockAppDrawerUiActions.close() returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns false
      mockNavigationUiActions.goToCollectionWorkspace() returns serviceRight(Unit)
      mockDragUiActions.startAddItem(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(contact).mustRightUnit

      there was one(mockTrackEventProcess).addContactToCollection()
      there was one(mockAppDrawerUiActions).close()
      there was one(mockDragUiActions).startAddItem(ContactCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }
  }

  "startAddItemToCollection" should {
    "Added a DockApp with DockType equal AppDockType to Collection" in new DragJobsScope {

      mockDeviceProcess.deleteDockAppByPosition(any) returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns true
      mockDragUiActions.startAddItemFromDockApp(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(dockAppData).mustRightUnit

      there was one(mockDeviceProcess).deleteDockAppByPosition(dockAppData.position)
      there was one(mockDragUiActions).startAddItemFromDockApp(AppCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }

    "Added a DockApp with DockType equal ContactDockType to Collection" in new DragJobsScope {

      mockDeviceProcess.deleteDockAppByPosition(any) returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns true
      mockDragUiActions.startAddItemFromDockApp(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(dockAppData.copy(dockType = ContactDockType)).mustRightUnit

      there was one(mockDeviceProcess).deleteDockAppByPosition(dockAppData.position)
      there was one(mockDragUiActions).startAddItemFromDockApp(ContactCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }

    "returns a JobException when added a DockApp with DockType equal CollectionDockType to Collection" in new DragJobsScope {

      dragJobs.startAddItemToCollection(dockAppData.copy(dockType = CollectionDockType)).mustLeft[JobException]

      there was no(mockDeviceProcess).deleteDockAppByPosition(dockAppData.position)
      there was no(mockDragUiActions).startAddItemFromDockApp(ContactCardType)
      there was no(mockNavigationUiActions).goToCollectionWorkspace()
    }
  }

  "draggingAddItemTo" should {
    "Update the position in statuses" in new DragJobsScope {

      dragJobs.draggingAddItemTo(position).mustRightUnit
      statuses.currentDraggingPosition shouldEqual position
    }
  }

  "draggingAddItemToPreviousScreen" should {
    "call goToPreviousScreenAddingItem and update current position in statuses" in new DragJobsScope {

      mockDragUiActions.goToPreviousScreenAddingItem() returns serviceRight(Unit)
      dragJobs.draggingAddItemToPreviousScreen(position).mustRightUnit
      there was one(mockDragUiActions).goToPreviousScreenAddingItem()
    }
  }

  "draggingAddItemToNextScreen" should {
    "call goToNextScreenAddingItem and update current position in statuses" in new DragJobsScope {

      mockDragUiActions.goToNextScreenAddingItem() returns serviceRight(Unit)
      dragJobs.draggingAddItemToNextScreen(position).mustRightUnit
      there was one(mockDragUiActions).goToNextScreenAddingItem()
    }
  }
  sequential
  "endAddItemToCollection" should {
    "call addCard when has card and collection" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData))
      mockLauncherDOM.getCollection(any) returns Option(collection)
      mockCollectionProcess.addCards(any, any) returns serviceRight(seqCard)
      mockNavigationUiActions.showAddItemMessage(any) returns serviceRight(Unit)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.endAddItemToCollection().mustRightUnit

      there was one(mockCollectionProcess).addCards(collection.id, Seq(cardData))
      there was one(mockNavigationUiActions).showAddItemMessage(collection.name)
      there was one(mockDragUiActions).endAddItem()
    }

    "Does nothing if hasn't card and collection" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = None)
      mockLauncherDOM.getCollection(any) returns None
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.endAddItemToCollection().mustRightUnit

      there was one(mockDragUiActions).endAddItem()
    }
  }
  "changePositionDockApp" should {
    "call to createOrUpdateDockApp when found a dockApps with from position" in new DragJobsScope {

      mockDeviceProcess.getDockApps returns serviceRight(seqDockApp)
      mockDeviceProcess.createOrUpdateDockApp(any, any, any, any, any) returns serviceRight(Unit)
      mockDockAppsUiActions.reloadDockApps(any) returns serviceRight(Unit)

      dragJobs.changePositionDockApp(positionFrom, positionTo).mustRightUnit
      there was one(mockDeviceProcess).getDockApps
      there was one(mockDeviceProcess).createOrUpdateDockApp(dockApp.name, dockApp.dockType, dockApp.intent, dockApp.imagePath, positionTo)
      there was one(mockDockAppsUiActions).reloadDockApps(dockApp.toData.copy(position = positionTo))
    }

    "returns an Unit when not found the position" in new DragJobsScope {

      mockDeviceProcess.getDockApps returns serviceRight(seqDockApp)
      dragJobs.changePositionDockApp(positionFromNoExist, positionTo).mustRightUnit
      there was one(mockDeviceProcess).getDockApps
    }
  }

  sequential
  "endAddItemToDockApp" should {
    "call to createORUpdateDockApp when statuses has a cardAddItemMode, this case AppCardType" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = AppCardType)))
      mockDeviceProcess.createOrUpdateDockApp(any, any, any, any, any) returns serviceRight(Unit)
      mockDockAppsUiActions.reloadDockApps(any) returns serviceRight(Unit)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.endAddItemToDockApp(position).mustRightUnit

      there was one(mockDeviceProcess).createOrUpdateDockApp(cardData.term, AppDockType, cardData.intent, cardData.imagePath getOrElse "", position)
      there was one(mockDockAppsUiActions).reloadDockApps(DockAppData(cardData.term, AppDockType, cardData.intent, cardData.imagePath getOrElse "", position))
      there was one(mockDragUiActions).endAddItem()
    }

    "call to createORUpdateDockApp when statuses has a cardAddItemMode, this case AppCardType" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = ContactCardType)))
      mockDeviceProcess.createOrUpdateDockApp(any, any, any, any, any) returns serviceRight(Unit)
      mockDockAppsUiActions.reloadDockApps(any) returns serviceRight(Unit)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.endAddItemToDockApp(position).mustRightUnit

      there was one(mockDeviceProcess).createOrUpdateDockApp(cardData.term, ContactDockType, cardData.intent, cardData.imagePath getOrElse "", position)
      there was one(mockDockAppsUiActions).reloadDockApps(DockAppData(cardData.term, ContactDockType, cardData.intent, cardData.imagePath getOrElse "", position))
      there was one(mockDragUiActions).endAddItem()
    }

    "show a message error if has a cardAddItemMode but it's different the AppCardType or ContactCardType" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = EmailCardType)))
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.endAddItemToDockApp(position).mustRightUnit

      there was one(mockNavigationUiActions).showContactUsError()
    }

    "show a message error if hasn't a cardAddItemMode" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = None)
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.endAddItemToDockApp(position).mustRightUnit

      there was one(mockNavigationUiActions).showContactUsError()
    }
  }

  sequential
  "endAddItem" should {
    "call to endAddItem if statuses is AddItemMode" in new DragJobsScope {

      statuses = statuses.copy(mode = AddItemMode)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.endAddItem().mustRightUnit

      there was one(mockDragUiActions).endAddItem()
    }

    "Does nothing if statuses is different the AddItemMode" in new DragJobsScope {

      statuses = statuses.copy(mode = NormalMode)
      dragJobs.endAddItem().mustRightUnit
      there was no(mockDragUiActions).endAddItem()
    }
  }

  sequential
  "uninstallInAddItem" should {
    "call to launchUninstall when statuses has carData and cardType is equal AppCardType and has a packagename" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = AppCardType)))
      mockLauncherExecutorProcess.launchUninstall(any)(any) returns serviceRight(Unit)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.uninstallInAddItem().mustRightUnit

      there was one(mockLauncherExecutorProcess).launchUninstall(===(cardData.packageName.getOrElse("")))(any)
      there was one(mockDragUiActions).endAddItem()
    }

    "Does nothing when statuses has carData and cardType is equal AppCardType and hasn't a packagename" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = AppCardType, packageName = None)))
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.uninstallInAddItem().mustRightUnit

      there was no(mockLauncherExecutorProcess).launchUninstall(any)(any)
      there was one(mockDragUiActions).endAddItem()
    }

    "Does nothing when statuses has carData and cardType is different to AppCardType " in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = SmsCardType)))
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.uninstallInAddItem().mustRightUnit

      there was no(mockLauncherExecutorProcess).launchUninstall(any)(any)
      there was one(mockDragUiActions).endAddItem()
    }


    "Does nothing when statuses hasn't carData " in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = None)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.uninstallInAddItem().mustRightUnit

      there was no(mockLauncherExecutorProcess).launchUninstall(any)(any)
      there was one(mockDragUiActions).endAddItem()
    }

  }


  sequential
  "settingsInAddItem" should {
    "call to launchSettings when statuses has carData and cardType is equal AppCardType and has a packagename" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = AppCardType)))
      mockLauncherExecutorProcess.launchSettings(any)(any) returns serviceRight(Unit)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.settingsInAddItem().mustRightUnit

      there was one(mockLauncherExecutorProcess).launchSettings(===(cardData.packageName.getOrElse("")))(any)
      there was one(mockDragUiActions).endAddItem()
    }

    "Does nothing when statuses has carData and cardType is equal AppCardType and hasn't a packagename" in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = AppCardType, packageName = None)))
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.settingsInAddItem().mustRightUnit

      there was no(mockLauncherExecutorProcess).launchSettings(any)(any)
      there was one(mockDragUiActions).endAddItem()
    }

    "Does nothing when statuses has carData and cardType is different to AppCardType " in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = Option(cardData.copy(cardType = SmsCardType)))
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.settingsInAddItem().mustRightUnit

      there was no(mockLauncherExecutorProcess).launchSettings(any)(any)
      there was one(mockDragUiActions).endAddItem()
    }


    "Does nothing when statuses hasn't carData " in new DragJobsScope {

      statuses = statuses.copy(cardAddItemMode = None)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.settingsInAddItem().mustRightUnit

      there was no(mockLauncherExecutorProcess).launchSettings(any)(any)
      there was one(mockDragUiActions).endAddItem()
    }
  }
  sequential
  "startReorder" should {
    "update statuses if has a Collection " in new DragJobsScope {

      mockDragUiActions.startReorder() returns serviceRight(Unit)

      dragJobs.startReorder(Option(collection), position).mustRightUnit

      there was one(mockDragUiActions).startReorder()
      statuses.startPositionReorderMode equals position
      statuses.currentDraggingPosition equals position
      statuses.collectionReorderMode equals Some(collection)
      statuses.mode equals ReorderMode
    }

    "show a message error if hasn't a Collection " in new DragJobsScope {

      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      dragJobs.startReorder(None, position).mustRightUnit

      there was no(mockDragUiActions).startReorder()
      there was one(mockNavigationUiActions).showContactUsError()
    }
  }

  sequential
  "draggingReorderTo" should {
    "update current position in statuses" in new DragJobsScope {

      dragJobs.draggingReorderTo(position).mustRightUnit
      statuses.currentDraggingPosition equals position
    }
  }

  sequential
  "draggingReorderToNextScreen" should {
    "update current position in statuses" in new DragJobsScope {

      mockDragUiActions.goToNextScreenReordering() returns serviceRight(Unit)

      dragJobs.draggingReorderToNextScreen(position).mustRightUnit

      there was one(mockDragUiActions).goToNextScreenReordering()
      statuses.currentDraggingPosition equals position
    }
  }

  sequential
  "draggingReorderToPreviousScreen" should {
    "update current position in statuses" in new DragJobsScope {

      mockDragUiActions.goToPreviousScreenReordering() returns serviceRight(Unit)

      dragJobs.draggingReorderToPreviousScreen(position).mustRightUnit

      there was one(mockDragUiActions).goToPreviousScreenReordering()
      statuses.currentDraggingPosition equals position
    }
  }

  sequential
  "dropReorder" should {
    "call reorderCollection if startPositionReorderMode is different to currentDraggingPosition and statuses.mode is ReorderMode" in new DragJobsScope {

      statuses = statuses.copy(mode = ReorderMode, startPositionReorderMode = positionFrom, currentDraggingPosition = positionTo)
      mockTrackEventProcess.reorderCollection() returns serviceRight(Unit)
      mockDragUiActions.endReorder() returns serviceRight(Unit)
      mockCollectionProcess.reorderCollection(any, any) returns serviceRight(Unit)
      mockWorkspaceUiActions.reloadWorkspaces(any,any) returns serviceRight(Unit)
      mockLauncherDOM.getData returns seqLauncherData

      dragJobs.dropReorder().mustRightUnit

      there was one(mockTrackEventProcess).reorderCollection()
      there was one(mockDragUiActions).endReorder()
      there was one(mockCollectionProcess).reorderCollection(positionFrom, positionTo)
    }

    "call reloadWorkspaces if startPositionReorderMode is equal to currentDraggingPosition and statuses.mode is ReorderMode" in new DragJobsScope {

      statuses = statuses.copy(mode = ReorderMode, startPositionReorderMode = positionFrom, currentDraggingPosition = positionFrom)
      mockTrackEventProcess.reorderCollection() returns serviceRight(Unit)
      mockDragUiActions.endReorder() returns serviceRight(Unit)
      mockWorkspaceUiActions.reloadWorkspaces(any,any) returns serviceRight(Unit)
      mockLauncherDOM.getData returns seqLauncherData

      dragJobs.dropReorder().mustRightUnit

      there was no(mockTrackEventProcess).reorderCollection()
      there was one(mockDragUiActions).endReorder()
    }

    "call reloadWorkspaces if startPositionReorderMode is equal to currentDraggingPosition and statuses.mode is ReorderMode" in new DragJobsScope {

      statuses = statuses.copy(mode = ReorderMode, startPositionReorderMode = positionFrom, currentDraggingPosition = positionFrom)
      mockTrackEventProcess.reorderCollection() returns serviceRight(Unit)
      mockDragUiActions.endReorder() returns serviceRight(Unit)
      mockWorkspaceUiActions.reloadWorkspaces(any,any) returns serviceRight(Unit)
      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = seqCollection))

      dragJobs.dropReorder().mustRightUnit

      there was no(mockTrackEventProcess).reorderCollection()
      there was one(mockDragUiActions).endReorder()
    }

    "Does nothing if  statuses.mode isn't ReorderMode" in new DragJobsScope {

      statuses = statuses.copy(mode = AddItemMode)
      dragJobs.dropReorder().mustRightUnit
    }
  }


  "dropReorderException" should {
    "call to reloadWorkspaces and showContactUsError" in new DragJobsScope {

      mockWorkspaceUiActions.reloadWorkspaces(any, any) returns serviceRight(Unit)
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)
      mockLauncherDOM.getData returns seqLauncherData

      dragJobs.dropReorderException()

      there was one(mockNavigationUiActions).showContactUsError()
    }
  }

  sequential
  "removeCollectionInReorderMode" should {
    "show a message for remove collection if has a collection and can remove" in new DragJobsScope {

      statuses = statuses.copy(collectionReorderMode = Option(collection))
      mockLauncherDOM.canRemoveCollections returns true
      mockNavigationUiActions.showDialogForRemoveCollection(any) returns serviceRight(Unit)

      dragJobs.removeCollectionInReorderMode().mustRightUnit

      there was one(mockNavigationUiActions).showDialogForRemoveCollection(collection)
      there was no(mockNavigationUiActions).showContactUsError()
    }

    "show a message if has a collection and can't remove" in new DragJobsScope {

      statuses = statuses.copy(collectionReorderMode = Option(collection))
      mockLauncherDOM.canRemoveCollections returns false
      mockNavigationUiActions.showMinimumOneCollectionMessage() returns serviceRight(Unit)

      dragJobs.removeCollectionInReorderMode().mustRightUnit

      there was one(mockNavigationUiActions).showMinimumOneCollectionMessage()
      there was no(mockNavigationUiActions).showContactUsError()
    }

    "show a message error if statuses hasn't collectionReorder" in new DragJobsScope {

      statuses = statuses.copy(collectionReorderMode = None)
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      dragJobs.removeCollectionInReorderMode().mustRightUnit

      there was one(mockNavigationUiActions).showContactUsError()
    }
  }
}