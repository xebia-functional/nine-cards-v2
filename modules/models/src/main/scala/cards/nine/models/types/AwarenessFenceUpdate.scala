package cards.nine.models.types

sealed trait AwarenessFenceUpdate {
  val key: String
}

case object HeadphonesFence extends AwarenessFenceUpdate {

  override val key: String = "HEADPHONE_PLUGGED_FENCE"

  val keyIn: String = s"${key}_IN"

  val keyOut: String = s"${key}_OUT"
}

case object InVehicleFence extends AwarenessFenceUpdate {
  override val key: String = "IN_VEHICLE_FENCE"
}

case object OnBicycleFence extends AwarenessFenceUpdate {
  override val key: String = "ON_BICYCLE_FENCE"
}

case object RunningFence extends AwarenessFenceUpdate {
  override val key: String = "RUNNING_FENCE"
}

object AwarenessFenceUpdate {

  val fences = Seq(HeadphonesFence, InVehicleFence, OnBicycleFence, RunningFence)

}