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

  implicit val cloudStorageWidgetAreaReads= Json.reads[CloudStorageWidgetArea]
  implicit val cloudStorageWidgetReads = Json.reads[CloudStorageWidget]
  implicit val cloudStorageDockAppReads = Json.reads[CloudStorageDockApp]
  implicit val cloudStorageMomentTimeSlotReads = Json.reads[CloudStorageMomentTimeSlot]
  implicit val cloudStorageMomentReads = Json.reads[CloudStorageMoment]
  implicit val cloudStorageCollectionItemReads = Json.reads[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionReads = Json.reads[CloudStorageCollection]
  implicit val cloudStorageDeviceReads = Json.reads[CloudStorageDeviceData]

  implicit val cloudStorageWidgetAreaWrites = Json.writes[CloudStorageWidgetArea]
  implicit val cloudStorageWidgetWrites = Json.writes[CloudStorageWidget]
  implicit val cloudStorageDockAppWrites = Json.writes[CloudStorageDockApp]
  implicit val cloudStorageMomentTimeSlotWrites = Json.writes[CloudStorageMomentTimeSlot]
  implicit val cloudStorageMomentWrites = Json.writes[CloudStorageMoment]
  implicit val cloudStorageCollectionItemWrites = Json.writes[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionWrites = Json.writes[CloudStorageCollection]
  implicit val cloudStorageDeviceWrites = Json.writes[CloudStorageDeviceData]

}
