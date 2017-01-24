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

package cards.nine.repository.card

import cards.nine.repository.model.{Card, CardData, CardsWithCollectionId}
import cards.nine.repository.provider.{CardEntity, CardEntityData}

import scala.util.Random

trait CardRepositoryTestData {

  val testCardId                  = Random.nextInt(10)
  val testNonExistingCardId       = 15
  val testPosition                = Random.nextInt(10)
  val testCollectionId            = Random.nextInt(10)
  val testNonExistingCollectionId = 15
  val testTerm                    = Random.nextString(5)
  val testPackageName             = Random.nextString(5)
  val testType                    = Random.nextString(5)
  val testIntent                  = Random.nextString(5)
  val testImagePath               = Random.nextString(5)
  val testStarRating              = Random.nextDouble()
  val testNumDownloads            = Random.nextString(10)
  val testNotification            = Random.nextString(10)
  val testPackageNameOption       = Option(testPackageName)
  val testImagePathOption         = Option(testImagePath)
  val testNotificationOption      = Option(testNotification)
  val testMockWhere               = "mocked-where"

  val cardEntitySeq            = createCardEntitySeq(5)
  val cardEntity               = cardEntitySeq(0)
  val cardSeq                  = createCardSeq(5)
  val cardDataSeq              = cardSeq map (_.data)
  val cardsWithCollectionIdSeq = createCardWithCollectionIdSeq(5)
  val cardIdSeq                = cardSeq map (_.id)
  val card                     = cardSeq(0)

  def createCardEntitySeq(num: Int) =
    List.tabulate(num)(
      i =>
        CardEntity(
          id = testCardId + i,
          data = CardEntityData(
            position = testPosition,
            collectionId = testCollectionId,
            term = testTerm,
            packageName = testPackageName,
            `type` = testType,
            intent = testIntent,
            imagePath = testImagePath,
            notification = testNotification)))

  def createCardSeq(num: Int) =
    List.tabulate(num)(
      i =>
        Card(
          id = testCardId + i,
          data = CardData(
            position = testPosition,
            term = testTerm,
            packageName = testPackageNameOption,
            cardType = testType,
            intent = testIntent,
            imagePath = testImagePathOption,
            notification = testNotificationOption)))

  def createInsertCardValues =
    Map[String, Any](
      CardEntity.position     -> testPosition,
      CardEntity.collectionId -> testCollectionId,
      CardEntity.term         -> testTerm,
      CardEntity.packageName  -> (testPackageNameOption orNull),
      CardEntity.cardType     -> testType,
      CardEntity.intent       -> testIntent,
      CardEntity.imagePath    -> (testImagePathOption orNull),
      CardEntity.notification -> (testNotificationOption orNull))

  def createUpdateCardValues =
    Map[String, Any](
      CardEntity.position     -> testPosition,
      CardEntity.term         -> testTerm,
      CardEntity.packageName  -> (testPackageNameOption orNull),
      CardEntity.cardType     -> testType,
      CardEntity.intent       -> testIntent,
      CardEntity.imagePath    -> (testImagePathOption orNull),
      CardEntity.notification -> (testNotificationOption orNull))

  def createCardData =
    CardData(
      position = testPosition,
      term = testTerm,
      packageName = testPackageNameOption,
      cardType = testType,
      intent = testIntent,
      imagePath = testImagePathOption,
      notification = testNotificationOption)

  def createCardWithCollectionIdSeq(num: Int) = List.tabulate(num) { i =>
    CardsWithCollectionId(collectionId = testCollectionId, data = cardDataSeq)
  }
}
