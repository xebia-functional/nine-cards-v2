package com.fortysevendeg.ninecardslauncher.app.commons

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
    override def getAppIconsDir =
      ctx.application.getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE)
    override def getAssets = ctx.application.getAssets
    override def getPackageName = ctx.application.getPackageName
    override def getSharedPreferences =
      ctx.application.getSharedPreferences(getResources.getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
    override def getActiveUserId: Option[Int] = {
      val key = getResources.getString(R.string.user_id_key)
      val pref = getSharedPreferences
      if (pref.contains(key)) {
        Some(pref.getInt(key, 0))
      } else {
        None
      }
    }
    override def setActiveUserId(id: Int): Unit = {
      val editor = getSharedPreferences.edit()
      editor.putInt(getResources.getString(R.string.user_id_key), id)
      editor.commit()
    }
  }

}
