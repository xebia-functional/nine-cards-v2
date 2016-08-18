package com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters

sealed trait MomentsActionFilter {
  val action: String
}

case object MomentsReloadedActionFilter
  extends MomentsActionFilter {
  override val action: String = "moments-reloaded-action-filter"
}

object MomentsActionFilter {

  val cases = Seq(MomentsReloadedActionFilter)

  def apply(action: String): MomentsActionFilter = cases find (_.action == action) getOrElse
    (throw new IllegalArgumentException(s"$action not found"))

}