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

package cards.nine.models

import cards.nine.models.types.{WidgetResizeMode, WidgetType}

case class AppsWithWidgets(packageName: String, name: String, widgets: Seq[AppWidget])

case class AppWidget(
    userHashCode: Option[Int],
    autoAdvanceViewId: Int,
    initialLayout: Int,
    minHeight: Int,
    minResizeHeight: Int,
    minResizeWidth: Int,
    minWidth: Int,
    className: String,
    packageName: String,
    resizeMode: WidgetResizeMode,
    updatePeriodMillis: Int,
    label: String,
    preview: Int)

case class Widget(
    id: Int,
    momentId: Int,
    packageName: String,
    className: String,
    appWidgetId: Option[Int],
    area: WidgetArea,
    widgetType: WidgetType,
    label: Option[String],
    imagePath: Option[String],
    intent: Option[NineCardsIntent])

case class WidgetData(
    momentId: Int = 0,
    packageName: String,
    className: String,
    appWidgetId: Option[Int],
    area: WidgetArea,
    widgetType: WidgetType,
    label: Option[String],
    imagePath: Option[String],
    intent: Option[NineCardsIntent])

case class WidgetArea(startX: Int, startY: Int, spanX: Int, spanY: Int) {

  def intersect(other: WidgetArea, limits: Option[(Int, Int)] = None): Boolean = {
    def valueInRange(value: Int, min: Int, max: Int) = (value >= min) && (value < max)

    val xOverlap = valueInRange(startX, other.startX, other.startX + other.spanX) ||
        valueInRange(other.startX, startX, startX + spanX)

    val yOverlap = valueInRange(startY, other.startY, other.startY + other.spanY) ||
        valueInRange(other.startY, startY, startY + spanY)

    val outOfLimits = limits exists {
      case (x, y) => (startX < 0) || (startY < 0) || (startX + spanX > x) || (startY + spanY > y)
    }

    (xOverlap && yOverlap) || outOfLimits
  }

  def isValid(columns: Int, rows: Int): Boolean =
    spanX > 0 &&
      spanY > 0 &&
      startX >= 0 && startX + spanX <= columns &&
      startY >= 0 && startY + spanY <= rows

}

object Widget {

  implicit class WidgetOps(widget: Widget) {

    def toData =
      WidgetData(
        momentId = widget.momentId,
        packageName = widget.packageName,
        className = widget.className,
        appWidgetId = widget.appWidgetId,
        area = widget.area,
        widgetType = widget.widgetType,
        label = widget.label,
        imagePath = widget.imagePath,
        intent = widget.intent)

  }
}
