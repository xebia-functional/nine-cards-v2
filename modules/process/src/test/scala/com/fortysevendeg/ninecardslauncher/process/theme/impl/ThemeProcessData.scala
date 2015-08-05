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
      |      "styleType": "SearchBackgroundColor",
      |      "color": "$sampleColorWithoutAlpha"
      |    },
      |    {
      |      "styleType": "SearchPressedColor",
      |      "color": "$sampleColorWithAlpha"
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
      |      "styleType": "AppDrawerPressedColor",
      |      "color": "#ffd5f2fa"
      |    },
      |    {
      |      "styleType": "CollectionDetailBackgroundColor",
      |      "color": "#eeeeee"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextCardColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailCardBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailCardBackgroundPressedColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabSelectedColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabDefaultColor",
      |      "color": "#80ffffff"
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
      |      "styleType": "AppDrawerPressedColor",
      |      "color": "#ffd5f2fa"
      |    },
      |    {
      |      "styleType": "CollectionDetailBackgroundColor",
      |      "color": "#eeeeee"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextCardColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailCardBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailCardBackgroundPressedColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabSelectedColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabDefaultColor",
      |      "color": "#80ffffff"
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
      |      "styleType": "AppDrawerPressedColor",
      |      "color": "#ffd5f2fa"
      |    },
      |    {
      |      "styleType": "CollectionDetailBackgroundColor",
      |      "color": "#eeeeee"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextCardColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailCardBackgroundColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailCardBackgroundPressedColor",
      |      "color": "#000000"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabSelectedColor",
      |      "color": "#ffffff"
      |    },
      |    {
      |      "styleType": "CollectionDetailTextTabDefaultColor",
      |      "color": "#80ffffff"
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
