package cards.nine.app.ui.launcher.actions.editmoment

import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait EditMomentDOM {

  self: TypedFindView =>

  lazy val momentCollection = findView(TR.edit_moment_collection)

  lazy val hourContent = findView(TR.edit_moment_hour_content)

  lazy val addHourAction = findView(TR.edit_moment_add_hour)

  lazy val wifiContent = findView(TR.edit_moment_wifi_content)

  lazy val iconLinkCollection = findView(TR.edit_moment_icon_link_collection)

  lazy val iconInfo = findView(TR.edit_moment_collection_info)

  lazy val iconHour = findView(TR.edit_moment_icon_hour)

  lazy val iconWifi = findView(TR.edit_moment_icon_wifi)

  lazy val addWifiAction = findView(TR.edit_moment_add_wifi)

  lazy val nameWifi = findView(TR.edit_moment_name_wifi)

  lazy val nameHour = findView(TR.edit_moment_name_hour)

  lazy val nameLinkCollection = findView(TR.edit_moment_name_link_collection)

}

trait EditMomentListener {

  def addWifi(): Unit

  def addWifi(wifi: String): Unit

  def addHour(): Unit

  def saveMoment(): Unit

  def setCollectionId(collectionId: Option[Int]): Unit

  def removeHour(position: Int): Unit

  def removeWifi(position: Int): Unit

  def changeFromHour(position: Int, hour: String): Unit

  def changeToHour(position: Int, hour: String): Unit

  def swapDay(position: Int, index: Int): Unit

}