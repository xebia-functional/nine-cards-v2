package cards.nine.process.theme.impl

import android.graphics.Color
import cards.nine.process.theme.models.ThemeLight

trait ThemeProcessData {

  val nonExistingFileName = "nonExistingFile.json"
  val defaultThemeName = "theme name"
  val themeParentLight = ThemeLight
  val themeParentLightName = "light"
  val sampleColorWithAlpha = "#ff59afdd"
  val sampleColorWithoutAlpha = "#ffffff"
  val intSampleColorWithAlpha = Color.parseColor(sampleColorWithAlpha)
  val intSampleColorWithoutAlpha = Color.parseColor(sampleColorWithoutAlpha)

  val validThemeJson =
    s"""
      |{
      |  "name": "$defaultThemeName",
      |  "parent": "$themeParentLightName",
      |  "styles": [
      |    {
      |      "styleType": "PrimaryColor",
      |      "color": "$sampleColorWithAlpha"
      |    },
      |    {
      |      "styleType": "DrawerTextColor",
      |      "color": "$sampleColorWithoutAlpha"
      |    }
      |  ]
      |}
    """.stripMargin

  val wrongThemeParentJson =
    s"""
       |{
       |  "name": "$defaultThemeName",
       |  "parent": "unknownParent",
       |  "styles": [
       |    {
       |      "styleType": "PrimaryColor",
       |      "color": "#3F51B5"
       |    }
       |  ]
       |}
    """.stripMargin

  val wrongThemeStyleTypeJson =
    """
      |{
      |  "name": "light",
      |  "parent": "$themeParentLightName",
      |  "styles": [
      |    {
      |      "styleType": "UnknowStyleType",
      |      "color": "#ffffff"
      |    }
      |  ]
      |}
    """.stripMargin

  val wrongThemeStyleColorJson =
    """
      |{
      |  "name": "light",
      |  "parent": "$themeParentLightName",
      |  "styles": [
      |    {
      |      "styleType": "PrimaryColor",
      |      "color": "#ffff"
      |    }
      |  ]
      |}
    """.stripMargin

  val wrongThemeJson =
    """
      |{
      |  "name": "light"
      |}
    """.stripMargin

}
