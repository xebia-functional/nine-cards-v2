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

import cards.nine.models.types.{CardType, CollectionType, NineCardsCategory}
import play.api.libs.json._

case class Device(name: String, deviceId: String, secretToken: String, permissions: Seq[String])

case class LoginResponseV1(
    userId: Option[String],
    sessionToken: Option[String],
    email: Option[String],
    devices: Seq[Device])

case class UserV1(
    _id: String,
    email: String,
    plusProfile: UserV1PlusProfile,
    devices: Seq[UserV1Device],
    status: UserV1StatusInfo)

case class UserV1Collection(
    name: String,
    originalSharedCollectionId: Option[String],
    sharedCollectionId: Option[String],
    sharedCollectionSubscribed: Option[Boolean],
    items: Seq[UserV1CollectionItem],
    collectionType: CollectionType,
    constrains: Seq[String],
    wifi: Seq[String],
    occurrence: Seq[String],
    icon: String,
    category: Option[NineCardsCategory])

case class UserV1CollectionItem(
    itemType: CardType,
    title: String,
    intent: String,
    categories: Option[Seq[NineCardsCategory]])

case class UserV1Device(deviceId: String, deviceName: String, collections: Seq[UserV1Collection])

case class UserV1PlusProfile(displayName: String, profileImage: UserV1ProfileImage)

case class UserV1ProfileImage(imageType: Int, imageUrl: String)

case class UserV1StatusInfo(
    products: Seq[String],
    friendsReferred: Int,
    themesShared: Int,
    collectionsShared: Int,
    customCollections: Int,
    earlyAdopter: Boolean,
    communityMember: Boolean,
    joinedThrough: Option[String],
    tester: Boolean)
