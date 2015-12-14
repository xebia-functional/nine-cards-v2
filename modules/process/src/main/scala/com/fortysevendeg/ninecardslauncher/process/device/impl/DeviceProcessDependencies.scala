package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.calls.CallsServices
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServices
import com.fortysevendeg.ninecardslauncher.services.image.ImageServices
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import com.fortysevendeg.ninecardslauncher.services.shortcuts.ShortcutsServices
import com.fortysevendeg.ninecardslauncher.services.widgets.WidgetsServices

trait DeviceProcessDependencies {

  val appsService: AppsServices
  val apiServices: ApiServices
  val persistenceServices: PersistenceServices
  val shortcutsServices: ShortcutsServices
  val contactsServices: ContactsServices
  val imageServices: ImageServices
  val widgetsServices: WidgetsServices
  val callsServices: CallsServices
}
