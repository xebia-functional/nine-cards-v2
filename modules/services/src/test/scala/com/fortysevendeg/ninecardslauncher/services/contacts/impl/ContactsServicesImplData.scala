package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import com.fortysevendeg.ninecardslauncher.commons.test.repository.{MockCursor, StringDataType}
import com.fortysevendeg.ninecardslauncher.services.commons._
import com.fortysevendeg.ninecardslauncher.services.contacts.Fields
import com.fortysevendeg.ninecardslauncher.services.contacts.models._

trait ContactsServicesImplData {

  val firstLookupKey = "lookupKey 1"

  val nonExistentLookupKey = "nonExistentLookupKey"

  val emailHome = "sample_home@domain.com"
  val emailWork = "sample_work@domain.com"
  val emailOther = "sample_other@domain.com"

  val nonExistentEmail = "not_found@domain.com"

  val phoneHome = "666666666"
  val phoneWork = "777777777"
  val phoneMobile = "888888888"
  val phoneOther = "999999999"

  val nonExistentPhone = "000000000"

  val contacts = generateContacts(num = 10, withEmails = false, withPhones = false)

  val contact = generateContacts(1, withEmails = true, withPhones = true).head.copy(lookupKey = firstLookupKey)

  val contactInfo = contact.info

  val contactLookupKeyAndEmails = contactInfo.map(_.emails.map(email => (contact.lookupKey, email))).toSeq.flatten

  val contactLookupKeyAndPhones = contactInfo.map(_.phones.map(phone => (contact.lookupKey, phone))).toSeq.flatten

  val contactWithEmail = generateContacts(1, withEmails = true, withPhones = false).headOption

  val contactWithPhone = generateContacts(1, withEmails = false, withPhones = true).headOption

  def generateContacts(num: Int, withEmails: Boolean = true, withPhones: Boolean = true): Seq[Contact] =
    1 to num map { i =>
      Contact(
        s"name $i",
        s"lookupKey $i",
        s"content://photoUri/$i",
        i % 2 == 0,
        i % 5 == 0,
        generateContactInfo(withEmails, withPhones))
    }

  def generateContactInfo(withEmails: Boolean, withPhones: Boolean): Option[ContactInfo] =
    (withPhones, withEmails) match {
      case (false, false) =>
        None
      case _ =>
        Some(ContactInfo(
          if (withEmails) generateEmails else Seq.empty,
          if (withPhones) generatePhones else Seq.empty))
    }

  def generateEmails: Seq[ContactEmail] =
    Seq(
      ContactEmail(emailHome, EmailHome),
      ContactEmail(emailWork, EmailWork),
      ContactEmail(emailOther, EmailOther))

  def generatePhones: Seq[ContactPhone] =
    Seq(
      ContactPhone(phoneHome, PhoneHome),
      ContactPhone(phoneWork, PhoneWork),
      ContactPhone(phoneMobile, PhoneMobile),
      ContactPhone(phoneOther, PhoneOther))



  trait ContactsMockCursor
    extends MockCursor {

    val contactsIterator: Seq[String] = Seq("!aaa", "2bbb", "?ccc", "1ddd", "#eeee", "Abc", "Acd", "Ade", "Bcd", "Bde", "Bef", "Cde")

    val data = Seq((Fields.DISPLAY_NAME, 0, contactsIterator, StringDataType))

    prepareCursor[String](contactsIterator.size, data)
  }

  val contactCounters = Seq(
    ContactCounter("#", 5),
    ContactCounter("A", 3),
    ContactCounter("B", 3),
    ContactCounter("C", 1)
  )

}
