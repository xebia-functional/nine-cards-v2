package com.fortysevendeg.ninecardslauncher.app.receivers

import android.content.Intent._
import android.content._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportImpl, ContextSupportPreferences}
import com.fortysevendeg.ninecardslauncher.app.di.InjectorImpl
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.AppInstalledActionFilter

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
          onResult = _ => {
            // We can't use the BroadcastDispatcher trait because the Receivers aren't a ContextWrapper then
            // we have to send the intent from the context parameter
            val intent = new Intent(AppInstalledActionFilter.action)
            intent.putExtra(BroadcastDispatcher.keyType, BroadcastDispatcher.commandType)
            context.sendBroadcast(intent)
          }
        )
        case (ACTION_PACKAGE_REMOVED, false) => Task.fork(deleteApp(packageName).value).resolveAsync()
        case (ACTION_PACKAGE_CHANGED | ACTION_PACKAGE_REPLACED, _) => Task.fork(updateApp(packageName).value).resolveAsync()
        case (_, _) =>
      }
    }
  }

  private[this] def getPackageName(intent: Intent): String = {
    intent.getData.toString.replace("package:", "")
  }
}

class ContextSupportReceiverImpl(ctx: Context)
  extends ContextSupportImpl
  with ContextSupportPreferences {

  override def context: Context = ctx
}
