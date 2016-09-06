package com.fortysevendeg.ninecardslauncher.process.recognition

import com.fortysevendeg.ninecardslauncher.services.awareness.{HeadphonesState, TypeActivity, WeatherState}

trait Conversions {

  def toProbablyActivity(typeActivity: TypeActivity): ProbablyActivity =
    ProbablyActivity(KindActivity(typeActivity.activityType))

  def toHeadphones(state: HeadphonesState): Headphones =
    Headphones(state.connected)

  def toWeather(weather: WeatherState) =
    Weather(
      conditions = weather.conditions map (ConditionWeather(_)),
      humidity = weather.humidity,
      dewPointCelsius = weather.dewPointCelsius,
      dewPointFahrenheit = weather.dewPointFahrenheit,
      temperatureCelsius = weather.temperatureCelsius,
      temperatureFahrenheit = weather.temperatureFahrenheit)

}
