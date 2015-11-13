package com.fortysevendeg.ninecardslauncher.app.commons

import android.content.Context
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher2.R

trait ContextSupportPreferences {

  self: ContextSupport =>

  override def getAppIconsDir =
    context.getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE)

  override def getSharedPreferences =
    context.getSharedPreferences(getResources.getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)

  override def getActiveUserId: Option[Int] = {
    val key = getResources.getString(R.string.user_id_key)
    val pref = getSharedPreferences
    if (pref.contains(key)) {
      Some(getSharedPreferences.getInt(key, 0))
    } else {
      None
    }
  }
  override def setActiveUserId(id: Int): Unit = {
    val editor = getSharedPreferences.edit()
    editor.putInt(getResources.getString(R.string.user_id_key), id)
    editor.apply()
  }


}
