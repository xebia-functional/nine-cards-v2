package com.fortysevendeg.ninecardslauncher.modules.persistent

trait PersistentServices {
  def getSearchBackgroundColor(): Int
  def getSearchIconsColor(): Int
  def getSearchGoogleColor(): Int
  def getSearchPressedColor(): Int
  def getAppDrawerPressedColor(): Int
}

trait PersistentServicesComponent {
  val persistentServices: PersistentServices
}
