package cards.nine.process.recognition

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