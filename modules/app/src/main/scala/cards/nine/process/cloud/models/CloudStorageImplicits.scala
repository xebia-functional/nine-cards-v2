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

package cards.nine.process.cloud.models

import cards.nine.models._
import cards.nine.models.types.json._

object CloudStorageImplicits {

  import play.api.libs.json._
  import NineCardCategoryImplicits._
  import NineCardsMomentImplicits._
  import CollectionTypeImplicits._
  import DockTypeImplicits._
  import WidgetTypeImplicits._

  implicit val cloudStorageWidgetAreaReads = Json.reads[CloudStorageWidgetArea]
  implicit val cloudStorageWidgetReads     = Json.reads[CloudStorageWidget]
  implicit val cloudStorageDockAppReads    = Json.reads[CloudStorageDockApp]
  implicit val cloudStorageMomentTimeSlotReads =
    Json.reads[CloudStorageMomentTimeSlot]
  implicit val cloudStorageMomentReads = Json.reads[CloudStorageMoment]
  implicit val cloudStorageCollectionItemReads =
    Json.reads[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionReads = Json.reads[CloudStorageCollection]
  implicit val cloudStorageDeviceReads     = Json.reads[CloudStorageDeviceData]

  implicit val cloudStorageWidgetAreaWrites =
    Json.writes[CloudStorageWidgetArea]
  implicit val cloudStorageWidgetWrites  = Json.writes[CloudStorageWidget]
  implicit val cloudStorageDockAppWrites = Json.writes[CloudStorageDockApp]
  implicit val cloudStorageMomentTimeSlotWrites =
    Json.writes[CloudStorageMomentTimeSlot]
  implicit val cloudStorageMomentWrites = Json.writes[CloudStorageMoment]
  implicit val cloudStorageCollectionItemWrites =
    Json.writes[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionWrites =
    Json.writes[CloudStorageCollection]
  implicit val cloudStorageDeviceWrites = Json.writes[CloudStorageDeviceData]

}
