package cards.nine.app.ui.launcher.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.DragEvent._
import android.view.View.OnDragListener
import android.view.{DragEvent, View, WindowManager}
import cards.nine.app.ui.commons.CommonsExcerpt._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.layouts.tweaks.AppsMomentLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.CollectionActionsPanelLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.DockAppsPanelLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.types.{AddItemToCollection, DragObject, ReorderCollection, ReorderWidget}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.DeviceVersion.KitKat
import macroid.extras.ViewTweaks._

class LauncherUiActions(val dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  implicit lazy val systemBarsTint = new SystemBarsTint

  implicit def theme: NineCardsTheme = statuses.theme

  val widgetsJobs = createWidgetsJobs

  def initialize(): TaskService[Unit] =
    (systemBarsTint.initAllSystemBarsTint() ~
      prepareBars ~
      (dom.root <~ dragListener())).toService()

  def resetFromCollection(): TaskService[Unit] = (dom.foreground <~ vBlankBackground <~ vGone).toService()

  def reloadAllViews(): TaskService[Unit] = activityContextWrapper.original.get match {
    case Some(activity: AppCompatActivity) => TaskService.right(activity.recreate())
    case _ => TaskService.empty
  }

  private[this] def prepareBars =
    KitKat.ifSupportedThen {
      val activity = activityContextWrapper.getOriginal
      val sbHeight = systemBarsTint.getStatusBarHeight
      val nbHeight = systemBarsTint.getNavigationBarHeight
      Ui(activity.getWindow.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) ~
        (dom.content <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (dom.menuCollectionRoot <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (dom.editWidgetsBottomPanel <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (dom.drawerContent <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (dom.appsMoment <~ amlPaddingTopAndBottom(sbHeight, nbHeight)) ~
        (dom.drawerLayout <~ vBackground(R.drawable.background_workspace))
    } getOrElse Ui.nop

  private[this] def dragListener(): Tweak[View] = Tweak[View] { view =>
    view.setOnDragListener(new OnDragListener {
      val dragAreaKey = "drag-area"

      override def onDrag(v: View, event: DragEvent): Boolean = {
        val dragArea = v.getField[DragArea](dragAreaKey) getOrElse NoDragArea
        (event.getAction, (dom.topBarPanel ~> height).get, (dom.dockAppsPanel ~> height).get) match {
          case (_, topBar, bottomBar) =>
            val height = KitKat.ifSupportedThen(view.getHeight - systemBarsTint.getStatusBarHeight) getOrElse view.getHeight
            val top = KitKat.ifSupportedThen(topBar + systemBarsTint.getStatusBarHeight) getOrElse topBar
            // Project location to views
            val x = event.getX
            val y = event.getY
            val currentDragArea = if (y < top) ActionsDragArea else if (y > height - bottomBar) DockAppsDragArea else WorkspacesDragArea

            val (action, area) = if (dragArea != currentDragArea) {
              (v <~ vAddField(dragAreaKey, currentDragArea)).run
              (ACTION_DRAG_EXITED, dragArea)
            } else {
              (event.getAction, currentDragArea)
            }

            (area, event.getLocalState, action) match {
              case (WorkspacesDragArea, DragObject(_, ReorderWidget), _) =>
                // Project to workspace
                (dom.workspaces <~ lwsDragReorderWidgetDispatcher(action, x, y - top)).run
              case (WorkspacesDragArea, DragObject(_, AddItemToCollection), _) =>
                // Project to workspace
                (dom.workspaces <~ lwsDragAddItemDispatcher(action, x, y - top)).run
              case (DockAppsDragArea, DragObject(_, AddItemToCollection), _) =>
                // Project to dock apps
                (dom.dockAppsPanel <~ daplDragDispatcher(action, x, y - (height - bottomBar))).run
              case (WorkspacesDragArea, DragObject(_, ReorderCollection), _) =>
                // Project to workspace
                (dom.workspaces <~ lwsDragReorderCollectionDispatcher(action, x, y - top)).run
              case (DockAppsDragArea, DragObject(_, ReorderCollection), ACTION_DROP) =>
                // Project to dock apps
                (dom.workspaces <~ lwsDragReorderCollectionDispatcher(action, x, y - top)).run
              case (ActionsDragArea, DragObject(_, ReorderCollection), ACTION_DROP) =>
                // Project to Collection actions
                ((dom.collectionActionsPanel <~ caplDragDispatcher(action, x, y)) ~
                  (dom.workspaces <~ lwsDragReorderCollectionDispatcher(action, x, y - top))).run
              case (ActionsDragArea, DragObject(_, ReorderWidget), ACTION_DROP) =>
                // Project to Collection actions
                ((dom.collectionActionsPanel <~ caplDragDispatcher(action, x, y)) ~
                  Ui(widgetsJobs.closeModeEditWidgets().resolveAsync())).run
              case (ActionsDragArea, _, _) =>
                // Project to Collection actions
                (dom.collectionActionsPanel <~ caplDragDispatcher(action, x, y)).run
              case _ =>
            }
          case _ =>
        }
        true
      }
    })
  }

}
