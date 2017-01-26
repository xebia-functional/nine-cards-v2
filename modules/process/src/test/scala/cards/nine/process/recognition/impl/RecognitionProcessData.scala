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

package cards.nine.process.recognition.impl

import cards.nine.models._
import cards.nine.models.types.{ClearCondition, CloudyCondition, FoggyCondition, InVehicleActivity}

import scala.util.Random

trait RecognitionProcessData {

  val latitude     = Random.nextDouble()
  val longitude    = Random.nextDouble()
  val countryCode  = Some("ES")
  val countryName  = Some("Spain")
  val addressLines = Seq("street", "city", "postal code")

  val humidity              = Random.nextInt(100)
  val dewPointCelsius       = Random.nextFloat()
  val dewPointFahrenheit    = Random.nextFloat()
  val temperatureCelsius    = Random.nextFloat()
  val temperatureFahrenheit = Random.nextFloat()

  val kindActivityService = InVehicleActivity

  val kindActivityProcess = InVehicleActivity

  val typeActivity = ProbablyActivity(activityType = kindActivityService)

  val probablyActivity = ProbablyActivity(activityType = kindActivityProcess)

  val connected = Random.nextBoolean()

  val headphonesState = Headphones(connected = connected)

  val headphones = Headphones(connected = connected)

  val awarenessLocation = Location(
    latitude = latitude,
    longitude = longitude,
    countryCode = countryCode,
    countryName = countryName,
    addressLines = addressLines)

  val location = Location(
    latitude = latitude,
    longitude = longitude,
    countryCode = countryCode,
    countryName = countryName,
    addressLines = addressLines)

  val conditionsServices = Seq(ClearCondition, CloudyCondition, FoggyCondition)

  val conditionsProcess = Seq(ClearCondition, CloudyCondition, FoggyCondition)

  val weatherState = WeatherState(
    conditions = conditionsServices,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

  val weather = WeatherState(
    conditions = conditionsProcess,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

}
