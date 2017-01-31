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

package cards.nine.services.awareness.impl

import cards.nine.models.types._
import com.google.android.gms.awareness.state.Weather._
import com.google.android.gms.location.DetectedActivity._

object KindActivity {

  def apply(t: Int): KindActivity = t match {
    case IN_VEHICLE => InVehicleActivity
    case ON_BICYCLE => OnBicycleActivity
    case ON_FOOT    => OnFootActivity
    case RUNNING    => RunningActivity
    case STILL      => StillActivity
    case TILTING    => TiltingActivity
    case WALKING    => WalkingActivity
    case _          => UnknownActivity
  }

}

object ConditionWeather {

  def apply(t: Int): ConditionWeather = t match {
    case CONDITION_CLEAR  => ClearCondition
    case CONDITION_CLOUDY => CloudyCondition
    case CONDITION_FOGGY  => FoggyCondition
    case CONDITION_HAZY   => HazyCondition
    case CONDITION_ICY    => IcyCondition
    case CONDITION_RAINY  => RainyCondition
    case CONDITION_SNOWY  => SnowyCondition
    case CONDITION_STORMY => StormyCondition
    case CONDITION_WINDY  => WindyCondition
    case _                => UnknownCondition
  }

}
