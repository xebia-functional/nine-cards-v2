package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.components.drawables.IconTypes._
import cards.nine.app.ui.components.drawables.PathMorphDrawable
import cards.nine.app.ui.components.layouts.tweaks.WorkSpaceButtonTweaks._
import cards.nine.app.ui.launcher.holders._
import cards.nine.app.ui.launcher.{LauncherActivity, ResizeTransformation}
import cards.nine.commons.javaNull
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.NineCardsTheme
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import LauncherActivity._
import cards.nine.app.ui.launcher.jobs.WidgetsJobs

class EditWidgetsBottomPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  // TODO First implementation in order to remove LauncherPresenter
  implicit def widgetJobs: WidgetsJobs = context match {
    case activity: LauncherActivity => activity.widgetJobs
    case _ => throw new RuntimeException("WidgetsJobs not found")
  }

  LayoutInflater.from(context).inflate(R.layout.edit_widgets_bottom_panel_layout, this)

  val resizeColor = resGetColor(R.color.edit_widget_resize)

  val moveColor = resGetColor(R.color.edit_widget_move)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_default)

  lazy val iconBack = PathMorphDrawable(defaultIcon = BACK, defaultStroke = stroke)

  lazy val iconNext = PathMorphDrawable(defaultIcon = NEXT, defaultStroke = stroke)

  lazy val iconUp = PathMorphDrawable(defaultIcon = UP, defaultStroke = stroke)

  lazy val iconDown = PathMorphDrawable(defaultIcon = DOWN, defaultStroke = stroke)

  lazy val actionsContent = findView(TR.edit_widget_bottom_actions_content)

  lazy val cursorContent = findView(TR.edit_widget_bottom_cursors_content)

  lazy val resizeAction = findView(TR.edit_widget_bottom_action_resize)

  lazy val moveAction = findView(TR.edit_widget_bottom_action_move)

  lazy val deleteAction = findView(TR.edit_widget_bottom_action_delete)

  lazy val arrowUp = findView(TR.edit_widget_bottom_cursor_up)

  lazy val arrowDown = findView(TR.edit_widget_bottom_cursor_down)

  lazy val arrowLeft = findView(TR.edit_widget_bottom_cursor_left)

  lazy val arrowRight = findView(TR.edit_widget_bottom_cursor_right)

  ((arrowUp <~ ivSrc(iconUp)) ~
    (arrowDown <~ ivSrc(iconDown)) ~
    (arrowLeft <~ ivSrc(iconBack)) ~
    (arrowRight <~ ivSrc(iconNext))).run

  def init(implicit theme: NineCardsTheme): Ui[Any] =
    (resizeAction <~
      wbInit(WorkSpaceActionWidgetButton) <~
      wbPopulateIcon(R.drawable.icon_edit_widgets_resize, R.string.resize, R.color.edit_widget_resize) <~
      On.click(Ui(widgetJobs.resizeWidget().resolveAsync()))) ~
      (moveAction <~
        wbInit(WorkSpaceActionWidgetButton) <~
        wbPopulateIcon(R.drawable.icon_edit_widgets_move, R.string.move, R.color.edit_widget_move) <~
        On.click(Ui(widgetJobs.moveWidget().resolveAsync()))) ~
      (deleteAction <~
        wbInit(WorkSpaceActionWidgetButton) <~
        wbPopulateIcon(R.drawable.icon_edit_widgets_delete, R.string.delete, R.color.edit_widget_delete) <~
        On.click(Ui(widgetJobs.deleteWidget().resolveAsync())))

  def showActions(): Ui[Any] = (actionsContent <~ vVisible) ~ (cursorContent <~ vInvisible)

  def animateActions(): Ui[Any] = (actionsContent <~ applyFadeIn()) ~ (cursorContent <~ applyFadeOut())

  def animateCursors(): Ui[Any] = {
    val color = statuses.transformation match {
      case Some(ResizeTransformation) => resizeColor
      case _ => moveColor
    }
    (arrowUp <~
      vBackgroundCircle(color) <~
      On.click(Ui(widgetJobs.arrowWidget(ArrowUp).resolveAsyncServiceOr(onExeption)))) ~
      (arrowDown <~
        vBackgroundCircle(color) <~
        On.click(Ui(widgetJobs.arrowWidget(ArrowDown).resolveAsyncServiceOr(onExeption)))) ~
      (arrowLeft <~
        vBackgroundCircle(color) <~
        On.click(Ui(widgetJobs.arrowWidget(ArrowLeft).resolveAsyncServiceOr(onExeption)))) ~
      (arrowRight <~
        vBackgroundCircle(color) <~
        On.click(Ui(widgetJobs.arrowWidget(ArrowRight).resolveAsyncServiceOr(onExeption)))) ~
      (actionsContent <~ applyFadeOut()) ~
      (cursorContent <~ applyFadeIn())
  }

  private[this] def onExeption(ex: Throwable) = widgetJobs.navigationUiActions.showContactUsError()

}
