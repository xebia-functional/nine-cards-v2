package com.fortysevendeg.ninecardslauncher.process.theme.impl

import android.graphics.Color

trait ThemeProcessData {

  val nonExistingFileName = "nonExistingFile.json"
  val defaultThemeName = "light"
  val sampleColorWithAlpha = "#ff59afdd"
  val sampleColorWithoutAlpha = "#ffffff"
  val intSampleColorWithAlpha = Color.parseColor(sampleColorWithAlpha)
  val intSampleColorWithoutAlpha = Color.parseColor(sampleColorWithoutAlpha)

  val validThemeJson =
    s"""
      |{
      |  "name": "$defaultThemeName",
      |  "styles": [
      |    {
      |      "styleType": "PrimaryColor",
      |      "color": "#3F51B5"
      |    },
      |    {
      |      "styleType": "SearchBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "SearchPressedColor",
      |      "color": "#ff59afdd"
      |    },
      |    {
      |      "styleType": "SearchGoogleColor",
      |      "color": "#a3a3a3"
      |    },
      |    {
      |      "styleType": "SearchIconsColor",
      |      "color": "#646464"
      |    },
      |    {
      |      "styleType": "SearchTextColor",
      |      "color": "#646464"
      |    },
      |    {
      |      "styleType": "DrawerTabsBackgroundColor",
      |      "color": "#16000000"
      |    },
      |    {
      |      "styleType": "DrawerBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "DrawerTextColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "DockPressedColor",
      |      "color": "#ffd5f2fa"
      |    },
      |    {
      |      "styleType": "CardLayoutBackgroundColor",
      |      "color": "#eeeeee"
      |    },
      |    {
      |      "styleType": "CardTextColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CardBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CardBackgroundPressedColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabSelectedColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabDefaultColor",
      |      "color": "#80ffffff"
      |    },
      |    {
      |      "styleType": "DrawerIconColor",
      |      "color": "#000000"
      |    }
      |  ]
      |}
    """.stripMargin

  val wrongThemeStyleTypeJson =
    """
      |{
      |  "name": "light",
      |  "styles": [
      |    {
      |      "styleType": "UnknowStyleType",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "PrimaryColor",
      |      "color": "#3F51B5"
      |    },
      |    {
      |      "styleType": "SearchBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "SearchPressedColor",
      |      "color": "#ff59afdd"
      |    },
      |    {
      |      "styleType": "SearchGoogleColor",
      |      "color": "#a3a3a3"
      |    },
      |    {
      |      "styleType": "SearchIconsColor",
      |      "color": "#646464"
      |    },
      |    {
      |      "styleType": "SearchTextColor",
      |      "color": "#646464"
      |    },
      |    {
      |      "styleType": "DrawerTabsBackgroundColor",
      |      "color": "#16000000"
      |    },
      |    {
      |      "styleType": "DrawerBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "DrawerTextColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "DockPressedColor",
      |      "color": "#ffd5f2fa"
      |    },
      |    {
      |      "styleType": "CardLayoutBackgroundColor",
      |      "color": "#eeeeee"
      |    },
      |    {
      |      "styleType": "CardTextColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CardBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CardBackgroundPressedColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabSelectedColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabDefaultColor",
      |      "color": "#80ffffff"
      |    },
      |    {
      |      "styleType": "DrawerIconColor",
      |      "color": "#000000"
      |    }
      |  ]
      |}
    """.stripMargin

  val wrongThemeStyleColorJson =
    """
      |{
      |  "name": "light",
      |  "styles": [
      |    {
      |      "styleType": "PrimaryColor",
      |      "color": "#3F51B5"
      |    },
      |    {
      |      "styleType": "SearchBackgroundColor",
      |      "color": "#fff"
      |    },
      |    {
      |      "styleType": "SearchPressedColor",
      |      "color": "#ff59afdd"
      |    },
      |    {
      |      "styleType": "SearchGoogleColor",
      |      "color": "#a3a3a3"
      |    },
      |    {
      |      "styleType": "SearchIconsColor",
      |      "color": "#646464"
      |    },
      |    {
      |      "styleType": "SearchTextColor",
      |      "color": "#646464"
      |    },
      |    {
      |      "styleType": "DrawerTabsBackgroundColor",
      |      "color": "#16000000"
      |    },
      |    {
      |      "styleType": "DrawerBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "DrawerTextColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "DockPressedColor",
      |      "color": "#ffd5f2fa"
      |    },
      |    {
      |      "styleType": "CardLayoutBackgroundColor",
      |      "color": "#eeeeee"
      |    },
      |    {
      |      "styleType": "CardTextColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CardBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CardBackgroundPressedColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabSelectedColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabDefaultColor",
      |      "color": "#80ffffff"
      |    },
      |    {
      |      "styleType": "DrawerIconColor",
      |      "color": "#000000"
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
