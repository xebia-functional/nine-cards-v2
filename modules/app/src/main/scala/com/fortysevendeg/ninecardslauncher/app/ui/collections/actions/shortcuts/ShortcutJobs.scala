package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import macroid.ActivityContextWrapper

class ShortcutJobs(actions: ShortcutUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(): TaskService[Unit] = for {
    _ <- actions.initialize()
    _ <- loadShortcuts()
  } yield ()

  def loadShortcuts(): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      shortcuts <- di.deviceProcess.getAvailableShortcuts
      _ <- actions.loadShortcuts(shortcuts)
    } yield ()

  def configureShortcut(shortcut: Shortcut): TaskService[Unit] =
    for {
      _ <- actions.close()
      _ <- TaskService.right {
        activityContextWrapper.getOriginal.startActivityForResult(shortcut.intent, shortcutAdded)
      }
    } yield ()

  def showErrorLoadingShortcuts(): TaskService[Unit] = actions.showErrorLoadingShortcutsInScreen()

}

