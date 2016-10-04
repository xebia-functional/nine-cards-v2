package cards.nine.process.device.models

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.javaNull
import cards.nine.models.Application
import cards.nine.models.types.Misc
import cards.nine.process.device.DeviceConversions
import cards.nine.services.contacts.models.{Contact => ServicesContact}
import cards.nine.services.persistence.models.{IterableApps => ServicesIterableApps}

class IterableApps(cursor: ServicesIterableApps)
  extends IterableCursor[Application]
  with DeviceConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Application = toApp(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}

class EmptyIterableApps()
  extends IterableApps(javaNull) {
  val emptyApp = Application("", "", "", Misc, 0, 0, "", installedFromGooglePlay = false)
  override def count(): Int = 0
  override def moveToPosition(pos: Int): Application = emptyApp
  override def close(): Unit = {}
}


class IterableContacts(cursor: IterableCursor[ServicesContact])
  extends IterableCursor[Contact]
    with DeviceConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Contact = toContact(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}
