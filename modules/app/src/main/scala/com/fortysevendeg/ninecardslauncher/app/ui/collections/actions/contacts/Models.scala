package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import com.fortysevendeg.ninecardslauncher.process.device.models.Contact

case class ContactHeadered(contact: Option[Contact] = None, header: Option[String] = None)
