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

package cards.nine.services.widgets.impl.utils.impl

import cards.nine.models.AppWidget
import cards.nine.models.types.WidgetResizeMode

import scala.util.Random

trait AppWidgetManagerData {

  val userHashCode: Int       = Random.nextInt(10)
  val autoAdvanceViewId: Int  = Random.nextInt(10)
  val initialLayout: Int      = Random.nextInt(10)
  val minHeight: Int          = Random.nextInt(10)
  val minResizeHeight: Int    = Random.nextInt(10)
  val minResizeWidth: Int     = Random.nextInt(10)
  val minWidth: Int           = Random.nextInt(10)
  val className: String       = Random.nextString(5)
  val packageName: String     = Random.nextString(5)
  val resizeMode: Int         = Random.nextInt(10)
  val updatePeriodMillis: Int = Random.nextInt(10)
  val label: String           = Random.nextString(5)
  val preview: Int            = Random.nextInt(10)

  val userHashCodeOption = Option(userHashCode)

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
      preview: Int = preview): Seq[AppWidget] =
    List.tabulate(num)(
      item =>
        AppWidget(
          userHashCode = userHashCode,
          autoAdvanceViewId = autoAdvanceViewId,
          initialLayout = initialLayout,
          minHeight = minHeight,
          minResizeHeight = minResizeHeight,
          minResizeWidth = minResizeWidth,
          minWidth = minWidth,
          className = className,
          packageName = packageName,
          resizeMode = WidgetResizeMode(resizeMode),
          updatePeriodMillis = updatePeriodMillis,
          label = label,
          preview = preview))

  val seqWidget: Seq[AppWidget] = createSeqWidget()

}
