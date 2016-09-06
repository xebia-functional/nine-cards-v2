package com.fortysevendeg.ninecardslauncher.services.awareness

case class TypeActivity(activityType: Int)

case class HeadphonesState(connected: Boolean)

case class WeatherState(
  conditions: Seq[Int],
  humidity: Int,
  dewPointCelsius: Float,
  dewPointFahrenheit: Float,
  temperatureCelsius: Float,
  temperatureFahrenheit: Float)