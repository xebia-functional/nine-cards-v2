package com.fortysevendeg.ninecardslauncher.app.ui.preferences.developers

import cats.implicits._
import com.bumptech.glide.Glide
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ImplicitsUiExceptions, Jobs, UiException}
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.device.GetByName
import macroid.ContextWrapper


class DeveloperJobs(ui: DeveloperUiActions)(implicit contextWrapper: ContextWrapper)
  extends Jobs
  with ImplicitsUiExceptions {

  def initialize() =
    (ui.initialize(this) |@|
      loadAppsCategorized |@|
      loadMostProbableActivity |@|
      loadHeadphone |@|
      loadLocation |@|
      loadWeather).tupled

  def loadAppsCategorized: TaskService[Unit] = for {
    apps <- di.deviceProcess.getSavedApps(GetByName)
    _ <- ui.setAppsCategorizedSummary(apps)
  } yield ()

  def copyAndroidToken: TaskService[Unit] = for {
    user <- di.userProcess.getUser
    _ <- ui.copyToClipboard(user.deviceToken)
  } yield ()

  def copyDeviceCloudId: TaskService[Unit] = for {
    user <- di.userProcess.getUser
    _ <- ui.copyToClipboard(user.deviceCloudId)
  } yield ()

  def clearCacheImages: TaskService[Unit] = {
    val clearCacheService = TaskService {
        CatchAll[UiException] {
          Glide.get(contextWrapper.bestAvailable).clearDiskCache()
        }
    }
    clearCacheService *> ui.cacheCleared
  }

  def loadMostProbableActivity: TaskService[Unit] = for {
    probableActivity <- di.recognitionProcess.getMostProbableActivity
    _ <- ui.setProbablyActivitySummary(probableActivity.activity.toString)
  } yield ()

  def loadHeadphone: TaskService[Unit] = for {
    headphone <- di.recognitionProcess.getHeadphone
    _ <- ui.setHeadphonesSummary(headphone.connected)
  } yield ()

  def loadLocation: TaskService[Unit] = for {
    location <- di.recognitionProcess.getLocation
    _ <- ui.setLocationSummary(location)
  } yield ()

  def loadWeather: TaskService[Unit] = for {
    weather <- di.recognitionProcess.getWeather
    _ <- ui.setWeatherSummary(weather)
  } yield ()

}
