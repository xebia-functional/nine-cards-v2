package com.fortysevendeg.ninecardslauncher.app.ui.commons.models

import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.ItemHeadered
import com.fortysevendeg.ninecardslauncher.process.device.models.App

case class AppHeadered(item: Option[App] = None, header: Option[String] = None)
  extends ItemHeadered[App]