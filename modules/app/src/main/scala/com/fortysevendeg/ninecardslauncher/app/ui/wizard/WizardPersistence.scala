package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.commons._

trait WizardPersistence {

  self: AppCompatActivity =>

  val googleKeyPreferences = "__google_auth__"
  val googleKeyToken = "__google_token__"
  val accountType = "com.google"
  val androidId = "android_id"
  val contentGServices = "content://com.google.android.gsf.gservices"

  lazy val preferences = getSharedPreferences(googleKeyPreferences, Context.MODE_PRIVATE)

  def getToken: Option[String] = Option(preferences.getString(googleKeyToken, javaNull))

  def setToken(token: String) = preferences.edit.putString(googleKeyToken, token).apply()

  def getAndroidId: Option[String] = {
    val cursor = Option(getContentResolver.query(Uri.parse(contentGServices), javaNull, javaNull, Array(androidId), javaNull))
    val id = cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
    cursor foreach (_.close())
    id
  }

}
