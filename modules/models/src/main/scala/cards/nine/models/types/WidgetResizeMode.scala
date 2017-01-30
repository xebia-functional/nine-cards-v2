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

package cards.nine.models.types

import android.appwidget.AppWidgetProviderInfo

sealed trait WidgetResizeMode

case object WidgetResizeNone extends WidgetResizeMode

case object WidgetResizeVertical extends WidgetResizeMode

case object WidgetResizeHorizontal extends WidgetResizeMode

case object WidgetResizeBoth extends WidgetResizeMode

object WidgetResizeMode {

  def apply(mode: Int): WidgetResizeMode = mode match {
    case AppWidgetProviderInfo.RESIZE_VERTICAL   => WidgetResizeVertical
    case AppWidgetProviderInfo.RESIZE_HORIZONTAL => WidgetResizeHorizontal
    case AppWidgetProviderInfo.RESIZE_BOTH       => WidgetResizeBoth
    case _                                       => WidgetResizeNone
  }

}
