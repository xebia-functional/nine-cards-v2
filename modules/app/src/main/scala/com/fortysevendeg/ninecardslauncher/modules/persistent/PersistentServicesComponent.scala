package com.fortysevendeg.ninecardslauncher.modules.persistent

trait PersistentServices {
  def getSearchBackgroundColor: Int
  def getSearchIconsColor: Int
  def getSearchGoogleColor: Int
  def getSearchPressedColor: Int
  def getAppDrawerPressedColor: Int
  def getCollectionDetailBackgroundColor: Int
  def getCollectionDetailCardBackgroundColor: Int
  def getCollectionDetailCardBackgroundPressedColor: Int
  def getCollectionDetailTextCardColor: Int
  def getCollectionDetailTextTabSelectedColor: Int
  def getCollectionDetailTextTabDefaultColor: Int
  def getIndexColor(index: Int): Int
}

trait PersistentServicesComponent {
  val persistentServices: PersistentServices
}
