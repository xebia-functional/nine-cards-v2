package cards.nine.app.ui.commons.action_filters

sealed trait MomentsActionFilter {
  val action: String
}

case object MomentReloadedActionFilter
  extends MomentsActionFilter {
  override val action: String = "moments-reloaded-action-filter"
}

case object MomentConstrainsChangedActionFilter
  extends MomentsActionFilter {
  override val action: String = "moments-constrains-changed-action-filter"
}

case object MomentBestAvailableActionFilter
  extends MomentsActionFilter {
  override val action: String = "moments-best-available-action-filter"
}

case object MomentForceBestAvailableActionFilter
  extends MomentsActionFilter {
  override val action: String = "moments-force-best-available-action-filter"
}

object MomentsActionFilter {

  val cases = Seq(MomentReloadedActionFilter, MomentConstrainsChangedActionFilter, MomentForceBestAvailableActionFilter)

  def apply(action: String): Option[MomentsActionFilter] = cases find (_.action == action)

}