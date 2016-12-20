package cards.nine.models.types

sealed trait KindActivity

case object InVehicleActivity extends KindActivity

case object OnBicycleActivity extends KindActivity

case object OnFootActivity extends KindActivity

case object RunningActivity extends KindActivity

case object StillActivity extends KindActivity

case object TiltingActivity extends KindActivity

case object WalkingActivity extends KindActivity

case object UnknownActivity extends KindActivity
