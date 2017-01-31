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

package cards.nine.repository.collection

import cards.nine.repository.model.{Collection, CollectionData}
import cards.nine.repository.provider.{CollectionEntity, CollectionEntityData}

import scala.util.Random

trait CollectionRepositoryTestData {

  val testCollectionId                     = Random.nextInt(10)
  val testNonExistingCollectionId          = 15
  val testPosition                         = Random.nextInt(10)
  val testNonExistingPosition              = 15
  val testName                             = Random.nextString(5)
  val testCollectionType                   = Random.nextString(5)
  val testIcon                             = Random.nextString(5)
  val testThemedColorIndex                 = Random.nextInt(10)
  val testAppsCategory                     = Random.nextString(5)
  val testOriginalSharedCollectionId       = Random.nextString(5)
  val testSharedCollectionId               = Random.nextString(5)
  val testNonExistingSharedCollectionId    = Random.nextString(5)
  val testSharedCollectionSubscribed       = Random.nextInt(10) < 5
  val testAppsCategoryOption               = Option(testAppsCategory)
  val testOriginalSharedCollectionIdOption = Option(testOriginalSharedCollectionId)
  val testSharedCollectionIdOption         = Option(testSharedCollectionId)
  val testSharedCollectionSubscribedOption = Option(testSharedCollectionSubscribed)
  val testMockWhere                        = "mock-where"

  val collectionEntitySeq = createCollectionEntitySeq(5)
  val collectionEntity    = collectionEntitySeq(0)
  val collectionSeq       = createCollectionSeq(5)
  val collectionIdSeq     = collectionSeq map (_.id)
  val collectionDataSeq   = collectionSeq map (_.data)
  val collection          = collectionSeq(0)

  def createCollectionEntitySeq(num: Int) =
    List.tabulate(num)(
      i =>
        CollectionEntity(
          id = testCollectionId + i,
          data = CollectionEntityData(
            position = testPosition,
            name = testName,
            `type` = testCollectionType,
            icon = testIcon,
            themedColorIndex = testThemedColorIndex,
            appsCategory = testAppsCategory,
            originalSharedCollectionId = testOriginalSharedCollectionId,
            sharedCollectionId = testSharedCollectionId,
            sharedCollectionSubscribed = testSharedCollectionSubscribed)))

  def createCollectionSeq(num: Int) =
    List.tabulate(num)(
      i =>
        Collection(
          id = testCollectionId + i,
          data = CollectionData(
            position = testPosition,
            name = testName,
            collectionType = testCollectionType,
            icon = testIcon,
            themedColorIndex = testThemedColorIndex,
            appsCategory = testAppsCategoryOption,
            originalSharedCollectionId = testOriginalSharedCollectionIdOption,
            sharedCollectionId = testSharedCollectionIdOption,
            sharedCollectionSubscribed = testSharedCollectionSubscribedOption)))

  def createCollectionValues =
    Map[String, Any](
      CollectionEntity.position                   -> testPosition,
      CollectionEntity.name                       -> testName,
      CollectionEntity.collectionType             -> testCollectionType,
      CollectionEntity.icon                       -> testIcon,
      CollectionEntity.themedColorIndex           -> testThemedColorIndex,
      CollectionEntity.appsCategory               -> (testAppsCategoryOption orNull),
      CollectionEntity.originalSharedCollectionId -> (testOriginalSharedCollectionIdOption orNull),
      CollectionEntity.sharedCollectionId         -> (testSharedCollectionIdOption orNull),
      CollectionEntity.sharedCollectionSubscribed -> (testSharedCollectionSubscribedOption getOrElse false))

  def createCollectionData =
    CollectionData(
      position = testPosition,
      name = testName,
      collectionType = testCollectionType,
      icon = testIcon,
      themedColorIndex = testThemedColorIndex,
      appsCategory = testAppsCategoryOption,
      originalSharedCollectionId = testOriginalSharedCollectionIdOption,
      sharedCollectionId = testSharedCollectionIdOption,
      sharedCollectionSubscribed = testSharedCollectionSubscribedOption)
}
