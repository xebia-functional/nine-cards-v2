package com.fortysevendeg.ninecardslauncher.services.widgets.utils

import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo => AndroidAppWidgetProviderInfo}
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.widgets.models.Widget

trait AppWidgetManagerCompat {

  def getAllProviders: Seq[Widget]

  def getUser(info: AndroidAppWidgetProviderInfo): Option[Int]

}
