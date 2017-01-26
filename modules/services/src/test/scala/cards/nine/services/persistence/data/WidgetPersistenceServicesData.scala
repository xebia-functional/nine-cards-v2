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

package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.WidgetValues._
import cards.nine.repository.model.{Widget, WidgetData}

trait WidgetPersistenceServicesData {

  def repoWidgetData(num: Int = 0) =
    WidgetData(
      momentId = widgetMomentId,
      packageName = widgetPackageName,
      className = widgetClassName,
      appWidgetId = appWidgetId,
      startX = startX,
      startY = startY,
      spanX = spanX,
      spanY = spanY,
      widgetType = widgetType,
      label = Option(label),
      imagePath = Option(widgetImagePath),
      intent = Option(intent))

  val repoWidgetData: WidgetData = repoWidgetData(0)
  val seqRepoWidgetData          = Seq(repoWidgetData(0), repoWidgetData(1), repoWidgetData(2))

  def repoWidget(num: Int = 0) = Widget(id = widgetId + num, data = repoWidgetData(num))

  val repoWidget: Widget = repoWidget(0)
  val seqRepoWidget      = Seq(repoWidget(0), repoWidget(1), repoWidget(2))

}
