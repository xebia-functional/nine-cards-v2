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

package cards.nine.app.ui.launcher.jobs.uiactions

import android.appwidget.{AppWidgetHost, AppWidgetManager}
import android.content.{ComponentName, Intent}
import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.CommonsTweak._
import macroid.extras.UIActionsExtras._
import macroid.extras.DrawerLayoutTweaks._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.WidgetsOps.{Cell, _}
import cards.nine.app.ui.commons._
import cards.nine.app.ui.components.layouts._
import cards.nine.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.CollectionActionsPanelLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.exceptions.SpaceException
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{NineCardsTheme, Widget}
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.ResourcesExtras.resGetString

class WidgetUiActions(val dom: LauncherDOM)(
    implicit activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
    extends ImplicitsUiExceptions {

  implicit lazy val systemBarsTint = new SystemBarsTint

  implicit lazy val widgetsJobs = createWidgetsJobs

  implicit def theme: NineCardsTheme = statuses.theme

  val appWidgetMessage = "Widget Manager not loaded"

  lazy val actionForWidgets = Seq(
    CollectionActionItem(
      resGetString(R.string.remove),
      R.drawable.icon_launcher_action_remove,
      WidgetActionRemove))

  def initialize(): TaskService[Unit] = TaskService.right {
    statuses.appWidgetHost match {
      case Some(appWidgetHost) => appWidgetHost.startListening()
      case _                   => AppLog.info(appWidgetMessage)
    }
  }

  def destroy(): TaskService[Unit] = TaskService.right {
    statuses.appWidgetHost match {
      case Some(appWidgetHost) => appWidgetHost.stopListening()
      case _                   => AppLog.info(appWidgetMessage)
    }
  }

  def addWidgets(widgets: Seq[Widget]): TaskService[Unit] = {

    def add(widget: Widget, appWidgetManager: AppWidgetManager, appWidgetHost: AppWidgetHost) = {
      val widthContent  = dom.workspaces.getWidth
      val heightContent = dom.workspaces.getHeight

      val maybeAppWidgetInfo = widget.appWidgetId flatMap (widgetId =>
                                                             Option(
                                                               appWidgetManager.getAppWidgetInfo(
                                                                 widgetId)))

      (maybeAppWidgetInfo, widget.appWidgetId) match {
        case (Some(appWidgetInfo), Some(appWidgetId)) =>
          val cell = appWidgetInfo.getCell(widthContent, heightContent)

          Ui {
            // We must create a wrapper of Ui here because the view must be created in the Ui-Thread
            val hostView =
              appWidgetHost
                .createView(activityContextWrapper.application, appWidgetId, appWidgetInfo)
            hostView.setAppWidget(appWidgetId, appWidgetInfo)
            (dom.workspaces <~ lwsAddWidget(hostView, cell, widget)).run
          }
        case _ =>
          val (wCell, hCell) = sizeCell(widthContent, heightContent)
          dom.workspaces <~ lwsAddNoConfiguredWidget(wCell, hCell, widget)
      }
    }

    (statuses.appWidgetManager, statuses.appWidgetHost) match {
      case (Some(appWidgetManager), Some(appWidgetHost)) =>
        val uiWidgets = widgets map (widget => add(widget, appWidgetManager, appWidgetHost))
        Ui.sequence(uiWidgets: _*).toService()
      case _ => showMessage(R.string.widgetsErrorMessage).toService()
    }
  }

  def replaceWidget(widget: Widget): TaskService[Unit] = {

    def replace(appWidgetManager: AppWidgetManager, appWidgetHost: AppWidgetHost) = {
      val maybeAppWidgetInfo = widget.appWidgetId flatMap (widgetId =>
                                                             Option(
                                                               appWidgetManager.getAppWidgetInfo(
                                                                 widgetId)))

      (maybeAppWidgetInfo, widget.appWidgetId) match {
        case (Some(appWidgetInfo), Some(appWidgetId)) =>
          val widthContent  = dom.workspaces.getWidth
          val heightContent = dom.workspaces.getHeight

          val (wCell, hCell) = sizeCell(widthContent, heightContent)

          Ui {
            // We must create a wrapper of Ui here because the view must be created in the Ui-Thread
            val hostView =
              appWidgetHost
                .createView(activityContextWrapper.application, appWidgetId, appWidgetInfo)
            hostView.setAppWidget(appWidgetId, appWidgetInfo)
            (dom.workspaces <~ lwsReplaceWidget(hostView, wCell, hCell, widget)).run
          }
        case _ => Ui.nop
      }
    }

    (statuses.appWidgetManager, statuses.appWidgetHost) match {
      case (Some(appWidgetManager), Some(appWidgetHost)) =>
        replace(appWidgetManager, appWidgetHost).toService()
      case _ => showMessage(R.string.widgetsErrorMessage).toService()
    }
  }

  def clearWidgets(): TaskService[Unit] =
    (dom.workspaces <~ lwsClearWidgets()).toService()

  def unhostWidget(id: Int): TaskService[Unit] =
    (dom.workspaces <~ lwsUnhostWidget(id)).toService()

  def hostWidget(packageName: String, className: String): TaskService[Unit] = {
    (statuses.appWidgetManager, statuses.appWidgetHost) match {
      case (Some(appWidgetManager), Some(appWidgetHost)) =>
        val appWidgetId = appWidgetHost.allocateAppWidgetId()
        val provider    = new ComponentName(packageName, className)
        val success =
          appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, provider)
        (if (success) {
           Ui(widgetsJobs.configureOrAddWidget(Some(appWidgetId)).resolveAsync())
         } else {
           val intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
           intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
           intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider)
           uiStartIntentForResult(intent, RequestCodes.goToWidgets)
         }).toService()
      case _ => showMessage(R.string.widgetsErrorMessage).toService()
    }
  }

  def configureWidget(appWidgetId: Int): TaskService[Unit] = {
    statuses.appWidgetManager match {
      case Some(appWidgetManager) =>
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
        (Option(appWidgetInfo.configure) match {
          case Some(configure) =>
            val intent =
              new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
            intent.setComponent(configure)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            uiStartIntentForResult(intent, RequestCodes.goToConfigureWidgets)
          case _ =>
            Ui(widgetsJobs.addWidget(Some(appWidgetId)).resolveAsyncServiceOr[Throwable] {
              case ex: SpaceException =>
                widgetsJobs.navigationUiActions.showWidgetNoHaveSpaceMessage()
              case _ =>
                widgetsJobs.navigationUiActions.showContactUsError()
            })
        }).toService()
      case _ => showMessage(R.string.widgetsErrorMessage).toService()
    }

  }

  def getWidgetInfoById(appWidgetId: Int): TaskService[Option[(ComponentName, Cell)]] =
    TaskService {
      CatchAll[UiException] {
        statuses.appWidgetManager match {
          case Some(appWidgetManager) =>
            Option(appWidgetManager.getAppWidgetInfo(appWidgetId)) map { info =>
              (info.provider, info.getCell(dom.workspaces.getWidth, dom.workspaces.getHeight))
            }
          case _ => None
        }
      }
    }

  def openModeEditWidgets(): TaskService[Unit] =
    (uiVibrate() ~
      (dom.dockAppsPanel <~ applyFadeOut()) ~
      (dom.paginationPanel <~ applyFadeOut()) ~
      (dom.topBarPanel <~ applyFadeOut()) ~
      (dom.collectionActionsPanel <~ caplLoad(actionForWidgets) <~ applyFadeIn()) ~
      (dom.workspaces <~ awsDisabled() <~ lwsStartEditWidgets()) ~
      (dom.drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd)).toService()

  def reloadViewEditWidgets(): TaskService[Unit] =
    (dom.workspaces <~ lwsReloadSelectedWidget).toService()

  def closeModeEditWidgets(): TaskService[Unit] =
    ((dom.dockAppsPanel <~ applyFadeIn()) ~
      (dom.paginationPanel <~ applyFadeIn()) ~
      (dom.topBarPanel <~ applyFadeIn()) ~
      (dom.collectionActionsPanel <~ applyFadeOut()) ~
      (dom.workspaces <~ awsEnabled() <~ lwsCloseEditWidgets()) ~
      (dom.drawerLayout <~
        dlUnlockedStart <~
        (if (dom.hasCurrentMomentAssociatedCollection) dlUnlockedEnd
         else Tweak.blank))).toService()

  def cancelWidget(appWidgetId: Int): TaskService[Unit] = TaskService.right {
    statuses.appWidgetHost match {
      case Some(appWidgetHost) => appWidgetHost.deleteAppWidgetId(appWidgetId)
      case _                   => TaskService.right(AppLog.info(appWidgetMessage))
    }
  }

  def editWidgetsShowActions(): TaskService[Unit] =
    (dom.workspaces <~ lwsReloadSelectedWidget).toService()

  private[this] def showMessage(res: Int, args: Seq[String] = Seq.empty): Ui[Any] =
    dom.workspaces <~ vLauncherSnackbar(res, args)

}
