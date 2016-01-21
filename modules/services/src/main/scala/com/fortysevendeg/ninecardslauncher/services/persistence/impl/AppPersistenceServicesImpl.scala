package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableApps

trait AppPersistenceServicesImpl {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def fetchApps(orderBy: FetchAppOrder, ascending: Boolean = true) = {
    val orderByString = s"${toStringOrderBy(orderBy)} ${toStringDirection(ascending)} ${toSecondaryOrderBy(orderBy)}"

    val appSeq = for {
      apps <- appRepository.fetchApps(orderByString)
    } yield apps map toApp

    appSeq.resolve[PersistenceServiceException]
  }

  def findAppByPackage(packageName: String) =
    (for {
      app <- appRepository.fetchAppByPackage(packageName)
    } yield app map toApp).resolve[PersistenceServiceException]

  def addApp(request: AddAppRequest) =
    (for {
      app <- appRepository.addApp(toRepositoryAppData(request))
    } yield toApp(app)).resolve[PersistenceServiceException]

  def deleteAllApps() =
    (for {
      deleted <- appRepository.deleteApps()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteAppByPackage(packageName: String) =
    (for {
      deleted <- appRepository.deleteAppByPackage(packageName)
    } yield deleted).resolve[PersistenceServiceException]

  def updateApp(request: UpdateAppRequest) =
    (for {
      updated <- appRepository.updateApp(toRepositoryApp(request))
    } yield updated).resolve[PersistenceServiceException]

  def fetchIterableApps(orderBy: FetchAppOrder, ascending: Boolean = true) = {
    val orderByString = s"${toStringOrderBy(orderBy)} ${toStringDirection(ascending)} ${toSecondaryOrderBy(orderBy)}"

    val appSeq = for {
      iter <- appRepository.fetchIterableApps(orderBy = orderByString)
    } yield new IterableApps(iter)

    appSeq.resolve[PersistenceServiceException]
  }

  def fetchIterableAppsByKeyword(keyword: String, orderBy: FetchAppOrder, ascending: Boolean = true) = {
    val orderByString = s"${toStringOrderBy(orderBy)} ${toStringDirection(ascending)} ${toSecondaryOrderBy(orderBy)}"

    val appSeq = for {
      iter <- appRepository.fetchIterableApps(
        where = toStringWhere(keyword),
        whereParams = Seq(keyword),
        orderBy = orderByString)
    } yield new IterableApps(iter)

    appSeq.resolve[PersistenceServiceException]
  }

  def fetchAppsByCategory(category: String, orderBy: FetchAppOrder, ascending: Boolean = true) = {
    val orderByString = s"${toStringOrderBy(orderBy)} ${toStringDirection(ascending)} ${toSecondaryOrderBy(orderBy)}"

    val appSeq = for {
      apps <- appRepository.fetchAppsByCategory(
        category = toStringWhere(category),
        orderBy = orderByString)
    } yield apps map toApp

    appSeq.resolve[PersistenceServiceException]
  }

  def fetchIterableAppsByCategory(category: String, orderBy: FetchAppOrder, ascending: Boolean = true) = {
    val orderByString = s"${toStringOrderBy(orderBy)} ${toStringDirection(ascending)} ${toSecondaryOrderBy(orderBy)}"

    val appSeq = for {
      iter <- appRepository.fetchIterableAppsByCategory(
        category = toStringWhere(category),
        orderBy = orderByString)
    } yield new IterableApps(iter)

    appSeq.resolve[PersistenceServiceException]
  }

  def fetchAlphabeticalAppsCounter =
    (for {
      counters <- appRepository.fetchAlphabeticalAppsCounter
    } yield toDataCounterSeq(counters)).resolve[PersistenceServiceException]

  def fetchCategorizedAppsCounter =
    (for {
      counters <- appRepository.fetchCategorizedAppsCounter
    } yield toDataCounterSeq(counters)).resolve[PersistenceServiceException]

  private[this] def toStringDirection(ascending: Boolean): String =
    if (ascending) "ASC" else "DESC"

  private[this] def toSecondaryOrderBy(orderBy: FetchAppOrder): String = orderBy match {
    case OrderByName => ""
    case _ => s", ${AppEntity.name} COLLATE NOCASE ASC"
  }

  private[this] def toStringWhere(keyword: String): String = s"${AppEntity.name} LIKE '%$keyword%' "

  private[this] def toStringOrderBy(orderBy: FetchAppOrder): String = orderBy match {
    case OrderByName => s"${AppEntity.name} COLLATE NOCASE"
    case OrderByInstallDate => AppEntity.dateInstalled
    case OrderByCategory => AppEntity.category
  }

}
