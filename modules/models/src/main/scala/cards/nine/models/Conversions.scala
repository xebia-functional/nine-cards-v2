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

import android.appwidget.{AppWidgetProviderInfo => AndroidAppWidgetProviderInfo}
import cards.nine.models.types.WidgetResizeMode

trait Conversions {

  def toWidget(
      androidAppWidgetProviderInfo: AndroidAppWidgetProviderInfo,
      widgetLabel: String,
      userHashCode: Option[Int]): AppWidget = {

    import androidAppWidgetProviderInfo._

    AppWidget(
      userHashCode = userHashCode,
      autoAdvanceViewId = autoAdvanceViewId,
      initialLayout = initialLayout,
      minHeight = minHeight,
      minResizeHeight = minResizeHeight,
      minResizeWidth = minResizeWidth,
      minWidth = minWidth,
      className = provider.getClassName,
      packageName = provider.getPackageName,
      resizeMode = WidgetResizeMode(resizeMode),
      updatePeriodMillis = updatePeriodMillis,
      label = widgetLabel,
      preview = previewImage)
  }
}
