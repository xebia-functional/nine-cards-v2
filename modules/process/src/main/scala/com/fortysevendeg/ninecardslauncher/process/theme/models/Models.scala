package com.fortysevendeg.ninecardslauncher.process.theme.models

import android.graphics.Color
import play.api.libs.json._

case class NineCardsTheme(name: String, styles: Seq[ThemeStyle]) {
  def get(style: ThemeStyleType): Int = styles.find(_.styleType == style) map (_.color) getOrElse {
    android.util.Log.i("9Cards", s"The selected theme doesn't have the $style property")
    Color.TRANSPARENT
  }
}

case class ThemeStyle(styleType: ThemeStyleType, color: Int)

sealed trait ThemeStyleType

case object PrimaryColor extends ThemeStyleType

case object DockPressedColor extends ThemeStyleType

case object CardLayoutBackgroundColor extends ThemeStyleType

case object CardBackgroundColor extends ThemeStyleType

case object CardBackgroundPressedColor extends ThemeStyleType

case object CardTextColor extends ThemeStyleType

case object CollectionDetailTextTabDefaultColor extends ThemeStyleType

case object CollectionDetailTextTabSelectedColor extends ThemeStyleType

case object DrawerTabsBackgroundColor extends ThemeStyleType

case object DrawerBackgroundColor extends ThemeStyleType

case object DrawerTextColor extends ThemeStyleType

case object SearchBackgroundColor extends ThemeStyleType

case object SearchGoogleColor extends ThemeStyleType

case object SearchIconsColor extends ThemeStyleType

case object SearchTextColor extends ThemeStyleType

case object SearchPressedColor extends ThemeStyleType

case object DrawerIconColor extends ThemeStyleType

object NineCardsThemeImplicits {

  implicit val themeStyleTypeReads = new Reads[ThemeStyleType] {

    def reads(js: JsValue) = js.as[String] match {
      case "PrimaryColor" => JsSuccess(PrimaryColor)
      case "DockPressedColor" => JsSuccess(DockPressedColor)
      case "CardLayoutBackgroundColor" => JsSuccess(CardLayoutBackgroundColor)
      case "CardBackgroundColor" => JsSuccess(CardBackgroundColor)
      case "CardBackgroundPressedColor" => JsSuccess(CardBackgroundPressedColor)
      case "CardTextColor" => JsSuccess(CardTextColor)
      case "CollectionDetailTextTabDefaultColor" => JsSuccess(CollectionDetailTextTabDefaultColor)
      case "CollectionDetailTextTabSelectedColor" => JsSuccess(CollectionDetailTextTabSelectedColor)
      case "DrawerTabsBackgroundColor" => JsSuccess(DrawerTabsBackgroundColor)
      case "DrawerBackgroundColor" => JsSuccess(DrawerBackgroundColor)
      case "DrawerTextColor" => JsSuccess(DrawerTextColor)
      case "SearchBackgroundColor" => JsSuccess(SearchBackgroundColor)
      case "SearchGoogleColor" => JsSuccess(SearchGoogleColor)
      case "SearchIconsColor" => JsSuccess(SearchIconsColor)
      case "SearchTextColor" => JsSuccess(SearchTextColor)
      case "SearchPressedColor" => JsSuccess(SearchPressedColor)
      case "DrawerIconColor" => JsSuccess(DrawerIconColor)
      case _ => JsError("Theme style type not allowed")
    }
  }

  implicit val themeStyleTypeWrites = new Writes[ThemeStyleType] {

    def writes(styleType: ThemeStyleType) = styleType match {
      case PrimaryColor => Json.toJson("PrimaryColor")
      case DockPressedColor => Json.toJson("DockPressedColor")
      case CardLayoutBackgroundColor => Json.toJson("CardLayoutBackgroundColor")
      case CardBackgroundColor => Json.toJson("CardBackgroundColor")
      case CardBackgroundPressedColor => Json.toJson("CardBackgroundPressedColor")
      case CardTextColor => Json.toJson("CardTextColor")
      case CollectionDetailTextTabDefaultColor => Json.toJson("CollectionDetailTextTabDefaultColor")
      case CollectionDetailTextTabSelectedColor => Json.toJson("CollectionDetailTextTabSelectedColor")
      case DrawerTabsBackgroundColor => Json.toJson("DrawerTabsBackgroundColor")
      case DrawerBackgroundColor => Json.toJson("DrawerBackgroundColor")
      case DrawerTextColor => Json.toJson("DrawerTextColor")
      case SearchBackgroundColor => Json.toJson("SearchBackgroundColor")
      case SearchGoogleColor => Json.toJson("SearchGoogleColor")
      case SearchIconsColor => Json.toJson("SearchIconsColor")
      case SearchTextColor => Json.toJson("SearchTextColor")
      case SearchPressedColor => Json.toJson("SearchPressedColor")
      case DrawerIconColor => Json.toJson("DrawerIconColor")
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
