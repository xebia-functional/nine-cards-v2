package com.fortysevendeg.ninecardslauncher.process.recognition

import com.fortysevendeg.ninecardslauncher.services.awareness.{AwarenessLocation, HeadphonesState, TypeActivity, WeatherState}

trait Conversions {

  def toProbablyActivity(typeActivity: TypeActivity): ProbablyActivity =
    ProbablyActivity(typeActivity.activityType)

  def toHeadphones(state: HeadphonesState): Headphones =
    Headphones(state.connected)

  def toWeather(weather: WeatherState) =
    Weather(
      conditions = weather.conditions,
      humidity = weather.humidity,
      dewPointCelsius = weather.dewPointCelsius,
      dewPointFahrenheit = weather.dewPointFahrenheit,
      temperatureCelsius = weather.temperatureCelsius,
      temperatureFahrenheit = weather.temperatureFahrenheit)

  def toLocation(awarenessLocation: AwarenessLocation) =
    Location(
      latitude = awarenessLocation.latitude,
      longitude = awarenessLocation.longitude,
      countryCode = awarenessLocation.countryCode,
      countryName = awarenessLocation.countryName,
      addressLines = awarenessLocation.addressLines)

}
