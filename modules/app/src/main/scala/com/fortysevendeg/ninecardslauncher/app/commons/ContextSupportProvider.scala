package com.fortysevendeg.ninecardslauncher.app.commons

import android.content.Context
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import macroid.ContextWrapper

trait ContextSupportProvider {

  implicit def contextSupport(implicit ctx: ContextWrapper): ContextSupport =
    new ContextSupport with ContextSupportPreferences {
      override def getContentResolver = context.getContentResolver

      override def getPackageManager = context.getPackageManager

      override def getResources = context.getResources

      override def getFilesDir = context.getFilesDir

      override def getAssets = context.getAssets

      override def getPackageName = context.getPackageName

      override def context: Context = ctx.application
    }

}
