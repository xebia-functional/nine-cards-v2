package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.calls.CallsServices
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServices, ImplicitsContactsServiceExceptions}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.ShortcutsServices
import com.fortysevendeg.ninecardslauncher.services.widgets.WidgetsServices

class DeviceProcessImpl(
  val appsServices: AppsServices,
  val apiServices: ApiServices,
  val persistenceServices: PersistenceServices,
  val shortcutsServices: ShortcutsServices,
  val contactsServices: ContactsServices,
  val imageServices: ImageServices,
  val widgetsServices: WidgetsServices,
  val callsServices: CallsServices)
  extends DeviceProcess
  with DeviceProcessDependencies
  with AppsDeviceProcessImpl
  with ContactsDeviceProcessImpl
  with DockAppsDeviceProcessImpl
  with LastCallsDeviceProcessImpl
  with ResetProcessImpl
  with ShorcutsDeviceProcessImpl
  with WidgetsDeviceProcessImpl
  with ImplicitsDeviceException
  with ImplicitsImageExceptions
  with ImplicitsPersistenceServiceExceptions
  with ImplicitsContactsServiceExceptions
  with DeviceConversions
