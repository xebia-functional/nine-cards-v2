package com.fortysevendeg.ninecardslauncher.services.contacts.models

import com.fortysevendeg.ninecardslauncher.services.commons.PhoneCategory

case class Contact(
  name: String,
  lookupKey: String,
  photoUri: String,
  hasPhone: Boolean = false,
  favorite: Boolean = false,
  info: Option[ContactInfo] = None)

case class ContactInfo(
  emails: Seq[ContactEmail],
  phones: Seq[ContactPhone])

case class ContactEmail(
  address: String,
  category: EmailCategory)

case class ContactPhone(
  number: String,
  category: PhoneCategory)

sealed trait EmailCategory

case object EmailHome extends EmailCategory

case object EmailWork extends EmailCategory

case object EmailOther extends EmailCategory
