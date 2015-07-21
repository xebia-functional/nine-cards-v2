package com.fortysevendeg.ninecardslauncher.app.modules.persistent

import macroid.ContextWrapper

@Deprecated
trait PersistentServices {
  def getSearchBackgroundColor(implicit c: ContextWrapper): Int
  def getSearchIconsColor(implicit c: ContextWrapper): Int
  def getSearchGoogleColor(implicit c: ContextWrapper): Int
  def getSearchPressedColor(implicit c: ContextWrapper): Int
  def getAppDrawerPressedColor(implicit c: ContextWrapper): Int
  def getCollectionDetailBackgroundColor(implicit c: ContextWrapper): Int
  def getCollectionDetailCardBackgroundColor(implicit c: ContextWrapper): Int
  def getCollectionDetailCardBackgroundPressedColor(implicit c: ContextWrapper): Int
  def getCollectionDetailTextCardColor(implicit c: ContextWrapper): Int
  def getCollectionDetailTextTabSelectedColor(implicit c: ContextWrapper): Int
  def getCollectionDetailTextTabDefaultColor(implicit c: ContextWrapper): Int
  def getIndexColor(index: Int): Int
}

@Deprecated
trait PersistentServicesComponent {
  val persistentServices: PersistentServices
}
