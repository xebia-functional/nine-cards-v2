package cards.nine.services.persistence.data

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.repository.model.{App => RepositoryApp, AppData => RepositoryAppData, DataCounter => RepositoryDataCounter}
import cards.nine.services.persistence.models._

import scala.util.Random

trait AppPersistenceServicesData {

  val termDataCounter: String = Random.nextString(1)
  val countDataCounter: Int = Random.nextInt(2)

  def createSeqRepoApp(
    num: Int = 5,
    id: Int = appId,
    data: RepositoryAppData = createRepoAppData()): Seq[RepositoryApp] =
    List.tabulate(num)(item => RepositoryApp(id = id + item, data = data))

  def createRepoAppData(
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    imagePath: String = "",
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): RepositoryAppData =
    RepositoryAppData(
      name = name,
      packageName = packageName,
      className = className,
      category = category,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay)

  val repoAppData: RepositoryAppData = createRepoAppData()
  val seqRepoApp: Seq[RepositoryApp] = createSeqRepoApp(data = repoAppData)
  val repoApp: RepositoryApp = seqRepoApp(0)

  val iterableCursorApp = new IterableCursor[RepositoryApp] {
    override def count(): Int = seqRepoApp.length

    override def moveToPosition(pos: Int): RepositoryApp = seqRepoApp(pos)

    override def close(): Unit = ()
  }
  val iterableApps = new IterableApps(iterableCursorApp)

  def createDataCounter(i: Int): RepositoryDataCounter =
    RepositoryDataCounter(
      term = s"$i - $termDataCounter",
      count = countDataCounter
    )

  val dataCounters = 1 to 10 map createDataCounter

}
