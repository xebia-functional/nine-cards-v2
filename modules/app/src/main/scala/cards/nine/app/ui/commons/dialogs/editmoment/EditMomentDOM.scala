package cards.nine.app.ui.commons.dialogs.editmoment

import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait EditMomentDOM {

  self: TypedFindView =>

  lazy val iconLinkCollection = findView(TR.edit_moment_icon_link_collection)

  lazy val nameLinkCollection = findView(TR.edit_moment_name_link_collection)

  lazy val momentCollection = findView(TR.edit_moment_collection)

  lazy val iconInfo = findView(TR.edit_moment_collection_info)

  lazy val wifiRoot = findView(TR.edit_moment_wifi_root)

  lazy val wifiContent = findView(TR.edit_moment_wifi_content)

  lazy val iconWifi = findView(TR.edit_moment_icon_wifi)

  lazy val nameWifi = findView(TR.edit_moment_name_wifi)

  lazy val addWifiAction = findView(TR.edit_moment_add_wifi)

  lazy val hourRoot = findView(TR.edit_moment_hours_root)

  lazy val hourContent = findView(TR.edit_moment_hour_content)

  lazy val addHourAction = findView(TR.edit_moment_add_hour)

  lazy val iconHour = findView(TR.edit_moment_icon_hour)

  lazy val nameHour = findView(TR.edit_moment_name_hour)

  lazy val messageRoot = findView(TR.edit_moment_message_root)

  lazy val messageIcon = findView(TR.edit_moment_icon_message)

  lazy val messageName = findView(TR.edit_moment_name_message)

  lazy val messageText = findView(TR.edit_moment_message)

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