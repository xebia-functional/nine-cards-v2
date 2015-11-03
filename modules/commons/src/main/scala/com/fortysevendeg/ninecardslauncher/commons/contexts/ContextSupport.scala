package com.fortysevendeg.ninecardslauncher.commons.contexts

import java.io.File

import android.app.ActivityManager
import android.appwidget.AppWidgetManager
import android.content.{Context, ContentResolver}
import android.content.pm.PackageManager
import android.content.res.{AssetManager, Resources}
import android.os.UserManager

trait ContextSupport {
  def context: Context
  def getPackageManager: PackageManager
  def getResources: Resources
  def getContentResolver: ContentResolver
  def getFilesDir: File
  def getAppIconsDir: File
  def getAssets: AssetManager
  def getPackageName: String
}
