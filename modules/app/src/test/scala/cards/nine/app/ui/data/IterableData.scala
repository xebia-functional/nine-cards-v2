package cards.nine.app.ui.data

import cards.nine.commons._
import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.{ApplicationTestData, DeviceTestData}
import cards.nine.models._
import cards.nine.process.device.models._
import cards.nine.repository.model.{App => RepositoryApp}
import cards.nine.services.persistence.models.{IterableApps => ServicesIterableApps}

trait IterableData
  extends ApplicationTestData
  with DeviceTestData
  with NineCardsIntentConversions {

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

  val emptyIterableCursorApps = new ServicesIterableApps(mockIterableCursor) {
    override def count(): Int = 0

    override def moveToPosition(pos: Int): Application = javaNull

    override def close(): Unit = ()
  }

  val emptyIterableApps = new IterableApps(emptyIterableCursorApps)

}
