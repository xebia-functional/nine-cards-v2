package com.fortysevendeg.ninecardslauncher.app.ui.profile

sealed trait ProfileTab

case object PublicationsTab extends ProfileTab

case object SubscriptionsTab extends ProfileTab

case object AccountsTab extends ProfileTab

trait ProfileTabListener {

  def onProfileTabSelected(profileTab: ProfileTab): Unit

}
