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

package cards.nine.repository.app

import cards.nine.repository.model.{App, AppData, DataCounter}
import cards.nine.repository.provider.{AppEntity, AppEntityData}
import org.joda.time.DateTime

import scala.util.Random

trait AppRepositoryTestData {

  val testAppId                   = Random.nextInt(10)
  val testNonExistingAppId        = 15
  val testName                    = Random.nextString(5)
  val testPackageName             = Random.nextString(5)
  val testNonExistingPackageName  = Random.nextString(5)
  val testClassName               = Random.nextString(5)
  val testCategory                = Random.nextString(5)
  val testNonExistingCategory     = Random.nextString(5)
  val testImagePath               = Random.nextString(5)
  val testDateInstalled           = Random.nextLong()
  val testDateUpdate              = Random.nextLong()
  val testVersion                 = Random.nextString(5)
  val testInstalledFromGooglePlay = Random.nextBoolean()
  val testMockWhere               = "mocked-where"

  val appEntitySeq = createAppEntitySeq(5)
  val appEntity    = appEntitySeq(0)
  val appSeq       = createAppSeq(5)
  val app          = appSeq(0)
  val appIdSeq     = appSeq map (_.id)
  val appDataSeq   = appSeq map (_.data)

  def createAppEntitySeq(num: Int) =
    List.tabulate(num)(
      i =>
        AppEntity(
          id = testAppId + i,
          data = AppEntityData(
            name = testName,
            packageName = testPackageName,
            className = testClassName,
            category = testCategory,
            dateInstalled = testDateInstalled,
            dateUpdate = testDateUpdate,
            version = testVersion,
            installedFromGooglePlay = testInstalledFromGooglePlay)))

  def createAppSeq(num: Int) =
    List.tabulate(num)(
      i =>
        App(
          id = testAppId + i,
          data = AppData(
            name = testName,
            packageName = testPackageName,
            className = testClassName,
            category = testCategory,
            dateInstalled = testDateInstalled,
            dateUpdate = testDateUpdate,
            version = testVersion,
            installedFromGooglePlay = testInstalledFromGooglePlay)))

  def createAppValues =
    Map[String, Any](
      AppEntity.name                    -> testName,
      AppEntity.packageName             -> testPackageName,
      AppEntity.className               -> testClassName,
      AppEntity.category                -> testCategory,
      AppEntity.dateInstalled           -> testDateInstalled,
      AppEntity.dateUpdate              -> testDateUpdate,
      AppEntity.version                 -> testVersion,
      AppEntity.installedFromGooglePlay -> testInstalledFromGooglePlay)

  def createAppData =
    AppData(
      name = testName,
      packageName = testPackageName,
      className = testClassName,
      category = testCategory,
      dateInstalled = testDateInstalled,
      dateUpdate = testDateUpdate,
      version = testVersion,
      installedFromGooglePlay = testInstalledFromGooglePlay)

  val appsDataSequence: Seq[String] =
    Seq("!aaa", "2bbb", "?ccc", "1ddd", "#eeee", "Abc", "Acd", "Ade", "Bcd", "Bde", "Bef", "Cde")

  val appsDataCounters =
    Seq(DataCounter("#", 5), DataCounter("A", 3), DataCounter("B", 3), DataCounter("C", 1))

  val categoryDataSequence: Seq[String] =
    Seq(
      "COMMUNICATION",
      "COMMUNICATION",
      "COMMUNICATION",
      "GAME_SIMULATION",
      "GAME_BOARD",
      "GAME_CARD",
      "SOCIAL",
      "SOCIAL",
      "SOCIAL",
      "SOCIAL",
      "TOOLS",
      "TOOLS")

  val categoryDataCounters = Seq(
    DataCounter("COMMUNICATION", 3),
    DataCounter("GAME", 3),
    DataCounter("SOCIAL", 4),
    DataCounter("TOOLS", 2))

  val now = new DateTime()

  val installationDateDataSequence: Seq[Long] = Seq(
    now.minusDays(1).getMillis,
    now.minusDays(2).getMillis,
    now.minusWeeks(1).minusDays(1).getMillis,
    now.minusWeeks(1).minusDays(3).getMillis,
    now.minusWeeks(2).minusDays(4).getMillis,
    now.minusMonths(1).minusWeeks(1).getMillis,
    now.minusMonths(2).minusWeeks(1).getMillis,
    now.minusMonths(2).minusWeeks(1).minusDays(3).getMillis)

  val installationDateDateCounters = Seq(
    DataCounter("oneWeek", 2),
    DataCounter("twoWeeks", 2),
    DataCounter("oneMonth", 1),
    DataCounter("twoMonths", 1),
    DataCounter("fourMonths", 2))

}
