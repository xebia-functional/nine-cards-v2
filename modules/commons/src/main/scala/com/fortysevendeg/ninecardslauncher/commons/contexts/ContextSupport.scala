package com.fortysevendeg.ninecardslauncher.commons.contexts

import java.io.File

import android.accounts.AccountManager
import android.app.Activity
import android.content.{ContentResolver, Context, SharedPreferences}
import android.content.pm.PackageManager
import android.content.res.{AssetManager, Resources}

trait ContextSupport {
  def context: Context
  def getPackageManager: PackageManager
  def getResources: Resources
  def getContentResolver: ContentResolver
  def getFilesDir: File
  def getAppIconsDir: File
  def getAssets: AssetManager
  def getPackageName: String
  def getSharedPreferences: SharedPreferences
  def getActiveUserId: Option[Int]
  def setActiveUserId(id: Int): Unit
  def getAccountManager: AccountManager
}

trait ActivityContextSupport extends ContextSupport {
  def getActivity: Option[Activity]
}