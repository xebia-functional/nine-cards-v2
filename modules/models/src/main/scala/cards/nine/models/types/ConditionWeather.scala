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

sealed trait ConditionWeather

case object ClearCondition extends ConditionWeather

case object CloudyCondition extends ConditionWeather

case object FoggyCondition extends ConditionWeather

case object HazyCondition extends ConditionWeather

case object IcyCondition extends ConditionWeather

case object RainyCondition extends ConditionWeather

case object SnowyCondition extends ConditionWeather

case object StormyCondition extends ConditionWeather

case object WindyCondition extends ConditionWeather

case object UnknownCondition extends ConditionWeather
