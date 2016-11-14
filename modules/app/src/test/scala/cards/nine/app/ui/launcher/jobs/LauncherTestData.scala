package cards.nine.app.ui.launcher.jobs

import android.graphics.Color
import cards.nine.app.ui.components.models.{LauncherMoment, LauncherData, MomentWorkSpace}
import cards.nine.commons._
import cards.nine.models.types.theme._
import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.{ApplicationTestData, DeviceTestData}
import cards.nine.models.{ThemeColors, NineCardsTheme, Application, Contact}
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


  val theme = NineCardsTheme(
    name = "light",
    parent = ThemeLight,
    styles = Seq.empty,
    themeColors = ThemeColors(Color.parseColor("#FF9800"), Seq.empty))


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
