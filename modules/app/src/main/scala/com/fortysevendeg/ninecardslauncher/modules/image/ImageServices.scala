package com.fortysevendeg.ninecardslauncher.modules.image

import android.content.pm.ResolveInfo

import scala.concurrent.{Future, ExecutionContext}

trait ImageServices {

  def getPath(filename: String): String

  def getImagePath(packageName: String, className: String): String

  def createAppBitmap(term: String, info: ResolveInfo)(implicit executionContext: ExecutionContext): Future[String]

  def storeImageApp(packageName: String, url: String)(implicit executionContext: ExecutionContext): Future[String]
}

