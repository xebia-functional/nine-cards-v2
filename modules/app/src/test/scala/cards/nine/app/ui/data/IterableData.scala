package cards.nine.app.ui.data

import cards.nine.commons._
import cards.nine.commons.test.data.{ApplicationTestData, DeviceTestData}
import cards.nine.models._
import cards.nine.repository.model.{App => RepositoryApp}
import cards.nine.services.persistence.conversions.AppConversions

trait IterableData
    extends ApplicationTestData
    with DeviceTestData
    with AppConversions
    with NineCardsIntentConversions {

  val mockIterableCursor = new IterableCursor[RepositoryApp] {
    override def count(): Int = 0

    override def moveToPosition(pos: Int): RepositoryApp = javaNull

    override def close(): Unit = ()
  }

  val iterableCursorApps = new IterableAppCursor(mockIterableCursor, toApp) {
    override def count(): Int = seqApplication.length

    override def moveToPosition(pos: Int): ApplicationData = seqApplicationData(pos)

    override def close(): Unit = ()
  }

  val emptyIterableCursorApps = new IterableAppCursor[RepositoryApp](mockIterableCursor, toApp) {
    override def count(): Int = 0

    override def moveToPosition(pos: Int): ApplicationData = javaNull

    override def close(): Unit = ()
  }

  val iterableCursorContact = new IterableCursor[Contact] {
    override def count(): Int = seqContact.length

    override def moveToPosition(pos: Int): Contact = seqContact(pos)

    override def close(): Unit = ()
  }

  val iterableContact = new IterableContacts(iterableCursorContact)

}
