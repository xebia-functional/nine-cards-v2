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

package cards.nine.repository.moment

import cards.nine.repository.model.{Moment, MomentData}
import cards.nine.repository.provider.{MomentEntity, MomentEntityData}

import scala.util.Random

trait MomentRepositoryTestData {

  val testId                      = Random.nextInt(10)
  val testNonExistingId           = 15
  val testCollectionId            = Random.nextInt(5)
  val testNonExistingCollectionId = Random.nextInt(5) + 100
  val testTimeslot                = Random.nextString(10)
  val testWifi                    = Random.nextString(10)
  val testBluetooth               = Random.nextString(10)
  val testHeadphone               = Random.nextBoolean()
  val testMomentType              = Random.nextString(10)
  val testCollectionIdOption      = Option(testCollectionId)
  val testMockWhere               = "mock-where"

  val momentEntitySeq = createMomentEntitySeq(5)
  val momentEntity    = momentEntitySeq(0)
  val momentSeq       = createMomentSeq(5)
  val momentIdSeq     = momentSeq map (_.id)
  val momentDataSeq   = momentSeq map (_.data)
  val moment          = momentSeq(0)

  def createMomentEntitySeq(num: Int) =
    List.tabulate(num)(
      i =>
        MomentEntity(
          id = testId + i,
          data = MomentEntityData(
            collectionId = Some(testCollectionId),
            timeslot = testTimeslot,
            wifi = testWifi,
            bluetooth = testBluetooth,
            headphone = testHeadphone,
            momentType = testMomentType)))

  def createMomentSeq(num: Int) =
    List.tabulate(num)(
      i =>
        Moment(
          id = testId + i,
          data = MomentData(
            collectionId = testCollectionIdOption,
            timeslot = testTimeslot,
            wifi = testWifi,
            bluetooth = testBluetooth,
            headphone = testHeadphone,
            momentType = Option(testMomentType))))

  def createMomentValues =
    Map[String, Any](
      MomentEntity.collectionId -> (testCollectionIdOption orNull),
      MomentEntity.timeslot     -> testTimeslot,
      MomentEntity.wifi         -> testWifi,
      MomentEntity.headphone    -> testHeadphone,
      MomentEntity.momentType   -> testMomentType)

  def createMomentData =
    MomentData(
      collectionId = testCollectionIdOption,
      timeslot = testTimeslot,
      wifi = testWifi,
      bluetooth = testBluetooth,
      headphone = testHeadphone,
      momentType = Option(testMomentType))

  def createMomentValuesCollection =
    Map[String, Any](
      MomentEntity.collectionId -> (None orNull),
      MomentEntity.timeslot     -> testTimeslot,
      MomentEntity.wifi         -> testWifi,
      MomentEntity.bluetooth    -> testBluetooth,
      MomentEntity.headphone    -> testHeadphone,
      MomentEntity.momentType   -> testMomentType)

  def createMomentDataCollection =
    MomentData(
      collectionId = None,
      timeslot = testTimeslot,
      wifi = testWifi,
      bluetooth = testBluetooth,
      headphone = testHeadphone,
      momentType = Option(testMomentType))
}
