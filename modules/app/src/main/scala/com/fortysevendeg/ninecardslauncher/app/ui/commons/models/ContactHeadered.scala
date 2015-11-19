package com.fortysevendeg.ninecardslauncher.app.ui.commons.models

import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.ItemHeadered
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact

case class ContactHeadered(item: Option[Contact] = None, header: Option[String] = None)
  extends ItemHeadered[Contact]