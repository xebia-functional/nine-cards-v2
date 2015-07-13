package com.fortysevendeg.ninecardslauncher.commons

import android.content.Context
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

trait ContextSupportProvider {

  implicit def contextSupport(implicit ctx : ContextWrapper): ContextSupport = new ContextSupport {
    override def getContentResolver = ctx.application.getContentResolver
    override def getPackageManager = ctx.application.getPackageManager
    override def getResources = ctx.application.getResources
    override def getFilesDir = ctx.application.getFilesDir
    override def getAppIconsDir = ctx.application.getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE)
  }

}
