package com.fortysevendeg.ninecardslauncher.process.recognition

import com.fortysevendeg.ninecardslauncher.services.awareness.{KindActivity, ConditionWeather}

case class ProbablyActivity(activity: KindActivity)

case class Headphones(connected: Boolean)

case class Weather(
  conditions: Seq[ConditionWeather],
  humidity: Int,
  dewPointCelsius: Float,
  dewPointFahrenheit: Float,
  temperatureCelsius: Float,
  temperatureFahrenheit: Float)

case class Location(
  latitude: Double,
  longitude: Double,
  countryCode: Option[String],
  countryName: Option[String],
  addressLines: Seq[String])
