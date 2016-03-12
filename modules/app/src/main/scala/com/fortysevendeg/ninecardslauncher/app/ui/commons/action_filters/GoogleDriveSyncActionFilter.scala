package com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters

sealed trait GoogleDriveSyncActionFilter {
  val action: String
}

case object GoogleDriveSyncActionFilterError
  extends GoogleDriveSyncActionFilter {
  override val action: String = "google-drive-sync-action-filter-error"
}

case object GoogleDriveSyncActionFilterSuccess
  extends GoogleDriveSyncActionFilter {
  override val action: String = "google-drive-sync-action-filter-success"
}

object GoogleDriveSyncActionFilter {

  val cases = Seq(GoogleDriveSyncActionFilterError, GoogleDriveSyncActionFilterSuccess)

  def apply(action: String): GoogleDriveSyncActionFilter = cases find (_.action == action) getOrElse
    (throw new IllegalArgumentException(s"$action not found"))

}