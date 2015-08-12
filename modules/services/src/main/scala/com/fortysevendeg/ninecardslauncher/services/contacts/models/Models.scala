package com.fortysevendeg.ninecardslauncher.services.contacts.models

case class Contact(
  id: Long,
  name: String,
  hasPhone: Boolean = false,
  favorite: Boolean = false,
  info: Option[ContactInfo] = None)

case class ContactInfo(
  emails: Seq[ContactEmail],
  phones: Seq[ContactPhone])

case class ContactEmail(
  address: String,
  category: String)

case class ContactPhone(
  number: String,
  category: String)