package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.WidgetsOps
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.exceptions.SpaceException
import cards.nine.app.ui.launcher.holders._
import cards.nine.app.ui.launcher.jobs.uiactions.{NavigationUiActions, WidgetUiActions}
import cards.nine.app.ui.launcher.{EditWidgetsMode, MoveTransformation, NormalMode, ResizeTransformation}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{AppWidgetType, MomentCategory, NineCardsMoment}
import cards.nine.models.{AppWidget, Widget, WidgetArea, WidgetData}
import cats.implicits._
import macroid.ActivityContextWrapper

class WidgetsJobs(
  val widgetUiActions: WidgetUiActions,
  val navigationUiActions: NavigationUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  def deleteWidget(): TaskService[Unit] =
    statuses.idWidget match {
      case Some(id) => navigationUiActions.deleteSelectedWidget()
      case _ => navigationUiActions.showContactUsError()
    }

  def deleteDBWidget(): TaskService[Unit] =
    statuses.idWidget match {
      case Some(id) =>
        for {
          _ <- di.widgetsProcess.deleteWidget(id)
          _ <- closeModeEditWidgets()
          _ <- widgetUiActions.unhostWidget(id)
        } yield ()
      case _ => navigationUiActions.showContactUsError()
    }

  def loadWidgetsForMoment(nineCardsMoment: NineCardsMoment): TaskService[Unit] =
    for {
      _ <- widgetUiActions.clearWidgets()
      moment <- di.momentProcess.getMomentByType(nineCardsMoment)
      widgets <- di.widgetsProcess.getWidgetsByMoment(moment.id)
      _ <- widgets match {
        case Nil => TaskService.empty
        case w => widgetUiActions.addWidgets(w)
      }
    } yield ()

  def addWidget(maybeAppWidgetId: Option[Int]): TaskService[Unit] = {

    def createWidget(appWidgetId: Int, nineCardsMoment: NineCardsMoment) = for {
      moment <- di.momentProcess.getMomentByType(nineCardsMoment)
      widgetInfo <- widgetUiActions.getWidgetInfoById(appWidgetId).resolveOption(s"Widget information nor found with id $appWidgetId")
      (provider, cell) = widgetInfo
      widgetsByMoment <- di.widgetsProcess.getWidgetsByMoment(moment.id)
      space <- getSpaceInTheScreen(widgetsByMoment, cell.spanX, cell.spanY)
      widgetData = WidgetData(
        momentId = moment.id,
        packageName = provider.getPackageName,
        className = provider.getClassName,
        appWidgetId = Option(appWidgetId),
        area = WidgetArea(
          startX = space.startX,
          startY = space.startY,
          spanX = space.spanX,
          spanY = space.spanY),
        widgetType = AppWidgetType,
        label = None,
        imagePath = None,
        intent = None)
      widget <- di.widgetsProcess.addWidget(widgetData)
    } yield widget

    (for {
      appWidgetId <- maybeAppWidgetId
      data <- widgetUiActions.dom.getData.headOption
      moment <- data.moment
      nineCardMoment <- moment.momentType
    } yield {
      val hostingWidgetId = statuses.hostingNoConfiguredWidget map (_.id)

      hostingWidgetId match {
        case Some(id) =>
          statuses = statuses.copy(hostingNoConfiguredWidget = None)
          for {
            widget <- di.widgetsProcess.updateAppWidgetId(id, appWidgetId)
            _ <- widgetUiActions.replaceWidget(widget)
          } yield ()
        case _ =>
          for {
            widget <- createWidget(appWidgetId, nineCardMoment)
            _ <- di.trackEventProcess.addWidget(widget.packageName)
            _ <- widgetUiActions.addWidgets(Seq(widget))
          } yield ()
      }
    }) getOrElse navigationUiActions.showContactUsError()
  }

  def hostNoConfiguredWidget(widget: Widget): TaskService[Unit] = {
    statuses = statuses.copy(hostingNoConfiguredWidget = Option(widget))
    widgetUiActions.hostWidget(widget.packageName, widget.className)
  }

  def hostWidget(widget: AppWidget): TaskService[Unit] = {
    statuses = statuses.copy(hostingNoConfiguredWidget = None)
    val currentMomentType = widgetUiActions.dom.getData.headOption flatMap (_.moment) flatMap (_.momentType)
    for {
      _ <- currentMomentType match {
        case Some(momentType) =>
          di.trackEventProcess.addWidgetToMoment(widget.packageName, widget.className, MomentCategory(momentType))
        case _ => TaskService.empty
      }
      _ <- widgetUiActions.hostWidget(widget.packageName, widget.className)
    } yield ()
  }

  def configureOrAddWidget(maybeAppWidgetId: Option[Int]): TaskService[Unit] =
    maybeAppWidgetId match {
      case Some(appWidgetId) => widgetUiActions.configureWidget(appWidgetId)
      case _ => navigationUiActions.showContactUsError()
    }

  def openModeEditWidgets(id: Int): TaskService[Unit] = if (!widgetUiActions.dom.isWorkspaceScrolling) {
    statuses = statuses.copy(mode = EditWidgetsMode, transformation = None, idWidget = Some(id))
    widgetUiActions.openModeEditWidgets()
  } else {
    TaskService.empty
  }

  def backToActionEditWidgets(): TaskService[Unit] = {
    statuses = statuses.copy(transformation = None)
    widgetUiActions.reloadViewEditWidgets()
  }

  def closeModeEditWidgets(): TaskService[Unit] =
    for {
      _ <- TaskService.right(statuses = statuses.copy(mode = NormalMode, idWidget = None))
      _ <- di.widgetsProcess.updateWidgets(widgetUiActions.dom.getCurrentWidgets)
      _ <- widgetUiActions.closeModeEditWidgets()
    } yield ()

  def cancelWidget(maybeAppWidgetId: Option[Int]): TaskService[Unit] =
    (statuses.mode == EditWidgetsMode, maybeAppWidgetId) match {
      case (true, Some(id)) => widgetUiActions.cancelWidget(id)
      case _ => TaskService.empty
    }

  def editWidgetsShowActions(): TaskService[Unit] = widgetUiActions.editWidgetsShowActions()

  private[this] def getSpaceInTheScreen(widgetsByMoment: Seq[Widget], spanX: Int, spanY: Int): TaskService[WidgetArea] = {

    def searchSpace(widgets: Seq[Widget]): TaskService[WidgetArea] = {
      val emptySpaces = (for {
        column <- 0 to (WidgetsOps.columns - spanX)
        row <- 0 to (WidgetsOps.rows - spanY)
      } yield {
        val area = WidgetArea(
          startX = column,
          startY = row,
          spanX = spanX,
          spanY = spanY)
        val hasConflict = widgets find (widget => widget.area.intersect(area, Option((WidgetsOps.rows, WidgetsOps.columns))))
        if (hasConflict.isEmpty) Some(area) else None
      }).flatten
      emptySpaces.headOption match {
        case Some(space) => TaskService.right(space)
        case _ => TaskService.left(SpaceException("Widget don't have space"))
      }
    }

    for {
      space <- searchSpace(widgetsByMoment)
    } yield space
  }

}
