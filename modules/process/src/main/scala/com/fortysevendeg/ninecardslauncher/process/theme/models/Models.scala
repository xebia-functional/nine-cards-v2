package com.fortysevendeg.ninecardslauncher.process.theme.models

import android.graphics.Color
import play.api.libs.json._

case class NineCardsTheme(name: String, styles: Seq[ThemeStyle]) {
  def get(style: ThemeStyleType): Int = styles.find(_.styleType == style) map (_.color) getOrElse Color.TRANSPARENT
}

case class ThemeStyle(styleType: ThemeStyleType, color: Int)

sealed trait ThemeStyleType

case object PrimaryColor extends ThemeStyleType

case object AppDrawerPressedColor extends ThemeStyleType

case object CollectionDetailBackgroundColor extends ThemeStyleType

case object CollectionDetailCardBackgroundColor extends ThemeStyleType

case object CollectionDetailCardBackgroundPressedColor extends ThemeStyleType

case object CollectionDetailTextCardColor extends ThemeStyleType

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

case object EditCollectionNameTextColor extends ThemeStyleType

case object EditCollectionNameHintTextColor extends ThemeStyleType

object NineCardsThemeImplicits {

  implicit val themeStyleTypeReads = new Reads[ThemeStyleType] {

    def reads(js: JsValue) = js.as[String] match {
      case "PrimaryColor" => JsSuccess(PrimaryColor)
      case "AppDrawerPressedColor" => JsSuccess(AppDrawerPressedColor)
      case "CollectionDetailBackgroundColor" => JsSuccess(CollectionDetailBackgroundColor)
      case "CollectionDetailCardBackgroundColor" => JsSuccess(CollectionDetailCardBackgroundColor)
      case "CollectionDetailCardBackgroundPressedColor" => JsSuccess(CollectionDetailCardBackgroundPressedColor)
      case "CollectionDetailTextCardColor" => JsSuccess(CollectionDetailTextCardColor)
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
      case "EditCollectionNameTextColor" => JsSuccess(EditCollectionNameTextColor)
      case "EditCollectionNameHintTextColor" => JsSuccess(EditCollectionNameHintTextColor)
      case _ => JsError("Theme style type not allowed")
    }
  }

  implicit val themeStyleTypeWrites = new Writes[ThemeStyleType] {

    def writes(styleType: ThemeStyleType) = styleType match {
      case PrimaryColor => Json.toJson("PrimaryColor")
      case AppDrawerPressedColor => Json.toJson("AppDrawerPressedColor")
      case CollectionDetailBackgroundColor => Json.toJson("CollectionDetailBackgroundColor")
      case CollectionDetailCardBackgroundColor => Json.toJson("CollectionDetailCardBackgroundColor")
      case CollectionDetailCardBackgroundPressedColor => Json.toJson("CollectionDetailCardBackgroundPressedColor")
      case CollectionDetailTextCardColor => Json.toJson("CollectionDetailTextCardColor")
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
      case EditCollectionNameTextColor => Json.toJson("EditCollectionNameTextColor")
      case EditCollectionNameHintTextColor => Json.toJson("EditCollectionNameHintTextColor")
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
