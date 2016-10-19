package cards.nine.services.awareness.impl

import cards.nine.models.types._
import com.google.android.gms.awareness.state.Weather._
import com.google.android.gms.location.DetectedActivity._

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