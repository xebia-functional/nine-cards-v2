package com.fortysevendeg.ninecardslauncher.app.receivers

import com.fortysevendeg.ninecardslauncher2.R
import android.content.Intent._
import android.content._
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

import scalaz.concurrent.Task

class AppBroadcastReceiver
  extends BroadcastReceiver
  with AppBroadcastReceiverTasks {

  override def onReceive(context: Context, intent: Intent): Unit = {

    val action = intent.getAction
    val replacing = intent.getBooleanExtra(EXTRA_REPLACING, false)
    val packageName = getPackageName(intent)

    implicit val contextSupport = new ContextSupportReceiverImpl(context)
    implicit val di = new Injector

    (action, replacing) match {
      case (ACTION_PACKAGE_ADDED, false) => Task.fork(addApp(packageName).run).resolveAsync()
      case (ACTION_PACKAGE_REMOVED, false) => Task.fork(deleteApp(packageName).run).resolveAsync()
      case (ACTION_PACKAGE_CHANGED | ACTION_PACKAGE_REPLACED, _) => Task.fork(updateApp(packageName).run).resolveAsync()
      case (_, _) =>
    }
  }

  private[this] def getPackageName(intent: Intent): String = {
    intent.getData.toString.replace("package:", "")
  }
}

class ContextSupportReceiverImpl(context: Context) extends ContextSupport {
  override def getPackageManager = context.getPackageManager
  override def getResources = context.getResources
  override def getContentResolver = context.getContentResolver
  override def getFilesDir = context.getFilesDir
  override def getAppIconsDir = context.getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE)
  override def getAssets = context.getAssets
  override def getPackageName = context.getPackageName
}
