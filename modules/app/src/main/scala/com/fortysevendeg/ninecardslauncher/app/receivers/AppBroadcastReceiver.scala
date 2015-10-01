package com.fortysevendeg.ninecardslauncher.app.receivers

import android.content._
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

import scalaz.concurrent.Task

class AppBroadcastReceiver
  extends BroadcastReceiver
  with AppBroadcastReceiverTasks {

  override def onReceive(context: Context, intent: Intent): Unit = {

    val action: String = intent.getAction
    val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

    val packageName: String = intent.getData.toString.replace("package:", "")

    implicit val contextSupport = new ContextSupportReceiverImpl(context)
    implicit val di = new Injector

    (action, replacing) match {
      case (Intent.ACTION_PACKAGE_ADDED, false) => Task.fork(addApp(packageName).run).resolveAsync()
      case (Intent.ACTION_PACKAGE_REMOVED, false) => Task.fork(deleteApp(packageName).run).resolveAsync()
      case (Intent.ACTION_PACKAGE_CHANGED, false) =>
      case (Intent.ACTION_PACKAGE_REPLACED, false) =>
      case (_, true) =>
      case (_, _) =>
    }
  }
}

class ContextSupportReceiverImpl(context: Context) extends ContextSupport {
  override def getPackageManager = context.getPackageManager
  override def getResources = context.getResources
  override def getContentResolver = context.getContentResolver
  override def getFilesDir = context.getFilesDir
  override def getAppIconsDir = context.getFilesDir //TODO find getAppIconsDir
  override def getAssets = context.getAssets
  override def getPackageName: String = context.getPackageName
}
