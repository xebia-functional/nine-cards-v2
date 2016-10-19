package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.WidgetsOps
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.exceptions.SpaceException
import cards.nine.app.ui.launcher.holders._
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

  def loadViewEditWidgets(id: Int): TaskService[Unit] = {
    statuses = statuses.copy(idWidget = Some(id), transformation = None)
    widgetUiActions.reloadViewEditWidgets()
  }

  def closeModeEditWidgets(): TaskService[Unit] = {
    statuses = statuses.copy(mode = NormalMode, idWidget = None)
    widgetUiActions.closeModeEditWidgets()
  }

  def resizeWidget(): TaskService[Unit] = if (statuses.mode == EditWidgetsMode) {
    statuses = statuses.copy(transformation = Some(ResizeTransformation))
    widgetUiActions.resizeWidget()
  } else {
    TaskService.empty
  }

  def moveWidget(): TaskService[Unit] = if (statuses.mode == EditWidgetsMode) {
    statuses = statuses.copy(transformation = Some(MoveTransformation))
    widgetUiActions.moveWidget()
  } else {
    TaskService.empty
  }

  def arrowWidget(arrow: Arrow): TaskService[Unit] = if (statuses.mode == EditWidgetsMode) {

    type WidgetMovement = (Int, (Int, Int))

    val limits = Option((WidgetsOps.rows, WidgetsOps.columns))

    def outOfTheLimit(area: WidgetArea) =
      area.spanX <= 0 ||
        area.spanY <= 0 ||
        area.startX + area.spanX > WidgetsOps.columns ||
        area.startY + area.spanY > WidgetsOps.rows

    def resizeIntersect(idWidget: Int): TaskService[Boolean] = {

      def convertSpace(widgetArea: WidgetArea) = {
        val (increaseX, increaseY) = operationArgs
        widgetArea.copy(
          spanX = widgetArea.spanX + increaseX,
          spanY = widgetArea.spanY + increaseY)
      }

      for {
        widget <- di.widgetsProcess.getWidgetById(idWidget).resolveOption(s"Can't find the widget with id $idWidget")
        widgetsByMoment <- di.widgetsProcess.getWidgetsByMoment(widget.momentId)
        newSpace = convertSpace(widget.area)
      } yield {
        outOfTheLimit(newSpace) ||
          widgetsByMoment.filterNot(_.id == widget.id).exists(w => newSpace.intersect(w.area, limits))
      }
    }

    @scala.annotation.tailrec
    def searchSpaceForMoveWidget(
      movements: List[(Int, Int)],
      widget: Widget,
      otherWidgets: Seq[Widget]): Option[WidgetMovement] =
      movements match {
        case Nil => None
        case head :: tail =>
          val (displaceX, displaceY) = head
          val newPosition = widget.area.copy(
            startX = widget.area.startX + displaceX,
            startY = widget.area.startY + displaceY)
          if (outOfTheLimit(newPosition)) {
            None
          } else {
            val widgetsIntersected = otherWidgets.filter(w => newPosition.intersect(w.area, limits))
            widgetsIntersected match {
              case Nil => Option((widget.id, head))
              case intersected =>
                searchSpaceForMoveWidget(tail, widget, otherWidgets)
            }
          }
      }

    def moveIntersect(idWidget: Int): TaskService[Option[WidgetMovement]] =
      for {
        widget <- di.widgetsProcess.getWidgetById(idWidget)
          .resolveOption(s"Can't find the widget with id $idWidget")
        widgetsByMoment <- di.widgetsProcess.getWidgetsByMoment(widget.momentId)
      } yield {
        val otherWidgets = widgetsByMoment.filterNot(_.id == widget.id)
        searchSpaceForMoveWidget(steps(widget.area), widget, otherWidgets)
      }

    def operationArgs: (Int, Int) = arrow match {
      case ArrowUp => (0, -1)
      case ArrowDown => (0, 1)
      case ArrowRight => (1, 0)
      case ArrowLeft => (-1, 0)
    }

    def steps(area: WidgetArea): List[(Int, Int)] = (arrow match {
      case ArrowUp => 1 to area.startY map (p => (0, -p))
      case ArrowDown => 1 until (WidgetsOps.columns - area.startY) map (p => (0, p))
      case ArrowRight => 1 until (WidgetsOps.rows - area.startX) map (p => (p, 0))
      case ArrowLeft => 1 to area.startX map (p => (-p, 0))
    }).toList

    (statuses.idWidget, statuses.transformation) match {
      case (Some(id), Some(ResizeTransformation)) =>
        for {
          intersect <- resizeIntersect(id)
          _ <- if (intersect) {
            navigationUiActions.showWidgetCantResizeMessage()
          } else {
            val (increaseX, increaseY) = operationArgs
            di.widgetsProcess.resizeWidget(id, increaseX, increaseY) *>
              widgetUiActions.resizeWidgetById(id, increaseX, increaseY)
          }
        } yield ()
      case (Some(id), Some(MoveTransformation)) =>
        for {
          result <- moveIntersect(id)
          _ <- result match {
            case Some((idWidget, (displaceX, displaceY))) =>
              di.widgetsProcess.moveWidget(id, displaceX, displaceY) *>
                widgetUiActions.moveWidgetById(idWidget, displaceX, displaceY)
            case _ => navigationUiActions.showWidgetCantMoveMessage()
          }
        } yield ()
      case _ => navigationUiActions.showContactUsError()
    }
  } else {
    TaskService.empty
  }

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
