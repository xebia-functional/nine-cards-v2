package com.fortysevendeg.ninecardslauncher.process.device.impl

import cards.nine.services.api.ApiServices
import cards.nine.services.apps.AppsServices
import cards.nine.services.calls.CallsServices
import cards.nine.services.contacts.ContactsServices
import cards.nine.services.image.ImageServices
import cards.nine.services.persistence.PersistenceServices
import cards.nine.services.shortcuts.ShortcutsServices
import cards.nine.services.widgets.WidgetsServices

trait DeviceProcessDependencies {

  val appsServices: AppsServices
  val apiServices: ApiServices
  val persistenceServices: PersistenceServices
  val shortcutsServices: ShortcutsServices
  val contactsServices: ContactsServices
  val imageServices: ImageServices
  val widgetsServices: WidgetsServices
  val callsServices: CallsServices
}
