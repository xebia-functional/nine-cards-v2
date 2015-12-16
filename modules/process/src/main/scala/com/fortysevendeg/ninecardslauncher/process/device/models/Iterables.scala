package com.fortysevendeg.ninecardslauncher.process.device.models

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor
import com.fortysevendeg.ninecardslauncher.process.device.DeviceConversions
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ServiceContact}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{IterableApps => ServiceIterableApps}

class IterableApps(cursor: ServiceIterableApps)
  extends IterableCursor[App]
  with DeviceConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): App = toApp(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}

class IterableContacts(cursor: IterableCursor[ServiceContact])
  extends IterableCursor[Contact]
    with DeviceConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Contact = toContact(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}
