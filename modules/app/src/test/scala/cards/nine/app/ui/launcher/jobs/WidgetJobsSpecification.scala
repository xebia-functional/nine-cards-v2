package cards.nine.app.ui.launcher.jobs

import android.content.ComponentName
import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.ops.WidgetsOps.Cell
import cards.nine.app.ui.components.models.{LauncherMoment, MomentWorkSpace, LauncherData}
import cards.nine.app.ui.launcher._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.holders.{ArrowRight, ArrowUp}
import cards.nine.app.ui.launcher.jobs.uiactions.{LauncherDOM, NavigationUiActions, WidgetUiActions}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{AppWidgetTestData, WidgetTestData, MomentTestData}
import cards.nine.models.types.{MomentCategory, NineCardsMoment}
import cards.nine.process.moment.MomentProcess
import cards.nine.process.trackevent.TrackEventProcess
import cards.nine.process.widget.WidgetProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait WidgetJobsSpecification
  extends TaskServiceSpecification
    with Mockito {

  trait WidgetJobsScope
    extends Scope
      with MomentTestData
      with WidgetTestData
      with AppWidgetTestData
      with LauncherTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockLauncherDOM = mock[LauncherDOM]

    val mockWidgetUiActions = mock[WidgetUiActions]
    mockWidgetUiActions.dom returns mockLauncherDOM

    val mockNavigationUiActions = mock[NavigationUiActions]

    val mockInjector = mock[Injector]

    val mockWidgetProcess = mock[WidgetProcess]

    mockInjector.widgetsProcess returns mockWidgetProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val widgetsJobs = new WidgetsJobs(mockWidgetUiActions, mockNavigationUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

    }

  }

}


class WidgetJobsSpec
  extends WidgetJobsSpecification {

  sequential
  "deleteWidget" should {
    "show an error message of contact when hasn't idWidget" in new WidgetJobsScope {

      statuses = statuses.copy(idWidget = None)
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)
      widgetsJobs.deleteWidget().mustRightUnit

      there was no(mockNavigationUiActions).deleteSelectedWidget()
      there was one(mockNavigationUiActions).showContactUsError()

    }

    "return an Answers when has idWidget" in new WidgetJobsScope {

      statuses = statuses.copy(idWidget = Option(idWidget))
      mockNavigationUiActions.deleteSelectedWidget() returns serviceRight(Unit)
      widgetsJobs.deleteWidget().mustRightUnit

      there was one(mockNavigationUiActions).deleteSelectedWidget()
      there was no(mockNavigationUiActions).showContactUsError()

    }
  }
  sequential
  "deleteDBWidget" should {
    "show an error message of contact when hasn't idWidget" in new WidgetJobsScope {

      statuses = statuses.copy(idWidget = None)
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)
      val result = widgetsJobs.deleteDBWidget().mustRightUnit

      there was one(mockNavigationUiActions).showContactUsError()
    }

    "return an Answers when has idWidget" in new WidgetJobsScope {

      statuses = statuses.copy(idWidget = Option(idWidget))
      mockWidgetProcess.deleteWidget(idWidget) returns serviceRight(Unit)
      mockWidgetUiActions.closeModeEditWidgets() returns serviceRight(Unit)
      mockWidgetUiActions.unhostWidget(any) returns serviceRight(Unit)

      widgetsJobs.deleteDBWidget().mustRightUnit

      there was no(mockNavigationUiActions).showContactUsError()
      there was one(mockWidgetUiActions).closeModeEditWidgets()
      there was one(mockWidgetUiActions).unhostWidget(idWidget)
      there was one(mockWidgetProcess).deleteWidget(idWidget)
    }
  }

  "loadWidgetsForMoment" should {
    "returns an Answers when there widget for moments" in new WidgetJobsScope {

      mockWidgetUiActions.clearWidgets() returns serviceRight(Unit)
      mockMomentProcess.getMomentByType(any) returns serviceRight(moment.copy(momentType = NineCardsMoment.defaultMoment))
      mockWidgetProcess.getWidgetsByMoment(any) returns serviceRight(seqWidget map (_.copy(momentId = moment.id)))
      mockWidgetUiActions.addWidgets(any) returns serviceRight(Unit)

      widgetsJobs.loadWidgetsForMoment(NineCardsMoment.defaultMoment).mustRightUnit

      there was one(mockWidgetUiActions).clearWidgets()
      there was one(mockMomentProcess).getMomentByType(NineCardsMoment.defaultMoment)
      there was one(mockWidgetProcess).getWidgetsByMoment(moment.id)
      there was one(mockWidgetUiActions).addWidgets(seqWidget)

    }
    "returns an Answers when not there widget for moments" in new WidgetJobsScope {

      mockWidgetUiActions.clearWidgets() returns serviceRight(Unit)
      mockMomentProcess.getMomentByType(any) returns serviceRight(moment.copy(momentType = NineCardsMoment.defaultMoment))
      mockWidgetProcess.getWidgetsByMoment(any) returns serviceRight(Seq.empty)

      widgetsJobs.loadWidgetsForMoment(NineCardsMoment.defaultMoment).mustRightUnit

      there was one(mockWidgetUiActions).clearWidgets()
      there was one(mockMomentProcess).getMomentByType(NineCardsMoment.defaultMoment)
      there was one(mockWidgetProcess).getWidgetsByMoment(moment.id)
      there was no(mockWidgetUiActions).addWidgets(any)

    }
  }

  sequential
  "addWidget" should {
    "show an error message of contact when parameter is None" in new WidgetJobsScope {

      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)
      widgetsJobs.addWidget(None).mustRightUnit
      there was one(mockNavigationUiActions).showContactUsError()

    }

    "show an error message of contact when hasn't data in LauncherDOM" in new WidgetJobsScope {

      mockLauncherDOM.getData returns Seq.empty
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      widgetsJobs.addWidget(Option(appWidgetId)).mustRightUnit

      there was one(mockNavigationUiActions).showContactUsError()
    }

    "show an error message of contact when hasn't a moment " in new WidgetJobsScope {

      mockLauncherDOM.getData returns Seq(launcherData.copy(moment = None))
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      widgetsJobs.addWidget(Option(appWidgetId)).mustRightUnit

      there was one(mockNavigationUiActions).showContactUsError()

    }

    "show an error message of contact when hasn't a momentType " in new WidgetJobsScope {

      mockLauncherDOM.getData returns Seq(launcherData.copy(moment = Option(launcherMoment.copy(momentType = None))))
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      widgetsJobs.addWidget(Option(appWidgetId)).mustRightUnit

      there was one(mockNavigationUiActions).showContactUsError()

    }

    "return a valid response when statuses hasn't widget" in new WidgetJobsScope {

      val provider = new ComponentName(widget.packageName, widget.className)
      val cell = new Cell(spanX = 1, spanY = 1, widthCell = 1, heightCell = 1)

      mockLauncherDOM.getData returns Seq(launcherData)
      statuses = statuses.copy(hostingNoConfiguredWidget = None)

      mockMomentProcess.getMomentByType(any) returns serviceRight(moment.copy(momentType = NineCardsMoment.defaultMoment))
      mockWidgetUiActions.getWidgetInfoById(any) returns serviceRight(Option((provider, cell)))
      mockWidgetProcess.getWidgetsByMoment(any) returns serviceRight(Seq(widget))

      mockWidgetProcess.addWidget(any) returns serviceRight(widget)

      mockWidgetUiActions.addWidgets(any) returns serviceRight(Unit)

      widgetsJobs.addWidget(Option(appWidgetId)).mustRightUnit

      there was no(mockNavigationUiActions).showContactUsError()
      there was one(mockMomentProcess).getMomentByType(NineCardsMoment.defaultMoment)
    }

    "return a valid response when statuses has widget " in new WidgetJobsScope {

      mockLauncherDOM.getData returns Seq(launcherData)
      statuses = statuses.copy(hostingNoConfiguredWidget = Option(widget))
      mockWidgetProcess.updateAppWidgetId(any, any) returns serviceRight(widget)
      mockWidgetUiActions.replaceWidget(any) returns serviceRight(Unit)

      mockWidgetProcess.addWidgets(any) returns serviceRight(seqWidget)

      widgetsJobs.addWidget(Option(appWidgetId)).mustRightUnit

      there was no(mockNavigationUiActions).showContactUsError()

    }
  }

  "hostNoConfiguredWidget" should {
    "return a valid response when the service returns a right response" in new WidgetJobsScope {

      mockWidgetUiActions.hostWidget(any, any) returns serviceRight(Unit)
      widgetsJobs.hostNoConfiguredWidget(widget).mustRightUnit
      there was one(mockWidgetUiActions).hostWidget(widget.packageName, widget.className)
    }

  }

  "hostWidget" should {
    "return a valid response when the service returns a right response" in new WidgetJobsScope {

      mockLauncherDOM.getData returns Seq(launcherData)
      mockTrackEventProcess.addWidgetToMoment(any, any, any) returns serviceRight(Unit)
      mockWidgetUiActions.hostWidget(any, any) returns serviceRight(Unit)

      widgetsJobs.hostWidget(appWidget).mustRightUnit

      there was one(mockTrackEventProcess).addWidgetToMoment(appWidget.packageName, appWidget.className, MomentCategory(NineCardsMoment.defaultMoment))
      there was one(mockWidgetUiActions).hostWidget(appWidget.packageName, appWidget.className)
    }

    "return a valid response when the LauncherDOM returns a Seq.empty" in new WidgetJobsScope {

      mockLauncherDOM.getData returns Seq.empty
      mockWidgetUiActions.hostWidget(any, any) returns serviceRight(Unit)

      widgetsJobs.hostWidget(appWidget).mustRightUnit

      there was no(mockTrackEventProcess).addWidgetToMoment(any, any, any)
      there was one(mockWidgetUiActions).hostWidget(appWidget.packageName, appWidget.className)

    }
  }

  "configureOrAddWidget" should {
    "return a valid response when has AppWidgetId" in new WidgetJobsScope {
      mockWidgetUiActions.configureWidget(any) returns serviceRight(Unit)

      widgetsJobs.configureOrAddWidget(Option(appWidgetId)).mustRightUnit

      there was one(mockWidgetUiActions).configureWidget(appWidgetId)

    }

    "show an error message of contact when hasn't AppWidgetId" in new WidgetJobsScope {

      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      widgetsJobs.configureOrAddWidget(None).mustRightUnit

      there was no(mockWidgetUiActions).configureWidget(any)
      there was one(mockNavigationUiActions).showContactUsError()

    }
  }

  sequential
  "openModeEditWidgets" should {
    "return a valid response when hasn't workspaceScrolling" in new WidgetJobsScope {

      mockLauncherDOM.isWorkspaceScrolling returns false
      mockWidgetUiActions.openModeEditWidgets() returns serviceRight(Unit)

      widgetsJobs.openModeEditWidgets(idWidget).mustRightUnit

      statuses.mode shouldEqual EditWidgetsMode
      statuses.transformation shouldEqual None
      statuses.idWidget shouldEqual Some(idWidget)
      there was one(mockWidgetUiActions).openModeEditWidgets()
    }

    "return a valid response when has workspaceScrolling" in new WidgetJobsScope {

      mockLauncherDOM.isWorkspaceScrolling returns true
      widgetsJobs.openModeEditWidgets(idWidget).mustRightUnit
      there was no(mockWidgetUiActions).openModeEditWidgets()
    }
  }

  sequential
  "backToActionEditWidgets" should {
    "return a valid response when the service returns a right response" in new WidgetJobsScope {

      mockWidgetUiActions.reloadViewEditWidgets() returns serviceRight(Unit)
      widgetsJobs.backToActionEditWidgets().mustRightUnit

      statuses.transformation shouldEqual None
      there was one(mockWidgetUiActions).reloadViewEditWidgets()
    }

  }

  sequential
  "loadViewEditWidgets" should {
    "return a valid response when the service returns a right response" in new WidgetJobsScope {

      mockWidgetUiActions.reloadViewEditWidgets() returns serviceRight(Unit)
      widgetsJobs.loadViewEditWidgets(idWidget).mustRightUnit

      statuses.idWidget shouldEqual Some(idWidget)
      statuses.transformation shouldEqual None
      there was one(mockWidgetUiActions).reloadViewEditWidgets()

    }

  }

  sequential
  "closeModeEditWidgets" should {
    "return a valid response when the service returns a right response" in new WidgetJobsScope {

      mockWidgetUiActions.closeModeEditWidgets() returns serviceRight(Unit)
      widgetsJobs.closeModeEditWidgets().mustRightUnit

      statuses.idWidget shouldEqual None
      statuses.mode shouldEqual NormalMode
      there was one(mockWidgetUiActions).closeModeEditWidgets()
    }
  }
  sequential
  "resizeWidget" should {
    "returns an Answers when statuses.mode equal EditWigetsMode" in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode)
      mockWidgetUiActions.resizeWidget() returns serviceRight(Unit)

      widgetsJobs.resizeWidget().mustRightUnit
      statuses.transformation shouldEqual Some(ResizeTransformation)
      there was one(mockWidgetUiActions).resizeWidget()

    }

    "returns an Answers when statuses.mode not equal EditWigetsMode" in new WidgetJobsScope {

      statuses = statuses.copy(mode = ReorderMode)
      widgetsJobs.resizeWidget().mustRightUnit
      there was no(mockWidgetUiActions).resizeWidget()

    }
  }

  sequential
  "moveWidget" should {
    "returns an Answers when statuses.mode equal EditWigetsMode" in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode)
      mockWidgetUiActions.moveWidget() returns serviceRight(Unit)

      widgetsJobs.moveWidget().mustRightUnit

      there was one(mockWidgetUiActions).moveWidget()
    }

    "returns an Answers when statuses.mode not equal EditWigetsMode" in new WidgetJobsScope {

      statuses = statuses.copy(mode = ReorderMode)
      widgetsJobs.moveWidget().mustRightUnit
      there was no(mockWidgetUiActions).resizeWidget()
    }
  }

  sequential
  "arrowWidget" should {
    "return a valid response although statuses.mode not equal EditWidgetsMode" in new WidgetJobsScope {

      statuses = statuses.copy(mode = ReorderMode)
      widgetsJobs.arrowWidget(ArrowUp).mustRightUnit
    }

    "return a valid response when statuses.mode equal EditWidgetsMode and transformation is None " in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode, transformation = None, idWidget = Option(idWidget))
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      widgetsJobs.arrowWidget(ArrowUp).mustRightUnit

      there was one(mockNavigationUiActions).showContactUsError()
    }

    "show a message that can't resize when statuses.mode equal EditWidgetsMode and transformation is ResizeTransformation and ArrowUp" in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode, transformation = Option(ResizeTransformation), idWidget = Option(idWidget))
      mockWidgetProcess.getWidgetById(any) returns serviceRight(Option(widget))
      mockWidgetProcess.getWidgetsByMoment(any) returns serviceRight(Seq(widget))
      mockNavigationUiActions.showWidgetCantResizeMessage() returns serviceRight(Unit)

      widgetsJobs.arrowWidget(ArrowUp).mustRightUnit

      there was one(mockNavigationUiActions).showWidgetCantResizeMessage()

    }

    "return a valid response when statuses.mode equal EditWidgetsMode and transformation is ResizeTransformation and ArrowRight" in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode, transformation = Option(ResizeTransformation), idWidget = Option(idWidget))
      mockWidgetProcess.getWidgetById(any) returns serviceRight(Option(widget))
      mockWidgetProcess.getWidgetsByMoment(any) returns serviceRight(Seq(widget))
      mockWidgetProcess.resizeWidget(any,any,any) returns serviceRight(widget)
      mockWidgetUiActions.resizeWidgetById(any,any,any) returns serviceRight(Unit)

      widgetsJobs.arrowWidget(ArrowRight).mustRightUnit

      there was no(mockNavigationUiActions).showWidgetCantResizeMessage()

    }

    "show a message that can't move when statuses.mode equal EditWidgetsMode and transformation is MoveTransformation and ArrowUp" in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode, transformation = Option(MoveTransformation), idWidget = Option(idWidget))
      mockWidgetProcess.getWidgetById(any) returns serviceRight(Option(widget))
      mockWidgetProcess.getWidgetsByMoment(any) returns serviceRight(Seq(widget))
      mockNavigationUiActions.showWidgetCantMoveMessage() returns serviceRight(Unit)

      widgetsJobs.arrowWidget(ArrowUp).mustRightUnit

      there was one(mockNavigationUiActions).showWidgetCantMoveMessage()

    }

    "return a valid response when statuses.mode equal EditWidgetsMode and transformation is MoveTransformation and ArrowRight " in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode, transformation = Option(MoveTransformation), idWidget = Option(idWidget))
      mockWidgetProcess.getWidgetById(any) returns serviceRight(Option(widget))
      mockWidgetProcess.getWidgetsByMoment(any) returns serviceRight(Seq(widget))
      mockWidgetProcess.moveWidget(any,any,any) returns serviceRight(widget)
      mockWidgetUiActions.moveWidgetById(any,any,any) returns serviceRight(Unit)

      widgetsJobs.arrowWidget(ArrowRight).mustRightUnit

      there was no(mockNavigationUiActions).showWidgetCantMoveMessage()

    }
  }

  sequential
  "cancelWidget" should {
    "returns an Answers when statuses.mode equal EditWigetsMode" in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode)
      mockWidgetUiActions.cancelWidget(any) returns serviceRight(Unit)

      widgetsJobs.cancelWidget(Option(appWidgetId)).mustRightUnit

      there was one(mockWidgetUiActions).cancelWidget(appWidgetId)
    }

    "returns an Answers when statuses.mode not equal EditWigetsMode " in new WidgetJobsScope {

      statuses = statuses.copy(mode = ReorderMode)
      widgetsJobs.cancelWidget(Option(appWidgetId)).mustRightUnit
      there was no(mockWidgetUiActions).cancelWidget(appWidgetId)
    }

    "returns an Answers when hasn't appwidgetId" in new WidgetJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode)
      widgetsJobs.cancelWidget(None).mustRightUnit
      there was no(mockWidgetUiActions).cancelWidget(any)
    }
  }

  "editWidgetsShowActions" should {
    "return a valid response when the service returns a right response" in new WidgetJobsScope {

      mockWidgetUiActions.editWidgetsShowActions() returns serviceRight(Unit)
      widgetsJobs.editWidgetsShowActions().mustRightUnit
      there was one(mockWidgetUiActions).editWidgetsShowActions()
    }
  }


}
