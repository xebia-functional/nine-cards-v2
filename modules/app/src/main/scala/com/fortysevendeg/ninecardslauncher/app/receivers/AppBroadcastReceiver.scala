package com.fortysevendeg.ninecardslauncher.app.receivers

import android.content.Intent._
import android.content._
import com.fortysevendeg.ninecardslauncher.app.commons.{ContextSupportPreferences, BroadcastDispatcher}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.AppInstalledActionFilter
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
      case (ACTION_PACKAGE_ADDED, false) => Task.fork(addApp(packageName).run).resolveAsync(
        onResult = _ => {
          // We can't use the BroadcastDispatcher trait because the Receivers aren't a ContextWrapper then
          // we have to send the intent from the context parameter
          val intent = new Intent(AppInstalledActionFilter.action)
          intent.putExtra(BroadcastDispatcher.keyType, BroadcastDispatcher.commandType)
          context.sendBroadcast(intent)
        }
      )
      case (ACTION_PACKAGE_REMOVED, false) => Task.fork(deleteApp(packageName).run).resolveAsync()
      case (ACTION_PACKAGE_CHANGED | ACTION_PACKAGE_REPLACED, _) => Task.fork(updateApp(packageName).run).resolveAsync()
      case (_, _) =>
    }
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
