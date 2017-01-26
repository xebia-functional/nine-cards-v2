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

package cards.nine.app.ui.launcher.jobs

import android.graphics.Color
import cards.nine.app.ui.components.models.{LauncherData, LauncherMoment, MomentWorkSpace}
import cards.nine.commons.test.data.{ApplicationTestData, DeviceTestData}
import cards.nine.models._
import cards.nine.models.types.theme._
import cards.nine.models.types.{ClearCondition, CloudyCondition, FoggyCondition, NineCardsMoment}
import cards.nine.services.persistence.conversions.AppConversions

import scala.util.Random

trait LauncherTestData extends DeviceTestData with ApplicationTestData with AppConversions {

  val idWidget    = 1
  val appWidgetId = 1

  val launcherMoment =
    LauncherMoment(momentType = Option(NineCardsMoment.defaultMoment), collection = None)

  def launcherData(num: Int = 0) =
    LauncherData(
      workSpaceType = MomentWorkSpace,
      moment = Option(launcherMoment),
      collections = Seq.empty,
      positionByType = 0 + num)

  val launcherData: LauncherData         = launcherData(0)
  val seqLauncherData: Seq[LauncherData] = Seq(launcherData(0), launcherData(1), launcherData(2))

  val numberPhone = "123456789"
  val packageName = "packageName"
  val errorMenu   = 0

  val keyword: String = "keyword"
  val querry: String  = "querry"

  val position: Int            = 1
  val positionFrom: Int        = 1
  val positionFromNoExist: Int = 50
  val positionTo: Int          = 2

  val theme = NineCardsTheme(
    name = "light",
    parent = ThemeLight,
    styles = Seq.empty,
    themeColors = ThemeColors(Color.parseColor("#FF9800"), Seq.empty))

  val humidity              = Random.nextInt(100)
  val dewPointCelsius       = Random.nextFloat()
  val dewPointFahrenheit    = Random.nextFloat()
  val temperatureCelsius    = Random.nextFloat()
  val temperatureFahrenheit = Random.nextFloat()
  val conditionsServices    = Seq(ClearCondition, CloudyCondition, FoggyCondition)

  val weatherState = WeatherState(
    conditions = conditionsServices,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

  val lastPhone = "lastPhone"

}
