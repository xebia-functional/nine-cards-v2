package cards.nine.app.ui.commons.dialogs.widgets

import cards.nine.app.di.Injector
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.AppWidgetTestData
import cards.nine.process.device.{DeviceProcess, WidgetException}
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait WidgetsDialogJobsSpecification extends TaskServiceSpecification with Mockito {

  trait WidgetsDialogJobsScope extends Scope with AppWidgetTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector: Injector = mock[Injector]

    val mockWidgetsDialogUiActions = mock[WidgetsDialogUiActions]

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val widgetsDialogJobs = new WidgetsDialogJobs(mockWidgetsDialogUiActions) {

      override lazy val di: Injector = mockInjector

    }
  }

}

class WidgetsDialogJobsSpec extends WidgetsDialogJobsSpecification {

  "initialize" should {
    "returns a valid response when the service returns a right response" in new WidgetsDialogJobsScope {

      mockWidgetsDialogUiActions.initialize() returns serviceRight(Unit)
      mockWidgetsDialogUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getWidgets(any) returns serviceRight(seqAppsWithWidgets)
      mockWidgetsDialogUiActions.loadWidgets(any) returns serviceRight(Unit)

      widgetsDialogJobs.initialize().mustRightUnit

      there was one(mockWidgetsDialogUiActions).initialize()
      there was one(mockWidgetsDialogUiActions).showLoading()
      there was one(mockDeviceProcess).getWidgets(any)
      there was one(mockWidgetsDialogUiActions).loadWidgets(seqAppsWithWidgets)
    }
  }

  "loadWidgets" should {
    "returns a valid response when the service returns a right response" in new WidgetsDialogJobsScope {

      mockWidgetsDialogUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getWidgets(any) returns serviceRight(seqAppsWithWidgets)
      mockWidgetsDialogUiActions.loadWidgets(any) returns serviceRight(Unit)

      widgetsDialogJobs.loadWidgets().mustRightUnit

      there was one(mockWidgetsDialogUiActions).showLoading()
      there was one(mockDeviceProcess).getWidgets(any)
      there was one(mockWidgetsDialogUiActions).loadWidgets(seqAppsWithWidgets)
    }

    "returns a WidgetException when the service returns an exception" in new WidgetsDialogJobsScope {

      mockWidgetsDialogUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getWidgets(any) returns serviceLeft(WidgetException(""))
      mockWidgetsDialogUiActions.loadWidgets(any) returns serviceRight(Unit)

      widgetsDialogJobs.loadWidgets().mustLeft[WidgetException]

      there was one(mockWidgetsDialogUiActions).showLoading()
      there was one(mockDeviceProcess).getWidgets(any)
      there was no(mockWidgetsDialogUiActions).loadWidgets(any)
    }
  }

  "close" should {
    "call to close" in new WidgetsDialogJobsScope {

      mockWidgetsDialogUiActions.close() returns serviceRight(Unit)
      widgetsDialogJobs.close().mustRightUnit
      there was one(mockWidgetsDialogUiActions).close()
    }
  }
}
