package com.fortysevendeg.ninecardslauncher.app.ui.drawer

trait DrawerListeners {
  def loadApps(appsMenuOption: AppsMenuOption): Unit
  def loadContacts(contactsMenuOption: ContactsMenuOption): Unit
}
