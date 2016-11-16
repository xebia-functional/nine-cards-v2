package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.components.models.{LauncherData, LauncherMoment, MomentWorkSpace}
import cards.nine.commons._
import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.{ApplicationTestData, DeviceTestData}
import cards.nine.models
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.{Application, Contact, IterableAppCursor}
import cards.nine.repository.model.{App => RepositoryApp}
import cards.nine.services.persistence.conversions.AppConversions

trait LauncherTestData
  extends DeviceTestData
  with ApplicationTestData
  with AppConversions {

  val idWidget = 1
  val appWidgetId = 1

  val launcherMoment = LauncherMoment(momentType = Option(NineCardsMoment.defaultMoment), collection = None)

  def launcherData(num: Int = 0) =
    LauncherData(
      workSpaceType = MomentWorkSpace,
      moment = Option(launcherMoment),
      collections = Seq.empty,
      positionByType = 0 + num)

  val launcherData: LauncherData = launcherData(0)
  val seqLauncherData: Seq[LauncherData] = Seq(launcherData(0),launcherData(1),launcherData(2))

  val numberPhone = "123456789"
  val packageName = "packageName"
  val errorMenu = 0

  val keyword: String = "keyword"
  val querry: String = "querry"

  val position: Int = 1
  val positionFrom: Int = 1
  val positionFromNoExist: Int = 50
  val positionTo: Int = 2

  val iterableCursorContact = new IterableCursor[Contact] {
    override def count(): Int = seqContact.length

    override def moveToPosition(pos: Int): Contact = seqContact(pos)

    override def close(): Unit = ()
  }

  val iterableContact = new models.IterableContacts(iterableCursorContact)


  val mockIterableCursor = new IterableCursor[RepositoryApp] {
    override def count(): Int = 0

    override def moveToPosition(pos: Int): RepositoryApp = javaNull

    override def close(): Unit = ()
  }

  val iterableCursorApps = new IterableAppCursor(mockIterableCursor, toApp) {
    override def count(): Int = seqApplication.length

    override def moveToPosition(pos: Int): Application = seqApplication(pos)

    override def close(): Unit = ()
  }

  val iterableApps = new IterableAppCursor(iterableCursorApps, toApp)
}
