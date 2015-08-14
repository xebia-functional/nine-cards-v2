package com.fortysevendeg.ninecardslauncher.process.collection.models

import android.content.Intent
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import play.api.libs.json._

import scala.collection.JavaConversions._

case class UnformedItem(
  name: String,
  packageName: String,
  className: String,
  imagePath: String,
  category: String,
  starRating: Double,
  numDownloads: String,
  ratingsCount: Int,
  commentCount: Int)

case class FormedCollection(
  name: String,
  originalSharedCollectionId: Option[String],
  sharedCollectionId: Option[String],
  sharedCollectionSubscribed: Option[Boolean],
  items: Seq[FormedItem],
  collectionType: String,
  constrains: Seq[String],
  icon: String,
  category: Option[String])

case class FormedItem(
  itemType: String,
  title: String,
  intent: String)

case class NineCardIntent(intentExtras: NineCardIntentExtras) extends Intent {

  def this(intent: Intent, intentExtras: NineCardIntentExtras) = this(intentExtras)

  def extractPackageName(): Option[String] =
    Option(intentExtras.package_name.getOrElse(getStringExtra(nineCardExtraPackageName)))

  def extractClassName(): Option[String] =
    Option(intentExtras.class_name.getOrElse(getStringExtra(nineCardExtraClassName)))

  def extraLookup(): Option[String] =
    Option(intentExtras.tel.getOrElse(getStringExtra(nineCardExtraLookup)))

  def extractPhone(): Option[String] =
    Option(intentExtras.tel.getOrElse(getStringExtra(nineCardExtraPhone)))

  def extractEmail(): Option[String] =
    Option(intentExtras.email.getOrElse(getStringExtra(nineCardExtraEmail)))

  def extractUrlAd(): Option[String] =
    Option(intentExtras.url_ad.getOrElse(getStringExtra(nineCardExtraUrlAd)))

  def toIntent: Intent = {
    val intent = new Intent(this)
    extractPackageName() map (packageName => intent.putExtra(nineCardExtraPackageName, packageName))
    extractClassName() map (className => intent.putExtra(nineCardExtraClassName, className))
    extraLookup() map (lookupKey => intent.putExtra(nineCardExtraLookup, lookupKey))
    extractPhone() map (phone => intent.putExtra(nineCardExtraPhone, phone))
    extractEmail() map (email => intent.putExtra(nineCardExtraEmail, email))
    extractUrlAd() map (urlAd => intent.putExtra(nineCardExtraUrlAd, urlAd))
    intent
  }
}

case class NineCardIntentExtras(
  contact_lookup_key: Option[String] = None,
  tel: Option[String] = None,
  email: Option[String] = None,
  url_ad: Option[String] = None,
  package_name: Option[String] = None,
  class_name: Option[String] = None)

object NineCardsIntentExtras {
  val nineCardExtraLookup: String = "contact_lookup_key"
  val nineCardExtraPhone: String = "tel"
  val nineCardExtraEmail: String = "email"
  val nineCardExtraUrlAd: String = "url_ad"
  val nineCardExtraPackageName: String = "package_name"
  val nineCardExtraClassName: String = "class_name"
  val openApp: String = "com.fortysevendeg.ninecardslauncher.OPEN_APP"
  val openRecommendedApp: String = "com.fortysevendeg.ninecardslauncher.OPEN_RECOMMENDED_APP"
  val openSms: String = "com.fortysevendeg.ninecardslauncher.OPEN_SMS"
  val openPhone: String = "com.fortysevendeg.ninecardslauncher.OPEN_PHONE"
  val openEmail: String = "com.fortysevendeg.ninecardslauncher.OPEN_EMAIL"
}

object NineCardIntentImplicits {

  implicit val extrasReads = Json.reads[NineCardIntentExtras]

  implicit val extrasWrites = Json.writes[NineCardIntentExtras]

  implicit val nineCardIntentReads = new Reads[NineCardIntent] {
    def reads(js: JsValue): JsResult[NineCardIntent] = {
      val intent = NineCardIntent(
        (js \ "intentExtras").as[NineCardIntentExtras]
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