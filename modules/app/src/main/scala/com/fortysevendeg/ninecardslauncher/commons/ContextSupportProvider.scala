package com.fortysevendeg.ninecardslauncher.commons

import android.content.Context
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import macroid.ContextWrapper

trait ContextSupportProvider {

  private val iconAppsFolder = "icons_apps"

  implicit def contextSupport(implicit ctx : ContextWrapper): ContextSupport = {
    new ContextSupport {
      override def getContentResolver = ctx.application.getContentResolver
      override def getPackageManager = ctx.application.getPackageManager
      override def getResources = ctx.application.getResources
      override def getFilesDir = ctx.application.getFilesDir
      override def getAppIconsDir = ctx.application.getDir(iconAppsFolder, Context.MODE_PRIVATE)
    }
  }

}
