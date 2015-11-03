package com.fortysevendeg.ninecardslauncher.app.commons

import android.content.Context
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

trait ContextSupportProvider {

  implicit def contextSupport(implicit ctx : ContextWrapper): ContextSupport = new ContextSupport {
    override def context = ctx.application
    override def getContentResolver = context.getContentResolver
    override def getPackageManager = context.getPackageManager
    override def getResources = context.getResources
    override def getFilesDir = context.getFilesDir
    override def getAppIconsDir = context.getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE)
    override def getAssets = context.getAssets
    override def getPackageName = context.getPackageName
  }

}
