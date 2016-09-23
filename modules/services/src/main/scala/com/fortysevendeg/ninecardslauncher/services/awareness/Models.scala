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

case class LocationState(
  accuracy: Float,
  altitude: Double,
  bearing: Float,
  latitude: Double,
  longitude: Double,
  speed: Float,
  elapsedTime: Long,
  time: Long)

case class AwarenessLocation(
  latitude: Double,
  longitude: Double,
  countryCode: Option[String],
  countryName: Option[String])