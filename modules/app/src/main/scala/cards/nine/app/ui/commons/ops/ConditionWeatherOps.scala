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

package cards.nine.app.ui.commons.ops

import cards.nine.models.types._
import com.fortysevendeg.ninecardslauncher.R

object ConditionWeatherOps {

  implicit class ConditionWeatherIcon(condition: ConditionWeather) {

    def getIcon: Int = condition match {
      case ClearCondition   => R.drawable.icon_weather_clear
      case CloudyCondition  => R.drawable.icon_weather_cloudy
      case FoggyCondition   => R.drawable.icon_weather_foggy
      case HazyCondition    => R.drawable.icon_weather_hazy
      case IcyCondition     => R.drawable.icon_weather_icy
      case RainyCondition   => R.drawable.icon_weather_rainy
      case SnowyCondition   => R.drawable.icon_weather_snowy
      case StormyCondition  => R.drawable.icon_weather_stormy
      case WindyCondition   => R.drawable.icon_weather_windy
      case UnknownCondition => R.drawable.icon_weather_unknown
    }

  }

}
