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

package cards.nine.repository.dockapp

import cards.nine.repository.model.{DockApp, DockAppData}
import cards.nine.repository.provider.{DockAppEntity, DockAppEntityData}

import scala.util.Random

trait DockAppRepositoryTestData {

  val testId            = Random.nextInt(10)
  val testNonExistingId = 15
  val testName          = Random.nextString(10)
  val testCardType      = Random.nextString(10)
  val testIntent        = Random.nextString(10)
  val testImagePath     = Random.nextString(10)
  val testPosition      = Random.nextInt(5)
  val testMockWhere     = "mock-where"

  val dockAppEntitySeq = createDockAppEntitySeq(5)
  val dockAppEntity    = dockAppEntitySeq(0)
  val dockAppSeq       = createDockAppSeq(5)
  val dockAppIdSeq     = dockAppSeq map (_.id)
  val dockAppDataSeq   = dockAppSeq map (_.data)
  val dockApp          = dockAppSeq(0)

  def createDockAppEntitySeq(num: Int) =
    List.tabulate(num)(
      i =>
        DockAppEntity(
          id = testId + i,
          data = DockAppEntityData(
            name = testName,
            dockType = testCardType,
            intent = testIntent,
            imagePath = testImagePath,
            position = testPosition)))

  def createDockAppSeq(num: Int) =
    List.tabulate(num)(
      i =>
        DockApp(
          id = testId + i,
          data = DockAppData(
            name = testName,
            dockType = testCardType,
            intent = testIntent,
            imagePath = testImagePath,
            position = testPosition)))

  def createDockAppValues =
    Map[String, Any](
      DockAppEntity.name      -> testName,
      DockAppEntity.dockType  -> testCardType,
      DockAppEntity.intent    -> testIntent,
      DockAppEntity.imagePath -> testImagePath,
      DockAppEntity.position  -> testPosition)

  def createDockAppData =
    DockAppData(
      name = testName,
      dockType = testCardType,
      intent = testIntent,
      imagePath = testImagePath,
      position = testPosition)
}
