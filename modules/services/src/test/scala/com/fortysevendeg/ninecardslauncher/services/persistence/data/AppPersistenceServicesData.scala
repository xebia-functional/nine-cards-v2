package com.fortysevendeg.ninecardslauncher.services.persistence.data

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor
import com.fortysevendeg.ninecardslauncher.services.persistence.{UpdateAppRequest, AddAppRequest}
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import com.fortysevendeg.ninecardslauncher.repository.model.{App => RepositoryApp, AppData => RepositoryAppData}

import scala.util.Random

trait AppPersistenceServicesData extends PersistenceServicesData {

  val appId: Int =  Random.nextInt(10)

  def createSeqApp(
    num: Int = 5,
    id: Int = appId,
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    imagePath: String = imagePath,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): Seq[App] = List.tabulate(num)(
    item => App(
      id = id + item,
      name = name,
      packageName = packageName,
      className = className,
      category = category,
      imagePath = imagePath,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay))

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
    imagePath: String = imagePath,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): RepositoryAppData = RepositoryAppData(
    name = name,
    packageName = packageName,
    className = className,
    category = category,
    imagePath = imagePath,
    dateInstalled = dateInstalled,
    dateUpdate = dateUpdate,
    version = version,
    installedFromGooglePlay = installedFromGooglePlay)


  val seqApp: Seq[App] = createSeqApp()
  val app: App = seqApp(0)
  val repoAppData: RepositoryAppData = createRepoAppData()
  val seqRepoApp: Seq[RepositoryApp] = createSeqRepoApp(data = repoAppData)
  val repoApp: RepositoryApp = seqRepoApp(0)

  def createAddAppRequest(
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    imagePath: String = imagePath,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): AddAppRequest =
    AddAppRequest(
      name = name,
      packageName = packageName,
      className = className,
      category = category,
      imagePath = imagePath,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay)

  def createUpdateAppRequest(
    id: Int = appId,
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    imagePath: String = imagePath,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): UpdateAppRequest =
    UpdateAppRequest(
      id = id,
      name = name,
      packageName = packageName,
      className = className,
      category = category,
      imagePath = imagePath,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay)

  val iterableCursorApp = new IterableCursor[RepositoryApp] {
    override def count(): Int = seqRepoApp.length
    override def moveToPosition(pos: Int): RepositoryApp = seqRepoApp(pos)
    override def close(): Unit = ()
  }
  val iterableApps = new IterableApps(iterableCursorApp)



}
