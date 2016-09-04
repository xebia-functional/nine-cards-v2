package com.fortysevendeg.ninecardslauncher.app.ui.preferences.developers

import cats.implicits._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import macroid.ContextWrapper

class DeveloperJobs(ui: DeveloperUiActions)(implicit contextWrapper: ContextWrapper)
  extends Jobs {

  def initialize() = (loadMostProbableActivity |@| loadHeadphone |@| loadWeather).tupled

  def loadMostProbableActivity = for {
    probableActivity <- di.recognitionProcess.getMostProbableActivity
    _ <- ui.setProbablyActivitySummary(probableActivity.activity.toString)
  } yield ()

  def loadHeadphone = for {
    headphone <- di.recognitionProcess.getHeadphone
    _ <- ui.setHeadphonesSummary(headphone.connected.toString)
  } yield ()

  def loadWeather = for {
    weather <- di.recognitionProcess.getWeather
    _ <- ui.setWeatherSummary(weather.toString)
  } yield ()

}
