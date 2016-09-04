package com.fortysevendeg.ninecardslauncher.app.ui.preferences.developers

import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import macroid.Ui

class DeveloperUiActions(dom: DeveloperDOM) {

  def setProbablyActivitySummary(summary: String): TaskService[Unit] = Ui {
    dom.probablyActivityPreference.setSummary(summary)
  }.toService

  def setHeadphonesSummary(summary: String): TaskService[Unit] = Ui {
    dom.headphonesPreference.setSummary(summary)
  }.toService

  def setWeatherSummary(summary: String): TaskService[Unit] = Ui {
    dom.weatherPreference.setSummary(summary)
  }.toService

}
