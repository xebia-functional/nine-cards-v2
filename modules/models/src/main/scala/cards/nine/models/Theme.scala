package cards.nine.models

import android.graphics.Color
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.types.theme._
import play.api.libs.json._

import scala.util.Random

case class NineCardsTheme(name: String, parent: ThemeType, styles: Seq[ThemeStyle], themeColors: ThemeColors) {

  val lineRatio = 0.4f

  def get(style: ThemeStyleType): Int = styles.find(_.styleType == style) map (_.color) getOrElse {
    android.util.Log.i("9Cards", s"The selected theme doesn't have the $style property")
    Color.TRANSPARENT
  }

  def getLineColor: Int = parent match {
    case ThemeLight => get(DrawerBackgroundColor).dark(lineRatio)
    case ThemeDark => get(DrawerBackgroundColor).light(lineRatio)
  }

  def getIndexColor(index: Int): Int = themeColors.colors.lift(index).getOrElse(themeColors.defaultColor)

  def getRandomIndexColor: Int = getIndexColor(Random.nextInt(themeColors.colors.size))
}

case class ThemeColors(defaultColor: Int, colors: Seq[Int])

case class ThemeStyle(styleType: ThemeStyleType, color: Int)

object NineCardsThemeImplicits {

  implicit val themeTypeReads = new Reads[ThemeType] {
    override def reads(js: JsValue) = js.as[String] match {
      case "light" => JsSuccess(ThemeLight)
      case "dark" => JsSuccess(ThemeDark)
      case _ => JsError(s"Theme type should be 'light' or 'dark'")
    }
  }

  implicit val themeTypeWrites = new Writes[ThemeType] {
    override def writes(themeType: ThemeType) = themeType match {
      case ThemeLight => JsString("light")
      case ThemeDark => JsString("dark")
    }
  }

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
        "color" -> Json.toJsFieldJsValueWrapper(themeStyle.color.colorToString)
      )
  }

  implicit val themeColorsReads = new Reads[ThemeColors] {

    def reads(js: JsValue) = {

      val defaultColor = Color.parseColor((js \ "defaultColor").as[String])

      val colors = (js \ "colors").as[Array[String]].map(Color.parseColor)

      JsSuccess(ThemeColors(defaultColor, colors))
    }

  }

  implicit val themeColorsWrites = new Writes[ThemeColors] {

    def writes(themeColors: ThemeColors) =
      Json.obj(
        "defaultColor" -> Json.toJsFieldJsValueWrapper(themeColors.defaultColor.colorToString),
        "color" -> Json.toJsFieldJsValueWrapper(themeColors.colors.map(_.colorToString).toArray)
      )

  }

  implicit val nineCardsThemeReads = Json.reads[NineCardsTheme]
}
