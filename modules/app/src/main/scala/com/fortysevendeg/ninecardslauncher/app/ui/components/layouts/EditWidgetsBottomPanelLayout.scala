package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.IconTypes._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.PathMorphDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.WorkSpaceButtonTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses.{MoveTransformation, ResizeTransformation}
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class EditWidgetsBottomPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  LayoutInflater.from(context).inflate(R.layout.edit_widgets_bottom_paneal_layout, this)

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

  lazy val cursorUp = findView(TR.edit_widget_bottom_cursor_up)

  lazy val cursorDown = findView(TR.edit_widget_bottom_cursor_down)

  lazy val cursorLeft = findView(TR.edit_widget_bottom_cursor_left)

  lazy val cursorRight = findView(TR.edit_widget_bottom_cursor_right)

  ((cursorUp <~ ivSrc(iconUp)) ~
    (cursorDown <~ ivSrc(iconDown)) ~
    (cursorLeft <~ ivSrc(iconBack)) ~
    (cursorRight <~ ivSrc(iconNext)) ~
    (resizeAction <~
    wbInit(WorkSpaceActionWidgetButton) <~
    wbPopulateIcon(R.drawable.icon_edit_widgets_resize, R.string.resize, R.color.edit_widget_resize)) ~
    (moveAction <~
      wbInit(WorkSpaceActionWidgetButton) <~
      wbPopulateIcon(R.drawable.icon_edit_widgets_move, R.string.move, R.color.edit_widget_move)) ~
    (deleteAction <~
      wbInit(WorkSpaceActionWidgetButton) <~
      wbPopulateIcon(R.drawable.icon_edit_widgets_delete, R.string.delete, R.color.edit_widget_delete))).run

  def init(implicit launcherPresenter: LauncherPresenter): Ui[Any] =
    (resizeAction <~ On.click(Ui(launcherPresenter.resizeWidget()))) ~
      (moveAction <~ On.click(Ui(launcherPresenter.moveWidget()))) ~
      (deleteAction <~ On.click(Ui(launcherPresenter.deleteWidget())))

  def showActions(): Ui[Any] = (actionsContent <~ vVisible) ~ (cursorContent <~ vInvisible)

  def animateActions(): Ui[Any] = (actionsContent <~ applyFadeIn()) ~ (cursorContent <~ applyFadeOut())

  def animateCursors(implicit launcherPresenter: LauncherPresenter): Ui[Any] = {
    val color = launcherPresenter.statuses.transformation match {
      case ResizeTransformation => resizeColor
      case MoveTransformation => moveColor
    }
    (cursorUp <~ vBackgroundCircle(color)) ~
      (cursorDown <~ vBackgroundCircle(color)) ~
      (cursorLeft <~ vBackgroundCircle(color)) ~
      (cursorRight <~ vBackgroundCircle(color)) ~
      (actionsContent <~ applyFadeOut()) ~
      (cursorContent <~ applyFadeIn())
  }

}
