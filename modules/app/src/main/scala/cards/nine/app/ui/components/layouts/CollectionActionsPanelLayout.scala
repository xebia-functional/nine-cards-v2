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

package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent._
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.widgets.TintableButton
import cards.nine.app.ui.components.widgets.tweaks.TintableButtonTweaks._
import cards.nine.app.ui.launcher.LauncherActivity
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.commons.services.TaskService._
import cards.nine.app.ui.launcher.jobs.{DragJobs, NavigationJobs, WidgetsJobs}
import cards.nine.commons.javaNull
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.PrimaryColor
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._
import macroid.extras.ViewTweaks._
import macroid.extras.TextViewTweaks._

class CollectionActionsPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
    extends LinearLayout(context, attrs, defStyle)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  val dragJobs: DragJobs = context match {
    case activity: LauncherActivity => activity.dragJobs
    case _                          => throw new RuntimeException("DragJobs not found")
  }

  val navigationJobs: NavigationJobs = context match {
    case activity: LauncherActivity => activity.navigationJobs
    case _                          => throw new RuntimeException("NavigationJobs not found")
  }

  val widgetJobs: WidgetsJobs = context match {
    case activity: LauncherActivity => activity.widgetJobs
    case _                          => throw new RuntimeException("WidgetsJobs not found")
  }

  val unselectedPosition = -1

  val selectedScale = 1.1f

  val defaultScale = 1f

  LayoutInflater.from(context).inflate(R.layout.collections_actions_view_panel, this)

  var actions: Seq[CollectionActionItem] = Seq.empty

  var draggingTo: Option[Int] = None

  lazy val leftContentView = findView(TR.launcher_collections_content_1)

  lazy val rightContentView = findView(TR.launcher_collections_content_2)

  lazy val leftActionView = findView(TR.launcher_collections_action_1)

  lazy val rightActionView = findView(TR.launcher_collections_action_2)

  def load(actions: Seq[CollectionActionItem])(implicit theme: NineCardsTheme): Ui[Any] = {

    def populate(action: CollectionActionItem, position: Int): Tweak[TintableButton] =
      tvText(action.name) +
        tvCompoundDrawablesWithIntrinsicBoundsResources(left = action.resource) +
        vSetPosition(position) +
        tbPressedColor(theme.get(PrimaryColor)) +
        tbResetColor

    def contentByIndex(index: Int) = index match {
      case 0 => Option(leftContentView)
      case 1 => Option(rightContentView)
      case _ => None
    }

    def buttonByIndex(index: Int) = index match {
      case 0 => Option(leftActionView)
      case 1 => Option(rightActionView)
      case _ => None
    }

    this.actions = actions

    if (actions.length == 1) {
      (actions.headOption match {
        case Some(action) => leftActionView <~ populate(action, 0)
        case _            => Ui.nop
      }) ~ (rightContentView <~ vGone)
    } else {
      Ui.sequence(actions.zipWithIndex map {
        case (action, index) =>
          (contentByIndex(index) <~ vVisible) ~ (buttonByIndex(index) <~ populate(action, index))
      }: _*)
    }
  }

  def dragController(action: Int, x: Float, y: Float)(implicit theme: NineCardsTheme): Unit = {

    def performAction(action: CollectionActionItem) =
      (action.collectionActionType, statuses.collectionReorderMode) match {
        case (CollectionActionAppInfo, _) =>
          dragJobs
            .settingsInAddItem()
            .resolveAsyncServiceOr(_ => dragJobs.dragUiActions.endAddItem())
        case (CollectionActionUninstall, _) =>
          dragJobs
            .uninstallInAddItem()
            .resolveAsyncServiceOr(_ => dragJobs.dragUiActions.endAddItem())
        case (CollectionActionRemove, _) =>
          dragJobs.removeCollectionInReorderMode().resolveAsync()
        case (CollectionActionEdit, Some(collection)) =>
          navigationJobs.launchCreateOrCollection(Option(collection.id)).resolveAsync()
        case (CollectionActionRemoveDockApp, _) =>
          (for {
            _ <- dragJobs.dockAppsUiActions.reset()
            _ <- dragJobs.dragUiActions.endAddItem()
          } yield ()).resolveAsync()
        case (WidgetActionRemove, _) =>
          widgetJobs.showDialogForDeletingWidget(statuses.idWidget).resolveAsync()
        case _ => dragJobs.dragUiActions.endAddItem().resolveAsync()
      }

    action match {
      case ACTION_DRAG_LOCATION =>
        val newPosition = Some(calculatePosition(x))
        if (newPosition != draggingTo) {
          draggingTo = newPosition
          (this <~ (draggingTo map select getOrElse select(unselectedPosition))).run
        }
      case ACTION_DROP =>
        if (actions.length == 1) {
          actions.headOption foreach performAction
        } else {
          draggingTo flatMap actions.lift match {
            case Some(action: CollectionActionItem) => performAction(action)
            case _                                  =>
          }
        }
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case ACTION_DRAG_EXITED =>
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case ACTION_DRAG_ENDED =>
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case _ =>
    }
  }

  private[this] def calculatePosition(x: Float): Int =
    x.toInt / (getWidth / actions.length)

  private[this] def select(position: Int) = Transformer {
    case view: TintableButton if view.getPosition.contains(position) =>
      Ui(view.setPressedColor())
    case view: TintableButton => Ui(view.setDefaultColor())
  }

}

case class CollectionActionItem(
    name: String,
    resource: Int,
    collectionActionType: CollectionActionType)

sealed trait CollectionActionType

case object CollectionActionAppInfo extends CollectionActionType

case object CollectionActionUninstall extends CollectionActionType

case object CollectionActionRemove extends CollectionActionType

case object CollectionActionRemoveDockApp extends CollectionActionType

case object CollectionActionEdit extends CollectionActionType

case object WidgetActionRemove extends CollectionActionType
