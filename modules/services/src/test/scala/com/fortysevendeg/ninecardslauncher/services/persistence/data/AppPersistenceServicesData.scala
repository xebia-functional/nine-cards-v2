package cards.nine.services.persistence.data

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.repository.model.{App, AppData, DataCounter}
import cards.nine.services.persistence.models._

import scala.util.Random

trait AppPersistenceServicesData {

  val termDataCounter: String = Random.nextString(1)
  val countDataCounter: Int = Random.nextInt(2)

  def applicationData(num: Int = 0) = AppData(
    name = applicationName,
    packageName = applicationPackageName,
    className = applicationClassName,
    category = applicationCategoryStr,
    dateInstalled = dateInstalled,
    dateUpdate = dateUpdated,
    version = version,
    installedFromGooglePlay = installedFromGooglePlay))

  val repoAppData: AppData = applicationData(0)
  val seqRepoAppData: Seq[AppData] = Seq(applicationData(0), applicationData(1), applicationData(2))

  def application(num: Int = 0) = App(
    id = applicationId + item,
    data = applicationData(num))

  val repoApp: App = application(0)
  val seqRepoApp: Seq[App] = Seq(application(0), application(1), application(2))

  val iterableCursorApp = new IterableCursor[App] {
    override def count(): Int = seqRepoApp.length

    override def moveToPosition(pos: Int): App = seqRepoApp(pos)

    override def close(): Unit = ()
  }

  val iterableApps = new IterableApps(iterableCursorApp)

  def createDataCounter(i: Int): DataCounter =
    DataCounter(
      term = s"$i - $termDataCounter",
      count = countDataCounter)

  val dataCounters = 1 to 10 map createDataCounter

}
