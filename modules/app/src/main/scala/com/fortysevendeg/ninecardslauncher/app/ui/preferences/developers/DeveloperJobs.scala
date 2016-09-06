package com.fortysevendeg.ninecardslauncher.app.ui.preferences.developers

import cats.implicits._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.device.GetByName
import macroid.ContextWrapper

class DeveloperJobs(ui: DeveloperUiActions)(implicit contextWrapper: ContextWrapper)
  extends Jobs {

  def initialize() =
    (ui.initialize(this) |@|
      loadAppsCategorized |@|
      loadMostProbableActivity |@|
      loadHeadphone |@|
      loadWeather).tupled

  def loadAppsCategorized = for {
    apps <- di.deviceProcess.getSavedApps(GetByName)
    _ <- ui.setAppsCategorizedSummary(apps)
  } yield ()

  def copyAndroidToken = for {
    user <- di.userProcess.getUser
    _ <- ui.copyToClipboard(user.deviceToken)
  } yield ()

  def copyDeviceCloudId = for {
    user <- di.userProcess.getUser
    _ <- ui.copyToClipboard(user.deviceCloudId)
  } yield ()

  def loadMostProbableActivity = for {
    probableActivity <- di.recognitionProcess.getMostProbableActivity
    _ <- ui.setProbablyActivitySummary(probableActivity.activity.toString)
  } yield ()

  def loadHeadphone = for {
    headphone <- di.recognitionProcess.getHeadphone
    _ <- ui.setHeadphonesSummary(headphone.connected)
  } yield ()

  def loadWeather = for {
    weather <- di.recognitionProcess.getWeather
    _ <- ui.setWeatherSummary(weather)
  } yield ()

}
