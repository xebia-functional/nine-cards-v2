package com.fortysevendeg.ninecardslauncher.services.awareness

case class TypeActivity(activityType: KindActivity)

case class HeadphonesState(connected: Boolean)

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

case class AwarenessLocation(
  latitude: Double,
  longitude: Double,
  countryCode: Option[String],
  countryName: Option[String],
  addressLines: Seq[String])

sealed trait KindActivity

case object InVehicleActivity extends KindActivity

case object OnBicycleActivity extends KindActivity

case object OnFootActivity extends KindActivity

case object RunningActivity extends KindActivity

case object StillActivity extends KindActivity

case object TiltingActivity extends KindActivity

case object WalkingActivity extends KindActivity

case object UnknownActivity extends KindActivity

sealed trait ConditionWeather

case object ClearCondition extends ConditionWeather

case object CloudyCondition extends ConditionWeather

case object FoggyCondition extends ConditionWeather

case object HazyCondition extends ConditionWeather

case object IcyCondition extends ConditionWeather

case object RainyCondition extends ConditionWeather

case object SnowyCondition extends ConditionWeather

case object StormyCondition extends ConditionWeather

case object WindyCondition extends ConditionWeather

case object UnknownCondition extends ConditionWeather
