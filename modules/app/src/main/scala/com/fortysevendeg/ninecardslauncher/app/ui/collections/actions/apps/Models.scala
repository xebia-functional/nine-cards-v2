package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized

case class AppHeadered(app: Option[AppCategorized] = None, header: Option[String] = None)
