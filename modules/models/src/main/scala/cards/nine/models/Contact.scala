package cards.nine.models

import cards.nine.models.types._

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