package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import macroid.extras.ResourcesExtras._
import macroid.extras.ImageViewTweaks._
import macroid.extras.TextViewTweaks._
import cards.nine.app.ui.components.drawables.PathMorphDrawable
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.jobs.WidgetsJobs
import cards.nine.commons.javaNull
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

class EditWidgetsTopPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  LayoutInflater.from(context).inflate(R.layout.edit_widgets_top_paneal_layout, this)

  lazy val iconIndicatorDrawable = PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  lazy val icon = findView(TR.launcher_edit_widgets_icon)

  lazy val text = findView(TR.launcher_edit_widgets_text)

  (icon <~ ivSrc(iconIndicatorDrawable)).run

  def init(implicit widgetsJobs: WidgetsJobs): Ui[Any] =
    (this <~ On.click(Ui(widgetsJobs.closeModeEditWidgets().resolveAsync()))) ~
      (text <~ tvText(R.string.editingWidgets))

  def resizing(implicit widgetsJobs: WidgetsJobs): Ui[Any] =
    (this <~ On.click(Ui(widgetsJobs.editWidgetsShowActions().resolveAsync()))) ~
      (text <~ tvText(R.string.resizingWidgets))

  def moving(implicit widgetsJobs: WidgetsJobs): Ui[Any] =
    (this <~ On.click(Ui(widgetsJobs.editWidgetsShowActions().resolveAsync()))) ~
      (text <~ tvText(R.string.movingWidgets))

}
