package com.fortysevendeg.ninecardslauncher.app.receivers

import android.content.Intent._
import android.content._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportImpl, ContextSupportPreferences}
import com.fortysevendeg.ninecardslauncher.app.di.InjectorImpl
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.{AppInstalledActionFilter, AppUninstalledActionFilter, AppUpdatedActionFilter, AppsActionFilter}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport


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
        case (ACTION_PACKAGE_ADDED, false) => addApp(packageName).resolveAsync2(
          onResult = _ => sendBroadcast(AppInstalledActionFilter)
        )
        case (ACTION_PACKAGE_REMOVED, false) =>deleteApp(packageName).resolveAsync2(
          onResult = _ => sendBroadcast(AppUninstalledActionFilter)
        )
        case (ACTION_PACKAGE_CHANGED | ACTION_PACKAGE_REPLACED, _) => updateApp(packageName).resolveAsync2(
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
  extends ContextSupportImpl
  with ContextSupportPreferences {

  override def context: Context = ctx
}
