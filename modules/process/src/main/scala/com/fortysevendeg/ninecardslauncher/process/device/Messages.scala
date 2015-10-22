package com.fortysevendeg.ninecardslauncher.process.device

sealed trait ContactsFilter

case object AllContacts extends ContactsFilter

case object FavoriteContacts extends ContactsFilter

case object ContactsWithPhoneNumber extends ContactsFilter


sealed trait GetAppOrder {
  val ascending: Boolean
}

case class GetByName(ascending: Boolean) extends GetAppOrder

case object GetByName extends GetByName(true)

case class GetByUpdate(ascending: Boolean) extends GetAppOrder

case object GetByUpdate extends GetByUpdate(false)

case class GetByCategory(ascending: Boolean) extends GetAppOrder

case object GetByCategory extends GetByCategory(true)