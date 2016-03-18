package com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters

sealed trait SyncActionFilter {
  val action: String
}

case object SyncStateActionFilter
  extends SyncActionFilter {
  override val action: String = "sync-state-action-filter"
}

case object SyncAskActionFilter
  extends SyncActionFilter {
  override val action: String = "sync-ask-action-filter"
}

case object SyncAnswerActionFilter
  extends SyncActionFilter {
  override val action: String = "sync-answer-action-filter"
}

object SyncActionFilter {

  val cases = Seq(SyncStateActionFilter, SyncAskActionFilter, SyncAnswerActionFilter)

  def apply(action: String): SyncActionFilter = cases find (_.action == action) getOrElse
    (throw new IllegalArgumentException(s"$action not found"))

}