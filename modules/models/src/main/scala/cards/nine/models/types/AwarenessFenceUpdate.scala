package cards.nine.models.types

sealed trait AwarenessFenceUpdate {
  def key: String
}

case object HeadphonesFence extends AwarenessFenceUpdate {
  override def key: String = "HEADPHONE_PLUGGED_FENCE"
}

case object InVehicleFence extends AwarenessFenceUpdate {
  override def key: String = "IN_VEHICLE_FENCE"
}

case object OnBicycleFence extends AwarenessFenceUpdate {
  override def key: String = "ON_BICYCLE_FENCE"
}

case object RunningFence extends AwarenessFenceUpdate {
  override def key: String = "RUNNING_FENCE"
}

object AwarenessFenceUpdate {

  val fences = Seq(HeadphonesFence, InVehicleFence, OnBicycleFence, RunningFence)

}