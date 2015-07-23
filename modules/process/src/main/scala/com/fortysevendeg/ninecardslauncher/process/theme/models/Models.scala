package com.fortysevendeg.ninecardslauncher.process.theme.models

import android.graphics.Color
import play.api.libs.json._

case class NineCardsTheme(name: String, styles: Seq[ThemeStyle]) {
  def get(style: ThemeStyleType): Int = styles.find(_.styleType == style) map (_.color) getOrElse Color.TRANSPARENT
}


case class ThemeStyle(styleType: ThemeStyleType, color: Int)

sealed trait ThemeStyleType

case object AppDrawerPressedColor extends ThemeStyleType

case object CollectionDetailBackgroundColor extends ThemeStyleType

case object CollectionDetailCardBackgroundColor extends ThemeStyleType

case object CollectionDetailCardBackgroundPressedColor extends ThemeStyleType

case object CollectionDetailTextCardColor extends ThemeStyleType

case object CollectionDetailTextTabDefaultColor extends ThemeStyleType

case object CollectionDetailTextTabSelectedColor extends ThemeStyleType

case object SearchBackgroundColor extends ThemeStyleType

case object SearchGoogleColor extends ThemeStyleType

case object SearchIconsColor extends ThemeStyleType

case object SearchPressedColor extends ThemeStyleType

object NineCardsThemeImplicits {

  implicit val themeStyleTypeReads = new Reads[ThemeStyleType] {

    def reads(js: JsValue) = js.as[String] match {
      case "AppDrawerPressedColor" => JsSuccess(AppDrawerPressedColor)
      case "CollectionDetailBackgroundColor" => JsSuccess(CollectionDetailBackgroundColor)
      case "CollectionDetailCardBackgroundColor" => JsSuccess(CollectionDetailCardBackgroundColor)
      case "CollectionDetailCardBackgroundPressedColor" => JsSuccess(CollectionDetailCardBackgroundPressedColor)
      case "CollectionDetailTextCardColor" => JsSuccess(CollectionDetailTextCardColor)
      case "CollectionDetailTextTabDefaultColor" => JsSuccess(CollectionDetailTextTabDefaultColor)
      case "CollectionDetailTextTabSelectedColor" => JsSuccess(CollectionDetailTextTabSelectedColor)
      case "SearchBackgroundColor" => JsSuccess(SearchBackgroundColor)
      case "SearchGoogleColor" => JsSuccess(SearchGoogleColor)
      case "SearchIconsColor" => JsSuccess(SearchIconsColor)
      case "SearchPressedColor" => JsSuccess(SearchPressedColor)
      case _ => JsError("Theme style type not allowed")
    }
  }

  implicit val themeStyleTypeWrites = new Writes[ThemeStyleType] {

    def writes(styleType: ThemeStyleType) = styleType match {
      case AppDrawerPressedColor => Json.toJson("AppDrawerPressedColor")
      case CollectionDetailBackgroundColor => Json.toJson("CollectionDetailBackgroundColor")
      case CollectionDetailCardBackgroundColor => Json.toJson("CollectionDetailCardBackgroundColor")
      case CollectionDetailCardBackgroundPressedColor => Json.toJson("CollectionDetailCardBackgroundPressedColor")
      case CollectionDetailTextCardColor => Json.toJson("CollectionDetailTextCardColor")
      case CollectionDetailTextTabDefaultColor => Json.toJson("CollectionDetailTextTabDefaultColor")
      case CollectionDetailTextTabSelectedColor => Json.toJson("CollectionDetailTextTabSelectedColor")
      case SearchBackgroundColor => Json.toJson("SearchBackgroundColor")
      case SearchGoogleColor => Json.toJson("SearchGoogleColor")
      case SearchIconsColor => Json.toJson("SearchIconsColor")
      case SearchPressedColor => Json.toJson("SearchPressedColor")
    }
  }

  implicit val themeStyleReads = new Reads[ThemeStyle] {

    def reads(js: JsValue) = {

      val themeStyleType = (js \ "styleType").as[ThemeStyleType]

      val themeStyleColor = Color.parseColor((js \ "color").as[String])

      JsSuccess(ThemeStyle(styleType = themeStyleType, color = themeStyleColor))
    }
  }

  implicit val themeStyleWrites = new Writes[ThemeStyle] {

    def writes(themeStyle: ThemeStyle) =
      Json.obj(
        "styleType" -> Json.toJsFieldJsValueWrapper(themeStyle.styleType),
        "color" -> Json.toJsFieldJsValueWrapper(Integer.toHexString(themeStyle.color))
      )
  }

  implicit val nineCardsThemeReads = Json.reads[NineCardsTheme]
}
