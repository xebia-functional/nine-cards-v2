package cards.nine.app.ui.launcher.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.JobException
import cards.nine.app.ui.launcher.{NormalMode, AddItemMode}
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.DockAppTestData
import cards.nine.models.types.{CollectionDockType, ContactDockType, ContactCardType, AppCardType}
import cards.nine.process.device.DeviceProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.app.ui.launcher.LauncherActivity._

trait DragJobsSpecification extends TaskServiceSpecification
  with Mockito {

  trait DragJobsScope
    extends Scope
      with LauncherTestData
      with DockAppTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockLauncherDOM = mock[LauncherDOM]

    val mockAppDrawerUiActions = mock[AppDrawerUiActions]

    mockAppDrawerUiActions.dom returns mockLauncherDOM

    val mockNavigationUiActions = mock[NavigationUiActions]

    val mockDockAppsUiActions = mock[DockAppsUiActions]

    val mockWorkspaceUiActions = mock[WorkspaceUiActions]

    val mockDragUiActions = mock[DragUiActions]

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val dragJobs = new DragJobs(mockAppDrawerUiActions, mockNavigationUiActions, mockDockAppsUiActions, mockWorkspaceUiActions, mockDragUiActions)(contextWrapper) {
      override lazy val di: Injector = mockInjector

    }
  }

}

class DragJobsSpec
  extends DragJobsSpecification {

  sequential
  "startAddItemToCollection" should {

    "Added an Application to Collection when is collection in workspace" in new DragJobsScope {

      mockAppDrawerUiActions.close() returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns true
      mockDragUiActions.startAddItem(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(applicationData).mustRightUnit

      there was one(mockAppDrawerUiActions).close()
      there was one(mockDragUiActions).startAddItem(AppCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }

    "Added an Application to Collection when isn't collection in workspace" in new DragJobsScope {

      mockAppDrawerUiActions.close() returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns false
      mockNavigationUiActions.goToCollectionWorkspace() returns serviceRight(Unit)
      mockDragUiActions.startAddItem(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(applicationData).mustRightUnit

      there was one(mockAppDrawerUiActions).close()
      there was one(mockDragUiActions).startAddItem(AppCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }
  }

  "startAddItemToCollection" should {
    "Added a Contact to Collection when is collection in workspace" in new DragJobsScope {

      mockAppDrawerUiActions.close() returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns true
      mockDragUiActions.startAddItem(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(contact).mustRightUnit

      there was one(mockAppDrawerUiActions).close()
      there was one(mockDragUiActions).startAddItem(ContactCardType)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }

    "Added an Contact to Collection when isn't collection in workspace" in new DragJobsScope {

      mockAppDrawerUiActions.close() returns serviceRight(Unit)
      mockLauncherDOM.isCollectionWorkspace returns false
      mockNavigationUiActions.goToCollectionWorkspace() returns serviceRight(Unit)
      mockDragUiActions.startAddItem(any) returns serviceRight(Unit)

      dragJobs.startAddItemToCollection(contact).mustRightUnit

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

  "endAddItemToCollection" should {
    "" in new DragJobsScope {

    }
  }
  "changePositionDockApp" should {
    "" in new DragJobsScope {

    }
  }
  "endAddItemToDockApp" should {
    "" in new DragJobsScope {

    }
  }

  sequential
  "endAddItem" should {
    "call to endAddItem if statuses is AddItemMode" in new DragJobsScope {

      statuses.copy(mode = AddItemMode)
      mockDragUiActions.endAddItem() returns serviceRight(Unit)

      dragJobs.endAddItem().mustRightUnit

      there was one(mockDragUiActions).endAddItem()
    }

    "Does nothing if statuses is different the AddItemMode" in new DragJobsScope {

      statuses.copy(mode = NormalMode)
      dragJobs.endAddItem().mustRightUnit
      there was no(mockDragUiActions).endAddItem()
    }
  }

}