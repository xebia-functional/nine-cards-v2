package com.fortysevendeg.ninecardslauncher.app.commons

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import macroid.{ActivityContextWrapper, ContextWrapper}

trait ContextSupportImpl extends ContextSupport {

  override def getContentResolver = context.getContentResolver

  override def getPackageManager = context.getPackageManager

  override def getResources = context.getResources

  override def getFilesDir = context.getFilesDir

  override def getAssets = context.getAssets

  override def getPackageName = context.getPackageName

  override def getAccountManager: AccountManager = AccountManager.get(context)
}

trait ContextSupportProvider {

  implicit def contextSupport(implicit ctx: ContextWrapper): ContextSupport =
    new ContextSupportImpl with ContextSupportPreferences {

      override def context: Context = ctx.bestAvailable
    }

  implicit def activityContextSupport(implicit ctx: ActivityContextWrapper): ActivityContextSupport =
    new ContextSupportImpl with ActivityContextSupport with ContextSupportPreferences {

      override def context: Context = ctx.bestAvailable

      override def getActivity: Option[Activity] = ctx.original.get
    }

}
