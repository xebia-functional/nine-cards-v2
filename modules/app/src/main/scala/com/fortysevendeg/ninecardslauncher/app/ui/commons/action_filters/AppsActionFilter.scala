package com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters

sealed trait AppsActionFilter {
  val action: String
}

case object AppInstalledActionFilter
  extends AppsActionFilter {
  override val action: String = "app-installed-action-filter"
}

object AppsActionFilter {

  val cases = Seq(AppInstalledActionFilter)

  def apply(action: String): AppsActionFilter = cases find (_.action == action) getOrElse
    (throw new IllegalArgumentException(s"$action not found"))

}