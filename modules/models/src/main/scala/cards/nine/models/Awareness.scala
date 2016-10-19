package cards.nine.models

import cards.nine.models.types.{ConditionWeather, KindActivity}

case class ProbablyActivity(activityType: KindActivity)

case class Headphones(connected: Boolean)

case class WeatherState(
  conditions: Seq[ConditionWeather],
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

case class Location(
  latitude: Double,
  longitude: Double,
  countryCode: Option[String],
  countryName: Option[String],
  addressLines: Seq[String])
