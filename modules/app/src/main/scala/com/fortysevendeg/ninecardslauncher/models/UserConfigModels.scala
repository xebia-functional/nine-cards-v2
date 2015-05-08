package com.fortysevendeg.ninecardslauncher.models

import android.content.{ComponentName, Intent}
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsIntent._
import macroid.AppContext
import play.api.libs.json._
import scala.collection.JavaConversions._

case class UserConfig(
  _id: String,
  email: String,
  plusProfile: UserConfigPlusProfile,
  devices: Seq[UserConfigDevice],
  geoInfo: UserConfigGeoInfo,
  status: UserConfigStatusInfo)

case class UserConfigPlusProfile(
  displayName: String,
  profileImage: UserConfigProfileImage)

case class UserConfigDevice(
  deviceId: String,
  deviceName: String,
  collections: Seq[UserConfigCollection])

case class UserConfigGeoInfo(
  homeMorning: Option[UserConfigUserLocation],
  homeNight: Option[UserConfigUserLocation],
  work: Option[UserConfigUserLocation],
  current: Option[UserConfigUserLocation])

case class UserConfigStatusInfo(
  products: Seq[String],
  friendsReferred: Int,
  themesShared: Int,
  collectionsShared: Int,
  customCollections: Int,
  earlyAdopter: Boolean,
  communityMember: Boolean,
  joinedThrough: Option[String],
  tester: Boolean)

case class UserConfigProfileImage(
  imageType: Int,
  imageUrl: String,
  secureUrl: String)

case class UserConfigCollection(
  name: String,
  originalSharedCollectionId: Option[String],
  sharedCollectionId: Option[String],
  sharedCollectionSubscribed: Option[Boolean],
  items: Seq[UserConfigCollectionItem],
  collectionType: String,
  constrains: Seq[String],
  wifi: Seq[String],
  occurrence: Seq[String],
  icon: String,
  radius: Int,
  lat: Double,
  lng: Double,
  alt: Double,
  category: Option[String])

case class UserConfigCollectionItem(
  itemType: String,
  title: String,
  metadata: NineCardIntent,
  categories: Option[Seq[String]])

case class NineCardIntent(
  intentExtras: Map[String, String]) extends Intent {

  def this(intent: Intent, intentExtras: Map[String, String]) = this(intentExtras)

  def extractPackageName(): Option[String] =
    Option(intentExtras.getOrElse(NineCardExtraPackageName, getStringExtra(NineCardExtraPackageName)))

  def extractClassName(): Option[String] =
    Option(intentExtras.getOrElse(NineCardExtraClassName, getStringExtra(NineCardExtraClassName)))

  def execute(implicit appContext: AppContext) =
    getAction match {
      case OpenApp | OpenRecommendedApp | OpenSms | OpenPhone | OpenEmail =>
        appContext.get.sendBroadcast(toIntent())
      case _ => appContext.get.startActivity(this)
    }

  def toIntent(): Intent = (for {
    packageName <- extractPackageName()
    className <- extractClassName()
  } yield {
      val intent = new Intent(this)
      intent.putExtra(NineCardExtraPackageName, packageName)
      intent.putExtra(NineCardExtraClassName, className)
      intent
    }) getOrElse this

}

object NineCardIntentImplicits {
//  implicit val reads = Json.reads[NineCardIntent]
  implicit val nineCardIntentReads = new Reads[NineCardIntent] {
    def reads(js: JsValue): JsResult[NineCardIntent] = {
      val intent = NineCardIntent(
        (js \ "intentExtras").as[Map[String, String]]
      )
      for {
        packageName <- (js \ "packageName").asOpt[String]
        className <- (js \ "className").asOpt[String]
      } yield {
        intent.setClassName(packageName, className)
      }
      (js \ "categories").asOpt[List[String]] map {
        categories =>
          categories map intent.addCategory
      }
      (js \ "action").asOpt[String] map intent.setAction getOrElse intent.setAction("android.intent.action.VIEW")
      (js \ "dataString").asOpt[String] map (dataString => intent.setData(Uri.parse(dataString)))
      (js \ "extras").asOpt[JsObject] map {
        extras =>
          (extras \ "pairValue").asOpt[String] map (pairValue => intent.putExtra("pairValue", pairValue))
          (extras \ "empty").asOpt[Boolean] map (empty => intent.putExtra("empty", empty))
          (extras \ "parcelled").asOpt[Boolean] map (parcelled => intent.putExtra("parcelled", parcelled))
      }
      (js \ "flags").asOpt[Int] map (flags => intent.setFlags(flags))
      (js \ "type").asOpt[String] map (t => intent.setType(t))
      JsSuccess(intent)
    }
  }
//  implicit val writes = Json.writes[NineCardIntent]
  implicit val nineCardIntentWrites = new Writes[NineCardIntent] {
    def writes(intent: NineCardIntent): JsValue = {
      val extras = Json.obj(
        "pairValue" -> Json.toJsFieldJsValueWrapper(intent.getStringExtra("pairValue")),
        "empty" -> Json.toJsFieldJsValueWrapper(intent.getBooleanExtra("empty", false)),
        "parcelled" -> Json.toJsFieldJsValueWrapper(intent.getBooleanExtra("parcelled", false))
      )
      val categoriesSet = Option(intent.getCategories) map {
        categories =>
          categories.toSet map JsString
      } getOrElse Set.empty
      val categories = JsArray(categoriesSet.toSeq)
      Json.obj(
        "intentExtras" -> Json.toJsFieldJsValueWrapper(intent.intentExtras),
        "className" -> Json.toJsFieldJsValueWrapper((Option(intent.getComponent) map (_.getClassName)).orNull),
        "packageName" -> Json.toJsFieldJsValueWrapper((Option(intent.getComponent) map (_.getPackageName)).orNull),
        "categories" -> Json.toJsFieldJsValueWrapper(categories),
        "action" -> Json.toJsFieldJsValueWrapper(intent.getAction),
        "extras" -> Json.toJsFieldJsValueWrapper(extras),
        "flags" -> Json.toJsFieldJsValueWrapper(intent.getFlags),
        "type" -> Json.toJsFieldJsValueWrapper(intent.getType)
      )
    }
  }
}

case class UserConfigUserLocation(
  wifi: String,
  lat: Double,
  lng: Double,
  occurrence: Seq[UserConfigTimeSlot])

case class UserConfigTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])