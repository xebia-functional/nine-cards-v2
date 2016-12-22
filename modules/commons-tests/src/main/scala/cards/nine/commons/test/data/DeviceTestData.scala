package cards.nine.commons.test.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.DeviceValues._
import cards.nine.models._
import cards.nine.models.types.AudioAndVideoBluetooth

trait DeviceTestData extends NineCardsIntentConversions {

  def shortcut(num: Int = 0) =
    Shortcut(title = shortcutName + num, icon = None, intent = jsonToNineCardIntent(intent))

  val shortcut: Shortcut         = shortcut(0)
  val seqShortcut: Seq[Shortcut] = Seq(shortcut(0), shortcut(1), shortcut(2))

  def contactEmail(num: Int = 0) =
    ContactEmail(address = emailAddress + num, category = emailCategory)

  val seqContactEmail: Seq[ContactEmail] = Seq(contactEmail(0), contactEmail(1), contactEmail(2))

  def contactPhone(num: Int = 0) =
    ContactPhone(number = phoneNumber + num, category = phoneCategory)

  val seqContactPhone: Seq[ContactPhone] = Seq(contactPhone(0), contactPhone(1), contactPhone(2))

  def contactInfo(num: Int = 0) = ContactInfo(emails = seqContactEmail, phones = seqContactPhone)

  val contactInfo: ContactInfo = contactInfo(0)

  def contact(num: Int = 0) =
    Contact(
      name = contactName + num,
      lookupKey = lookupKey + num,
      photoUri = photoUri + num,
      hasPhone = hasPhone,
      favorite = favorite,
      info = Option(contactInfo))

  val contact: Contact         = contact(0)
  val seqContact: Seq[Contact] = Seq(contact(0), contact(1), contact(2))

  def call(num: Int = 0) =
    Call(
      number = phoneNumber + num,
      name = Option(contactName + num),
      numberType = phoneCategory,
      date = date + num,
      callType = callType)

  val call: Call         = call(0)
  val seqCall: Seq[Call] = Seq(call(0), call(1), call(2))

  def lastCallsContact(num: Int = 0) =
    LastCallsContact(
      hasContact = hasContact,
      number = phoneNumber + num,
      title = contactName + num,
      photoUri = Option(photoUri + num),
      lookupKey = Option(lookupKey + num),
      lastCallDate = date + num,
      calls = Seq(call(num)))

  val lastCallsContact: LastCallsContact = lastCallsContact(0)
  val seqLastCallsContact: Seq[LastCallsContact] =
    Seq(lastCallsContact(0), lastCallsContact(1), lastCallsContact(2))
  val seqLastCallsContactByDate: Seq[LastCallsContact] =
    seqLastCallsContact.sortBy(_.lastCallDate).reverse

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

  val bluetoothDevices = 0 to 10 map { c =>
    NineCardsBluetoothDevice(
      name = s"Bluetooth $c",
      address = s"Address $c",
      bluetoothType = AudioAndVideoBluetooth
    )
  }
}
