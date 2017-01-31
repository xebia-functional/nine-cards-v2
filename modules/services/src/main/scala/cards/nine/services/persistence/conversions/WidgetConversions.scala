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

package cards.nine.services.persistence.conversions

import cards.nine.models.types.WidgetType
import cards.nine.models.{NineCardsIntentConversions, Widget, WidgetArea, WidgetData}
import cards.nine.repository.model.{Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}

trait WidgetConversions extends NineCardsIntentConversions {

  def toWidget(widget: RepositoryWidget): Widget =
    Widget(
      id = widget.id,
      momentId = widget.data.momentId,
      packageName = widget.data.packageName,
      className = widget.data.className,
      appWidgetId = if (widget.data.appWidgetId == 0) None else Some(widget.data.appWidgetId),
      area = WidgetArea(
        startX = widget.data.startX,
        startY = widget.data.startY,
        spanX = widget.data.spanX,
        spanY = widget.data.spanY),
      widgetType = WidgetType(widget.data.widgetType),
      label = widget.data.label,
      imagePath = widget.data.imagePath,
      intent = widget.data.intent map jsonToNineCardIntent)

  def toRepositoryWidget(widget: Widget): RepositoryWidget =
    RepositoryWidget(
      id = widget.id,
      data = RepositoryWidgetData(
        momentId = widget.momentId,
        packageName = widget.packageName,
        className = widget.className,
        appWidgetId = widget.appWidgetId getOrElse 0,
        startX = widget.area.startX,
        startY = widget.area.startY,
        spanX = widget.area.spanX,
        spanY = widget.area.spanY,
        widgetType = widget.widgetType.name,
        label = widget.label,
        imagePath = widget.imagePath,
        intent = widget.intent map nineCardIntentToJson))

  def toRepositoryWidgetData(widget: WidgetData): RepositoryWidgetData =
    RepositoryWidgetData(
      momentId = widget.momentId,
      packageName = widget.packageName,
      className = widget.className,
      appWidgetId = widget.appWidgetId getOrElse 0,
      startX = widget.area.startX,
      startY = widget.area.startY,
      spanX = widget.area.spanX,
      spanY = widget.area.spanY,
      widgetType = widget.widgetType.name,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent map nineCardIntentToJson)

}
