package com.fortysevendeg.ninecardslauncher.services.contacts.models

import com.fortysevendeg.ninecardslauncher.services.commons.{EmailCategory, PhoneCategory}

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

case class ContactCounter(term: String, count: Int)