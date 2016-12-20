package cards.nine.app.ui.commons.dialogs.shortcuts

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.ShortcutJobs
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.DeviceTestData
import cards.nine.process.device.{ShortcutException, DeviceProcess}
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait ShortcutJobsSpecification
  extends TaskServiceSpecification
    with Mockito {

  trait ShortcutJobsScope
    extends Scope
  with DeviceTestData{

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector: Injector = mock[Injector]

    val mockShortcutUiActions = mock[ShortcutDialogUiActions]

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val shortcutJobs = new ShortcutDialogJobs(mockShortcutUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

    }

  }

}

class ShortcutJobsSpec
  extends ShortcutJobsSpecification {

  "initialize" should {
    "returns a valid response when the service returns a right response" in new ShortcutJobsScope {

      mockShortcutUiActions.initialize() returns serviceRight(Unit)
      mockShortcutUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getAvailableShortcuts(any) returns serviceRight(seqShortcut)
      mockShortcutUiActions.loadShortcuts(any) returns serviceRight(Unit)

      shortcutJobs.initialize().mustRightUnit

      there was one(mockShortcutUiActions).initialize()
      there was one(mockShortcutUiActions).showLoading()
      there was one(mockDeviceProcess).getAvailableShortcuts(any)
      there was one(mockShortcutUiActions).loadShortcuts(any)
    }

    "returns a ShortcutException when the service returns an exception" in new ShortcutJobsScope {

      mockShortcutUiActions.initialize() returns serviceRight(Unit)
      mockShortcutUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getAvailableShortcuts(any) returns serviceLeft(ShortcutException(""))

      shortcutJobs.initialize().mustLeft[ShortcutException]

      there was one(mockShortcutUiActions).initialize()
      there was one(mockShortcutUiActions).showLoading()
      there was one(mockDeviceProcess).getAvailableShortcuts(any)
      there was no(mockShortcutUiActions).loadShortcuts(any)
    }
  }

  "loadShortcuts" should {
    "returns a valid response when the service returns a right response" in new ShortcutJobsScope {

      mockShortcutUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getAvailableShortcuts(any) returns serviceRight(seqShortcut)
      mockShortcutUiActions.loadShortcuts(any) returns serviceRight(Unit)

      shortcutJobs.loadShortcuts().mustRightUnit

      there was one(mockShortcutUiActions).showLoading()
      there was one(mockDeviceProcess).getAvailableShortcuts(any)
      there was one(mockShortcutUiActions).loadShortcuts(any)
    }

    "returns a ShortcutException when the service returns an exception" in new ShortcutJobsScope {

      mockShortcutUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getAvailableShortcuts(any) returns serviceLeft(ShortcutException(""))

      shortcutJobs.loadShortcuts().mustLeft[ShortcutException]

      there was one(mockShortcutUiActions).showLoading()
      there was one(mockDeviceProcess).getAvailableShortcuts(any)
      there was no(mockShortcutUiActions).loadShortcuts(any)
    }
  }

  "configureShortcut" should {
    "call to configureShortcut" in new ShortcutJobsScope {

      mockShortcutUiActions.close() returns serviceRight(Unit)
      mockShortcutUiActions.configureShortcut(any) returns serviceRight(Unit)

      shortcutJobs.configureShortcut(shortcut).mustRightUnit

      there was one(mockShortcutUiActions).close()
      there was one(mockShortcutUiActions).configureShortcut(shortcut)
    }
  }

  "showErrorLoadingShortcuts" should {
    "call to showErrorLoadingShortcutsInScreen" in new ShortcutJobsScope {

      mockShortcutUiActions.showErrorLoadingShortcutsInScreen() returns serviceRight(Unit)
      shortcutJobs.showErrorLoadingShortcuts().mustRightUnit
      there was one(mockShortcutUiActions).showErrorLoadingShortcutsInScreen()
    }
  }

}
