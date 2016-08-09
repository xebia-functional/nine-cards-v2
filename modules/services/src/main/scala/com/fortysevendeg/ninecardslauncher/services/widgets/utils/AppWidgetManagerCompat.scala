package com.fortysevendeg.ninecardslauncher.services.widgets.utils

import com.fortysevendeg.ninecardslauncher.services.widgets.models.Widget

trait AppWidgetManagerCompat {

  def getAllProviders: Seq[Widget]

}
