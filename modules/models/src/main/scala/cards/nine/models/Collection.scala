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

package cards.nine.models

import cards.nine.models.types._

case class Collection(
    id: Int,
    position: Int,
    name: String,
    collectionType: CollectionType,
    icon: String,
    themedColorIndex: Int,
    appsCategory: Option[NineCardsCategory] = None,
    cards: Seq[Card] = Seq.empty,
    moment: Option[Moment] = None,
    originalSharedCollectionId: Option[String] = None,
    sharedCollectionId: Option[String] = None,
    sharedCollectionSubscribed: Boolean,
    publicCollectionStatus: PublicCollectionStatus)
    extends Serializable

case class CollectionData(
    position: Int = 0,
    name: String,
    collectionType: CollectionType,
    icon: String,
    themedColorIndex: Int,
    appsCategory: Option[NineCardsCategory] = None,
    cards: Seq[CardData] = Seq.empty,
    moment: Option[MomentData] = None,
    originalSharedCollectionId: Option[String] = None,
    sharedCollectionId: Option[String] = None,
    sharedCollectionSubscribed: Boolean = false,
    publicCollectionStatus: PublicCollectionStatus = NotPublished)
    extends Serializable

object Collection {

  implicit class CollectionOps(collection: Collection) {

    def toData =
      CollectionData(
        position = collection.position,
        name = collection.name,
        collectionType = collection.collectionType,
        icon = collection.icon,
        themedColorIndex = collection.themedColorIndex,
        appsCategory = collection.appsCategory,
        cards = collection.cards map (_.toData),
        moment = collection.moment map (_.toData),
        originalSharedCollectionId = collection.originalSharedCollectionId,
        sharedCollectionId = collection.sharedCollectionId,
        sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
        publicCollectionStatus = collection.publicCollectionStatus)

  }
}
