package com.fortysevendeg.ninecardslauncher.app.ui.preferences

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.app.commons.ActivityContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import macroid.ActivityContextWrapper


import scalaz.concurrent.Task

class PreferencesJobs(ui: PreferencesUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with ImplicitsUiExceptions
  with ActivityContextSupportProvider {

  var statuses = PreferencesJobsStatuses()

  def initialize(): TaskService[Unit] = ui.initialize()

  def initializeActionBarTitle(): TaskService[Unit] = ui.setActionBarTitle()

  def preferenceChanged(preferenceName: String): TaskService[Unit] = {
    statuses = statuses.copy(changedPreferences = statuses.changedPreferences + preferenceName)
    val data = new Intent()
    data.putExtra(ResultData.preferencesResultData, statuses.changedPreferences.toArray)
    ui.setActivityResult(ResultCodes.preferencesChanged, data)
  }

  def launchSettings(): TaskService[Unit] = {

    def readPackageName: TaskService[String] =
      TaskService(Task(XorCatchAll[UiException](activityContextSupport.context.getPackageName)))

    def launchSettingsService: TaskService[Unit] =
      for {
        packageName <- readPackageName
        _ <- di.launcherExecutorProcess.launchSettings(packageName)
      } yield ()

    launchSettingsService.recoverWith {
      case _ => ui.showContactUsError()
    }
  }

}

case class PreferencesJobsStatuses(changedPreferences: Set[String] = Set.empty)
