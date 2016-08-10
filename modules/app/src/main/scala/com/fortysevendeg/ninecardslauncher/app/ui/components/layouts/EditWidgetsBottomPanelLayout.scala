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
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class EditWidgetsBottomPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

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

  def init(implicit launcherPresenter: LauncherPresenter, theme: NineCardsTheme): Ui[Any] =
    (resizeAction <~
      wbInit(WorkSpaceActionWidgetButton) <~
      wbPopulateIcon(R.drawable.icon_edit_widgets_resize, R.string.resize, R.color.edit_widget_resize) <~
      On.click(Ui(launcherPresenter.resizeWidget()))) ~
      (moveAction <~
        wbInit(WorkSpaceActionWidgetButton) <~
        wbPopulateIcon(R.drawable.icon_edit_widgets_move, R.string.move, R.color.edit_widget_move) <~
        On.click(Ui(launcherPresenter.moveWidget()))) ~
      (deleteAction <~
        wbInit(WorkSpaceActionWidgetButton) <~
        wbPopulateIcon(R.drawable.icon_edit_widgets_delete, R.string.delete, R.color.edit_widget_delete) <~
        On.click(Ui(launcherPresenter.deleteWidget())))

  def showActions(): Ui[Any] = (actionsContent <~ vVisible) ~ (cursorContent <~ vInvisible)

  def animateActions(): Ui[Any] = (actionsContent <~ applyFadeIn()) ~ (cursorContent <~ applyFadeOut())

  def animateCursors(implicit launcherPresenter: LauncherPresenter): Ui[Any] = {
    val color = launcherPresenter.statuses.transformation match {
      case ResizeTransformation => resizeColor
      case MoveTransformation => moveColor
    }
    (arrowUp <~ vBackgroundCircle(color) <~ On.click(Ui(launcherPresenter.arrowWidget(ArrowUp)))) ~
      (arrowDown <~ vBackgroundCircle(color) <~ On.click(Ui(launcherPresenter.arrowWidget(ArrowDown)))) ~
      (arrowLeft <~ vBackgroundCircle(color) <~ On.click(Ui(launcherPresenter.arrowWidget(ArrowLeft)))) ~
      (arrowRight <~ vBackgroundCircle(color) <~ On.click(Ui(launcherPresenter.arrowWidget(ArrowRight)))) ~
      (actionsContent <~ applyFadeOut()) ~
      (cursorContent <~ applyFadeIn())
  }

}
