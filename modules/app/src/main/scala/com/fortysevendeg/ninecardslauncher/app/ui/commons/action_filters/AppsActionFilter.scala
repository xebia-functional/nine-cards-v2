package com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters

sealed trait AppsActionFilter {
  val action: String
}

case object AppInstalledActionFilter
  extends AppsActionFilter {
  override val action: String = "app-installed-action-filter"
}

case object AppUninstalledActionFilter
  extends AppsActionFilter {
  override val action: String = "app-uninstalled-action-filter"
}

case object AppUpdatedActionFilter
  extends AppsActionFilter {
  override val action: String = "app-updated-action-filter"
}

object AppsActionFilter {

  val cases = Seq(AppInstalledActionFilter, AppUninstalledActionFilter, AppUpdatedActionFilter)

  def apply(action: String): Option[AppsActionFilter] = cases find (_.action == action)

}