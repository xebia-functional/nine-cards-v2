package com.fortysevendeg.ninecardslauncher.commons.contexts

import android.content.Context

case class ContextSupport(private val context: Context) {
  def getPackageManager = context.getPackageManager
  def getResources = context.getResources
  def getContentResolver = context.getContentResolver
  def getFilesDir = context.getFilesDir
}
