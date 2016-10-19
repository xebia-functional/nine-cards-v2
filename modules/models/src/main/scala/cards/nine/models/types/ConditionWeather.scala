package cards.nine.models.types

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