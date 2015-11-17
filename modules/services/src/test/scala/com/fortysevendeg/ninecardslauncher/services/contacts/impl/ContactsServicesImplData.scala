package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import com.fortysevendeg.ninecardslauncher.services.commons._
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

  val contact = generateContacts(1, withEmails = true, withPhones = true).head

  val contactEmails = contact.info map (_.emails) getOrElse Seq.empty

  val contactPhones = contact.info map (_.phones) getOrElse Seq.empty

  val contactWithEmail = generateContacts(1, withEmails = true, withPhones = false).head

  val contactWithPhone = generateContacts(1, withEmails = false, withPhones = true).head

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

}
