package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.commons._

trait WizardPersistence {

  self: AppCompatActivity =>

  private[this] val googleKeyPreferences = "__google_auth__"
  private[this] val googleKeyToken = "__google_token__"

  lazy val preferences = getSharedPreferences(googleKeyPreferences, Context.MODE_PRIVATE)

  def getToken: Option[String] = Option(preferences.getString(googleKeyToken, javaNull))

  def setToken(token: String) = preferences.edit.putString(googleKeyToken, token).apply()

}
