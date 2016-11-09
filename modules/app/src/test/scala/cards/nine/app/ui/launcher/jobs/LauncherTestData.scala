package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.components.models.{LauncherMoment, LauncherData, MomentWorkSpace}
import cards.nine.commons._
import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.{ApplicationTestData, DeviceTestData}
import cards.nine.models.{Application, Contact}
import cards.nine.models.types.NineCardsMoment
import cards.nine.process.device.models.{IterableApps, IterableContacts}
import cards.nine.repository.model.{App => RepositoryApp}
import cards.nine.services.persistence.models.{IterableApps => ServicesIterableApps}

trait LauncherTestData
  extends DeviceTestData
    with ApplicationTestData {

  val idWidget = 1
  val appWidgetId = 1

  val launcherMoment = LauncherMoment(momentType = Option(NineCardsMoment.defaultMoment), collection = None)
  val launcherData =
    LauncherData(
      workSpaceType = MomentWorkSpace,
      moment = Option(launcherMoment),
      collections = Seq.empty,
      positionByType = 0)

  val numberPhone = "123456789"
  val packageName = "packageName"
  val errorMenu = 0

  val keyword: String = "keyword"
  val querry: String = "querry"

  val iterableCursorContact = new IterableCursor[Contact] {
    override def count(): Int = seqContact.length

    override def moveToPosition(pos: Int): Contact = seqContact(pos)

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
}
