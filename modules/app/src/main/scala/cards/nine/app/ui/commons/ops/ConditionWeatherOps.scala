package com.fortysevendeg.ninecardslauncher.app.ui.commons.ops

import com.fortysevendeg.ninecardslauncher.process.recognition._
import com.fortysevendeg.ninecardslauncher2.R

object ConditionWeatherOps {

  implicit class ConditionWeatherIcon(condition: ConditionWeather) {

    def getIcon: Int = condition match {
      case ClearCondition => R.drawable.icon_weather_clear
      case CloudyCondition => R.drawable.icon_weather_cloudy
      case FoggyCondition => R.drawable.icon_weather_foggy
      case HazyCondition => R.drawable.icon_weather_hazy
      case IcyCondition => R.drawable.icon_weather_icy
      case RainyCondition => R.drawable.icon_weather_rainy
      case SnowyCondition => R.drawable.icon_weather_snowy
      case StormyCondition => R.drawable.icon_weather_stormy
      case WindyCondition => R.drawable.icon_weather_windy
      case UnknownCondition => R.drawable.icon_weather_unknown
    }

  }

}
