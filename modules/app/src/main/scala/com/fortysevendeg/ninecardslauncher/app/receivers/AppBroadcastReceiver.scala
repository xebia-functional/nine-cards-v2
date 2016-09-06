package com.fortysevendeg.ninecardslauncher.app.receivers

import android.content.Intent._
import android.content._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportPreferences}
import com.fortysevendeg.ninecardslauncher.app.di.InjectorImpl
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.{AppInstalledActionFilter, AppUninstalledActionFilter, AppUpdatedActionFilter, AppsActionFilter}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

import scalaz.concurrent.Task

class AppBroadcastReceiver
  extends BroadcastReceiver
  with AppBroadcastReceiverTasks {

  override def onReceive(context: Context, intent: Intent): Unit = {
    val packageName = getPackageName(intent)
    // We don't want to change 9Cards App
    if (!context.getPackageName.equals(packageName)) {
      val action = intent.getAction
      val replacing = intent.getBooleanExtra(EXTRA_REPLACING, false)

      implicit val contextSupport = new ContextSupportReceiverImpl(context)
      implicit val di = new InjectorImpl

      (action, replacing) match {
        case (ACTION_PACKAGE_ADDED, false) => Task.fork(addApp(packageName).value).resolveAsync(
          onResult = _ => sendBroadcast(AppInstalledActionFilter)
        )
        case (ACTION_PACKAGE_REMOVED, false) => Task.fork(deleteApp(packageName).value).resolveAsync(
          onResult = _ => sendBroadcast(AppUninstalledActionFilter)
        )
        case (ACTION_PACKAGE_CHANGED | ACTION_PACKAGE_REPLACED, _) => Task.fork(updateApp(packageName).value).resolveAsync(
          onResult = _ => sendBroadcast(AppUpdatedActionFilter)
        )
        case (_, _) =>
      }
    }
  }

  private[this] def sendBroadcast(filter: AppsActionFilter)(implicit contextSupport: ContextSupport) = {
    val intent = new Intent(filter.action)
    intent.putExtra(BroadcastDispatcher.keyType, BroadcastDispatcher.commandType)
    contextSupport.context.sendBroadcast(intent)
  }

  private[this] def getPackageName(intent: Intent): String = {
    intent.getData.toString.replace("package:", "")
  }
}

class ContextSupportReceiverImpl(ctx: Context)
  extends ContextSupport
  with ContextSupportPreferences {
  override def getPackageManager = context.getPackageManager

  override def getResources = context.getResources

  override def getContentResolver = context.getContentResolver

  override def getFilesDir = context.getFilesDir

  override def getAssets = context.getAssets

  override def getPackageName = context.getPackageName

  override def context: Context = ctx
}
