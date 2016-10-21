package cards.nine.process.device.impl

import cards.nine.commons._
import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.ApplicationTestData
import cards.nine.models.NineCardsIntentImplicits._
import cards.nine.models._
import cards.nine.models.types._
import cards.nine.process.device.models._
import cards.nine.repository.model.{App => RepositoryApp}
import cards.nine.services.persistence.models.{IterableApps => ServicesIterableApps}
import play.api.libs.json.Json

trait DeviceProcessData
  extends ApplicationTestData 
  with NineCardsIntentConversions {

  val statusCodeOk = 200

  val size = 3

  val phoneNumber1 = "+00 111 222 333"
  val contactName1 = "Contact 1"
  val numberType1 = PhoneHome
  val date1 = 3L
  val callType1 = IncomingType
  val lookupKey1 = "lookupKey 1"
  val photoUri1 = "photoUri 1"

  val phoneNumber2 = "+00 444 555 666"
  val contactName2 = "Contact 2"
  val numberType2 = PhoneWork
  val date2 = 2L
  val callType2 = OutgoingType
  val lookupKey2 = "lookupKey 2"
  val photoUri2 = "photoUri 2"

  val phoneNumber3 = "+00 777 888 999"
  val contactName3 = "Contact 3"
  val numberType3 = PhoneOther
  val date3 = 1L
  val callType3 = MissedType
  val lookupKey3 = "lookupKey 3"
  val photoUri3 = "photoUri 3"

  val requestConfig = RequestConfig("fake-api-key", "fake-session-token", "fake-android-id", Some("fake-android-token"))

  val packageNameForCreateImage = "com.example"

  val pathForCreateImage = "/example/for/create/image"

  val urlForCreateImage = "http://www.w.com/image.jpg"

  val categorizedPackage = CategorizedPackage(
    packageName = packageNameForCreateImage,
    category = Some("SOCIAL"))

  val intentStr = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val intent = Json.parse(intentStr).as[NineCardsIntent]

  val shortcuts: Seq[Shortcut] = Seq(
    Shortcut(
      title = "Shortcut 1",
      icon = None,
      intent = intent),
    Shortcut(
      title = "Shortcut 1",
      icon = None,
      intent = intent),
    Shortcut(
      title = "Shortcut 1",
      icon = None,
      intent = intent))

  val contacts: Seq[Contact] = Seq(
   Contact(
      name = contactName1,
      lookupKey = lookupKey1,
      photoUri = photoUri1,
      hasPhone = false,
      favorite = false,
      info = None),
   Contact(
      name = contactName2,
      lookupKey = lookupKey2,
      photoUri = photoUri2,
      hasPhone = false,
      favorite = false,
      info = None),
   Contact(
      name = contactName3,
      lookupKey = lookupKey3,
      photoUri = photoUri3,
      hasPhone = false,
      favorite = false,
      info = None))

  val pathShortcut = "/example/shortcut"

  val nameShortcut = "aeiou-12345"

  val fileNameShortcut = s"$pathShortcut/$nameShortcut"

  val lookupKey = "lookupKey 1"

  val keyword = "keyword"

  val contact = Contact(
    name = "Simple Contact",
    lookupKey = lookupKey,
    photoUri = photoUri1,
    hasPhone = true,
    favorite = false,
    info = Some(
     ContactInfo(
        emails = Seq(
          ContactEmail(
            address = "sample1@47deg.com",
            category = EmailHome)),
        phones = Seq(
          ContactPhone(
            number = phoneNumber1,
            category = PhoneHome),
          ContactPhone(
            number = phoneNumber2,
            category = PhoneMobile)))))

  val call1 = Call(
      number = phoneNumber1,
      name = Some(contactName1),
      numberType = PhoneMobile,
      date = date1,
      callType = callType1)
  val call2 = Call(
      number = phoneNumber2,
      name = Some(contactName2),
      numberType = numberType2,
      date = date2,
      callType = callType2)
  val call3 =  Call(
      number = phoneNumber3,
      name = Some(contactName3),
      numberType = numberType3,
      date = date3,
      callType = callType3)

  val calls: Seq[Call] = Seq(call1, call2, call3)

  val callsContacts: Seq[Contact] = Seq(
   Contact(
      name = contactName1,
      lookupKey = lookupKey1,
      photoUri = photoUri1,
      hasPhone = false,
      favorite = false,
      info = None),
   Contact(
      name = contactName2,
      lookupKey = lookupKey2,
      photoUri = photoUri2,
      hasPhone = false,
      favorite = false,
      info = None),
   Contact(
      name = contactName3,
      lookupKey = lookupKey3,
      photoUri = photoUri3,
      hasPhone = false,
      favorite = false,
      info = None))

  val lastCallsContacts: Seq[LastCallsContact] = Seq(
    LastCallsContact(
      hasContact = true,
      number = phoneNumber1,
      title = contactName1,
      photoUri = Some(photoUri1),
      lookupKey = Some(lookupKey1),
      lastCallDate = date1,
      calls = Seq(call1)),
    LastCallsContact(
      hasContact = true,
      number = phoneNumber2,
      title = contactName2,
      photoUri = Some(photoUri2),
      lookupKey = Some(lookupKey2),
      lastCallDate = date2,
      calls = Seq(call2)),
    LastCallsContact(
      hasContact = true,
      number = phoneNumber3,
      title = contactName3,
      photoUri = Some(photoUri3),
      lookupKey = Some(lookupKey3),
      lastCallDate = date3,
      calls = Seq(call3)))

  val iterableCursorContact = new IterableCursor[Contact] {
    override def count(): Int = contacts.length

    override def moveToPosition(pos: Int): Contact = contacts(pos)

    override def close(): Unit = ()
  }

  val iterableContact = new IterableContacts(iterableCursorContact)

  val mockIterableCursor = new IterableCursor[RepositoryApp] {
    override def count(): Int = 0

    override def moveToPosition(pos: Int): RepositoryApp = javaNull

    override def close(): Unit = ()
  }

  val iterableCursorApps = new ServicesIterableApps(mockIterableCursor) {
    override def count(): Int = seqApplication.length

    override def moveToPosition(pos: Int): Application = seqApplication(pos)

    override def close(): Unit = ()
  }

  val iterableApps = new IterableApps(iterableCursorApps)

  val appsCounters = Seq(
    TermCounter("#", 4),
    TermCounter("B", 1),
    TermCounter("E", 6),
    TermCounter("F", 5),
    TermCounter("Z", 3))

  val categoryCounters = Seq(
    TermCounter("COMMUNICATION", 4),
    TermCounter("GAMES", 1),
    TermCounter("SOCIAL", 6),
    TermCounter("TOOLS", 5),
    TermCounter("WEATHER", 3))

  val contactsCounters = Seq(
    TermCounter("#", 4),
    TermCounter("B", 1),
    TermCounter("E", 6),
    TermCounter("F", 5),
    TermCounter("Z", 3))

  val installationAppsCounters = Seq(
    TermCounter("oneWeek", 4),
    TermCounter("twoWeeks", 1),
    TermCounter("oneMonth", 6),
    TermCounter("twoMonths", 5))

  val networks = 0 to 10 map (c => s"Networks $c")

}
