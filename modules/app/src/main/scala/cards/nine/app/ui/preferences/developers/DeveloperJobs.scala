/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.preferences.developers

import android.app.Activity
import android.content.Intent
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, JobException, Jobs, UiException}
import cards.nine.app.ui.launcher.LauncherActivity
import cards.nine.app.ui.preferences.commons.{
  BackendV2Url,
  IsFlowUpActive,
  IsStethoActive,
  OverrideBackendV2Url
}
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.GetByName
import cats.implicits._
import com.bumptech.glide.Glide
import macroid.ContextWrapper

class DeveloperJobs(ui: DeveloperUiActions)(implicit contextWrapper: ContextWrapper)
    extends Jobs
    with ImplicitsUiExceptions {

  def initialize() =
    (ui.initialize(this) |@|
      loadAppsCategorized |@|
      loadBackendV2Status |@|
      loadMostProbableActivity |@|
      loadHeadphone |@|
      loadLocation |@|
      loadWeather |@|
      loadStethoStatus |@|
      loadFlowUpStatus).tupled

  def loadAppsCategorized: TaskService[Unit] =
    for {
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _    <- ui.setAppsCategorizedSummary(apps)
    } yield ()

  def loadBackendV2Status: TaskService[Unit] =
    for {
      _ <- ui.enableBackendV2Url(OverrideBackendV2Url.readValue)
      _ <- ui.setBackendV2UrlSummary(BackendV2Url.readValue)
    } yield ()

  def loadStethoStatus: TaskService[Unit] =
    ui.setStethoTitle(IsStethoActive.readValue)

  def loadFlowUpStatus: TaskService[Unit] =
    ui.setFlowUpTitle(IsFlowUpActive.readValue)

  def copyAndroidToken: TaskService[Unit] =
    for {
      user <- di.userProcess.getUser
      _    <- ui.copyToClipboard(user.deviceToken)
    } yield ()

  def copyDeviceCloudId: TaskService[Unit] =
    for {
      user <- di.userProcess.getUser
      _    <- ui.copyToClipboard(user.deviceCloudId)
    } yield ()

  def clearCacheImages: TaskService[Unit] = {
    val clearCacheService = TaskService {
      CatchAll[UiException] {
        Glide.get(contextWrapper.bestAvailable).clearDiskCache()
      }
    }
    clearCacheService *> ui.cacheCleared
  }

  def restartApplication: TaskService[Unit] = TaskService {
    CatchAll[JobException] {
      val intent =
        new Intent(contextWrapper.bestAvailable, classOf[LauncherActivity])
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      contextWrapper.bestAvailable.startActivity(intent)
      contextWrapper.original.get match {
        case Some(a: Activity) => a.finish()
        case _                 =>
      }
      Runtime.getRuntime.exit(0)
    }
  }

  def loadMostProbableActivity: TaskService[Unit] =
    for {
      probableActivity <- di.recognitionProcess.getMostProbableActivity
      _                <- ui.setProbablyActivitySummary(probableActivity.activityType.toString)
    } yield ()

  def loadHeadphone: TaskService[Unit] =
    for {
      headphone <- di.recognitionProcess.getHeadphone
      _         <- ui.setHeadphonesSummary(headphone.connected)
    } yield ()

  def loadLocation: TaskService[Unit] =
    for {
      location <- di.recognitionProcess.getLocation
      _        <- ui.setLocationSummary(location)
    } yield ()

  def loadWeather: TaskService[Unit] =
    for {
      weather <- di.recognitionProcess.getWeather
      _       <- ui.setWeatherSummary(weather)
    } yield ()

}
