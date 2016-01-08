package com.fortysevendeg.ninecardslauncher.services.widgets.impl.utils.impl

import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher.services.widgets.impl.TestDrawable
import com.fortysevendeg.ninecardslauncher.services.widgets.models.Widget

import scala.util.Random

trait AppWidgetManagerData {

  val userHashCode: Int = Random.nextInt(10)
  val autoAdvanceViewId: Int = Random.nextInt(10)
  val initialLayout: Int = Random.nextInt(10)
  val minHeight: Int = Random.nextInt(10)
  val minResizeHeight: Int = Random.nextInt(10)
  val minResizeWidth: Int = Random.nextInt(10)
  val minWidth: Int = Random.nextInt(10)
  val className: String = Random.nextString(5)
  val packageName: String = Random.nextString(5)
  val resizeMode: Int = Random.nextInt(10)
  val updatePeriodMillis: Int = Random.nextInt(10)
  val label: String = Random.nextString(5)
  val icon: Drawable = new TestDrawable
  val preview: Drawable = new TestDrawable

  val userHashCodeOption = Option(userHashCode)
  val previewOption = Option(preview)

  def createSeqWidget(
    num: Int = 5,
    userHashCode: Option[Int] = userHashCodeOption,
    autoAdvanceViewId: Int = autoAdvanceViewId,
    initialLayout: Int = initialLayout,
    minHeight: Int = minHeight,
    minResizeHeight: Int = minResizeHeight,
    minResizeWidth: Int = minResizeWidth,
    minWidth: Int = minWidth,
    className: String = className,
    packageName: String = packageName,
    resizeMode: Int = resizeMode,
    updatePeriodMillis: Int = updatePeriodMillis,
    label: String = label,
    icon: Drawable = icon,
    preview: Option[Drawable] = previewOption): Seq[Widget] = List.tabulate(num)(
    item => Widget(
      userHashCode = userHashCode,
      autoAdvanceViewId = autoAdvanceViewId,
      initialLayout = initialLayout,
      minHeight = minHeight,
      minResizeHeight = minResizeHeight,
      minResizeWidth = minResizeWidth,
      minWidth = minWidth,
      className = className,
      packageName = packageName,
      resizeMode = resizeMode,
      updatePeriodMillis = updatePeriodMillis,
      label = label,
      icon = icon,
      preview = preview))

  val seqWidget: Seq[Widget] = createSeqWidget()

}
