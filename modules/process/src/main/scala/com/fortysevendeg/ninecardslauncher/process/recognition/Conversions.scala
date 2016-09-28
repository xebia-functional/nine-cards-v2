package com.fortysevendeg.ninecardslauncher.process.recognition

import com.fortysevendeg.ninecardslauncher._

trait Conversions {

  def toProbablyActivity(typeActivity: services.awareness.TypeActivity): ProbablyActivity = 
    typeActivity.activityType match {
      case services.awareness.InVehicleActivity => ProbablyActivity(InVehicleActivity)
      case services.awareness.OnBicycleActivity => ProbablyActivity(OnBicycleActivity)
      case services.awareness.OnFootActivity => ProbablyActivity(OnFootActivity)
      case services.awareness.RunningActivity => ProbablyActivity(RunningActivity)
      case services.awareness.StillActivity => ProbablyActivity(StillActivity)
      case services.awareness.TiltingActivity => ProbablyActivity(TiltingActivity)
      case services.awareness.WalkingActivity => ProbablyActivity(WalkingActivity)
      case services.awareness.UnknownActivity => ProbablyActivity(UnknownActivity)
    }

  def toHeadphones(state: services.awareness.HeadphonesState): Headphones =
    Headphones(state.connected)

  def toWeather(weather: services.awareness.WeatherState): Weather =
    Weather(
      conditions = weather.conditions map toWeatherCondition,
      humidity = weather.humidity,
      dewPointCelsius = weather.dewPointCelsius,
      dewPointFahrenheit = weather.dewPointFahrenheit,
      temperatureCelsius = weather.temperatureCelsius,
      temperatureFahrenheit = weather.temperatureFahrenheit)
  
  def toWeatherCondition(condition: services.awareness.ConditionWeather): ConditionWeather =
    condition match {
      case services.awareness.ClearCondition => ClearCondition
      case services.awareness.CloudyCondition => CloudyCondition
      case services.awareness.FoggyCondition => FoggyCondition
      case services.awareness.HazyCondition => HazyCondition
      case services.awareness.IcyCondition => IcyCondition
      case services.awareness.RainyCondition => RainyCondition
      case services.awareness.SnowyCondition => SnowyCondition
      case services.awareness.StormyCondition => StormyCondition
      case services.awareness.WindyCondition => WindyCondition
      case services.awareness.UnknownCondition => UnknownCondition
    }

  def toLocation(awarenessLocation: services.awareness.AwarenessLocation): Location =
    Location(
      latitude = awarenessLocation.latitude,
      longitude = awarenessLocation.longitude,
      countryCode = awarenessLocation.countryCode,
      countryName = awarenessLocation.countryName,
      addressLines = awarenessLocation.addressLines)

}
