package com.fortysevendeg.ninecardslauncher.process.commons.models

import android.content.Intent
import android.content.Intent._
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentExtras._
import play.api.libs.json._

import scala.collection.JavaConversions._
import scala.util.Try

case class NineCardIntent(intentExtras: NineCardIntentExtras) extends Intent {

  def fill(intent: Intent) = fillIn(
    intent,
    FILL_IN_ACTION | FILL_IN_DATA | FILL_IN_CATEGORIES | FILL_IN_COMPONENT | FILL_IN_PACKAGE |
      FILL_IN_SOURCE_BOUNDS | FILL_IN_SELECTOR | FILL_IN_CLIP_DATA)

  def extractPackageName(): Option[String] =
    Option(intentExtras.package_name.getOrElse(getStringExtra(nineCardExtraPackageName)))

  def extractClassName(): Option[String] =
    Option(intentExtras.class_name.getOrElse(getStringExtra(nineCardExtraClassName)))

  def extraLookup(): Option[String] =
    Option(intentExtras.contact_lookup_key.getOrElse(getStringExtra(nineCardExtraLookup)))

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

object NineCardIntentExtras {
  val nineCardExtraLookup: String = "contact_lookup_key"
  val nineCardExtraPhone: String = "tel"
  val nineCardExtraEmail: String = "email"
  val nineCardExtraUrlAd: String = "url_ad"
  val nineCardExtraPackageName: String = "package_name"
  val nineCardExtraClassName: String = "class_name"
  val openApp: String = "com.fortysevendeg.ninecardslauncher.OPEN_APP"
  val openNoInstalledApp: String = "com.fortysevendeg.ninecardslauncher.OPEN_RECOMMENDED_APP"
  val openSms: String = "com.fortysevendeg.ninecardslauncher.OPEN_SMS"
  val openPhone: String = "com.fortysevendeg.ninecardslauncher.OPEN_PHONE"
  val openEmail: String = "com.fortysevendeg.ninecardslauncher.OPEN_EMAIL"
  val openContact: String = "com.fortysevendeg.ninecardslauncher.OPEN_CONTACT"
}

object NineCardIntentImplicits {

  implicit val extrasReads = Json.reads[NineCardIntentExtras]

  implicit val extrasWrites = Json.writes[NineCardIntentExtras]

  implicit val nineCardIntentReads = new Reads[NineCardIntent] {
    def reads(js: JsValue): JsResult[NineCardIntent] = {
      val intent = NineCardIntent((js \ "intentExtras").as[NineCardIntentExtras])
      (for {
        packageName <- (js \ "packageName").asOpt[String]
        className <- (js \ "className").asOpt[String]
      } yield {
          intent.setClassName(packageName, className)
        }) getOrElse {
        (js \ "packageName").asOpt[String] foreach intent.setPackage
      }
      // We have to set data and type values together because Android SDK
      // clear type value when data values is set and vice versa
      (for {
        dataString <- (js \ "dataString").asOpt[String]
        typeString <- (js \ "type").asOpt[String]
      } yield {
          intent.setDataAndTypeAndNormalize(Uri.parse(dataString), typeString)
        }) getOrElse {
        (js \ "dataString").asOpt[String] map (dataString => intent.setDataAndNormalize(Uri.parse(dataString))) getOrElse {
          (js \ "type").asOpt[String] foreach intent.setTypeAndNormalize
        }
      }
      (js \ "categories").asOpt[List[String]] foreach (_ foreach intent.addCategory)
      (js \ "action").asOpt[String] map intent.setAction
      (js \ "extras").asOpt[JsObject] foreach (_.value map {
        case (name, value) => matchExtras(intent, name, value)
      })
      (js \ "flags").asOpt[Int] foreach intent.setFlags
      JsSuccess(intent)
    }

    def matchExtras(intent: NineCardIntent, name: String, value: JsValue) = value match {
      case s:JsString => intent.putExtra(name, s.as[String])
      case s:JsNumber => intent.putExtra(name, s.as[Int])
      case s:JsBoolean => intent.putExtra(name, s.as[Boolean])
      case _ =>
    }

  }

  implicit val nineCardIntentWrites = new Writes[NineCardIntent] {
    def writes(intent: NineCardIntent): JsValue = {
      val jsExtras = Option(intent.getExtras) map { extras =>
        (extras.keySet() flatMap { key =>
          Try {
            extras.get(key).asInstanceOf[Any] match {
              case s: String => key -> Json.toJsFieldJsValueWrapper(s)
              case i: Int => key -> Json.toJsFieldJsValueWrapper(i)
              case b: Boolean => key -> Json.toJsFieldJsValueWrapper(b)
              case f: Float => key -> Json.toJsFieldJsValueWrapper(f)
              case d: Double => key -> Json.toJsFieldJsValueWrapper(d)
            }
          }.toOption
        }).toSeq
      }
      val extras = jsExtras map (e => Json.obj(e :_*)) getOrElse Json.obj()
      val categoriesSet = Option(intent.getCategories) map (_.toSet map JsString) getOrElse Set.empty
      val categories = JsArray(categoriesSet.toSeq)
      Json.obj(
        "intentExtras" -> Json.toJsFieldJsValueWrapper(intent.intentExtras),
        "className" -> Json.toJsFieldJsValueWrapper((Option(intent.getComponent) map (_.getClassName)).orNull),
        "packageName" -> Json.toJsFieldJsValueWrapper((Option(intent.getComponent) map (_.getPackageName)).orNull),
        "categories" -> Json.toJsFieldJsValueWrapper(categories),
        "action" -> Json.toJsFieldJsValueWrapper(intent.getAction),
        "extras" -> Json.toJsFieldJsValueWrapper(extras),
        "flags" -> Json.toJsFieldJsValueWrapper(intent.getFlags),
        "type" -> Json.toJsFieldJsValueWrapper(intent.getType),
        "dataString" -> Json.toJsFieldJsValueWrapper((Option(intent.getData) map (_.toString)).orNull))
    }
  }
}