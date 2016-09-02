package com.fortysevendeg.ninecardslauncher.process.recognition

import com.google.android.gms.awareness.state.Weather._
import com.google.android.gms.location.DetectedActivity._

case class ProbablyActivity(activity: KindActivity)

case class Headphones(connected: Boolean)

case class Weather(
  conditions: Seq[ConditionWeather],
  humidity: Int,
  dewPointCelsius: Float,
  dewPointFahrenheit: Float,
  temperatureCelsius: Float,
  temperatureFahrenheit: Float)

sealed trait KindActivity

case object InVehicleActivity extends KindActivity

case object OnBicycleActivity extends KindActivity

case object OnFootActivity extends KindActivity

case object RunningActivity extends KindActivity

case object StillActivity extends KindActivity

case object TiltingActivity extends KindActivity

case object WalkingActivity extends KindActivity

case object UnknownActivity extends KindActivity

object KindActivity {

  def apply(t: Int): KindActivity = t match {
    case IN_VEHICLE => InVehicleActivity
    case ON_BICYCLE => OnBicycleActivity
    case ON_FOOT => OnFootActivity
    case RUNNING => RunningActivity
    case STILL => StillActivity
    case TILTING => TiltingActivity
    case WALKING => WalkingActivity
    case _ => UnknownActivity
  }

}

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

object ConditionWeather {

  def apply(t: Int): ConditionWeather = t match {
    case CONDITION_CLEAR => ClearCondition
    case CONDITION_CLOUDY => CloudyCondition
    case CONDITION_FOGGY => FoggyCondition
    case CONDITION_HAZY => HazyCondition
    case CONDITION_ICY => IcyCondition
    case CONDITION_RAINY => RainyCondition
    case CONDITION_SNOWY => SnowyCondition
    case CONDITION_STORMY => StormyCondition
    case CONDITION_WINDY => WindyCondition
    case _ => UnknownCondition
  }

}