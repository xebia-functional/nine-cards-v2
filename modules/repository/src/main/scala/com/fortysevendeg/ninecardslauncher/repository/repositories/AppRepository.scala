package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toApp
import com.fortysevendeg.ninecardslauncher.repository.model.{App, AppData, DataCounter}
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.provider.{AppEntity, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import org.joda.time.DateTime

import scalaz.concurrent.Task

class AppRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val appUri = uriCreator.parse(appUriString)

  val appNotificationUri = uriCreator.parse(appUriNotificationString)

  val abc = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ"

  val wildcard = "#"

  val game = "GAME"

  def addApp(data: AppData): CatsService[App] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = appUri,
            values = values,
            notificationUri = Some(appNotificationUri))

          App(
            id = id,
            data = data)
        }
      }
    }

  def addApps(datas: Seq[AppData]): CatsService[Unit] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = datas map createMapValues

          contentResolverWrapper.inserts(
            authority = NineCardsUri.authorityPart,
            uri = appUri,
            allValues = values,
            notificationUri = Some(appNotificationUri))
        }
      }
    }

  def deleteApps(where: String = ""): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = appUri,
            where = where,
            notificationUri = Some(appNotificationUri))
        }
      }
    }

  def deleteApp(app: App): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = appUri,
            id = app.id,
            notificationUri = Some(appNotificationUri))
        }
      }
    }

  def deleteAppByPackage(packageName: String): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = appUri,
            where = s"${AppEntity.packageName} = ?",
            whereParams = Seq(packageName),
            notificationUri = Some(appNotificationUri))
        }
      }
    }

  def fetchApps(orderBy: String = ""): CatsService[Seq[App]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = appUri,
            projection = allFields,
            orderBy = orderBy)(getListFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchIterableApps(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): CatsService[IterableCursor[App]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = appUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(appFromCursor)
        }
      }
    }

  def fetchAlphabeticalAppsCounter: CatsService[Seq[DataCounter]] =
    toDataCounter(
      fetchData = getNamesAlphabetically,
      normalize = (name: String) => name.substring(0, 1).toUpperCase match {
        case t if abc.contains(t) => t
        case _ => wildcard
      })

  def fetchCategorizedAppsCounter: CatsService[Seq[DataCounter]] =
    toDataCounter(
      fetchData = getCategoriesAlphabetically,
      normalize = {
        case t if t.startsWith(game) => game
        case t => t
      })

  def fetchInstallationDateAppsCounter: CatsService[Seq[DataCounter]] =
    toInstallationDateDataCounter(fetchData = getInstallationDate)

  def findAppById(id: Int): CatsService[Option[App]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = appUri,
            id = id,
            projection = allFields)(getEntityFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchAppByPackage(packageName: String): CatsService[Option[App]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.fetch(
            uri = appUri,
            projection = allFields,
            where = s"${AppEntity.packageName} = ?",
            whereParams = Seq(packageName))(getEntityFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchAppByPackages(packageName: Seq[String]): CatsService[Seq[App]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = appUri,
            projection = allFields,
            where = s"${AppEntity.packageName} IN (${packageName.mkString("\"", ",", "\"")})")(getListFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchAppsByCategory(category: String, orderBy: String = ""): CatsService[Seq[App]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val (where, param) = whereCategory(category)
          contentResolverWrapper.fetchAll(
            uri = appUri,
            projection = allFields,
            where = where,
            whereParams = Seq(param),
            orderBy = orderBy)(getListFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchIterableAppsByCategory(category: String, orderBy: String = ""): CatsService[IterableCursor[App]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val (where, param) = whereCategory(category)
          contentResolverWrapper.getCursor(
            uri = appUri,
            projection = allFields,
            where = where,
            whereParams = Seq(param),
            orderBy = orderBy).toIterator(appFromCursor)
        }
      }
    }

  def updateApp(app: App): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(app.data)

          contentResolverWrapper.updateById(
            uri = appUri,
            id = app.id,
            values = values,
            notificationUri = Some(appNotificationUri)
          )
        }
      }
    }

  protected def getNamesAlphabetically: Seq[String] =
    getListFromCursor(nameFromCursor)(contentResolverWrapper.getCursor(
      uri = appUri,
      projection = Seq(name),
      orderBy = s"$name COLLATE NOCASE ASC"))

  protected def getCategoriesAlphabetically: Seq[String] =
    getListFromCursor(categoryFromCursor)(contentResolverWrapper.getCursor(
      uri = appUri,
      projection = Seq(category),
      orderBy = s"$category COLLATE NOCASE ASC"))

  protected def getInstallationDate: Seq[Long] =
    getListFromCursor(dateInstalledFromCursor)(contentResolverWrapper.getCursor(
      uri = appUri,
      projection = Seq(dateInstalled),
      orderBy = s"$dateInstalled DESC"))

  private[this] def whereCategory(category: String): (String, String) = category match {
    case t if t.startsWith(game) =>
      (s"${AppEntity.category} LIKE ?", s"$category%")
    case _ =>
      (s"${AppEntity.category} = ?", category)
  }

  private[this] def toDataCounter(
    fetchData: => Seq[String],
    normalize: (String) => String = (term) => term): CatsService[Seq[DataCounter]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val data = fetchData
          data.foldLeft(Seq.empty[DataCounter]) { (acc, name) =>
            val term = normalize(name)
            val lastWithSameTerm = acc.lastOption flatMap {
              case last if last.term == term => Some(last)
              case _ => None
            }
            lastWithSameTerm map { c =>
              acc.dropRight(1) :+ c.copy(count = c.count + 1)
            } getOrElse acc :+ DataCounter(term, 1)
          }
        }
      }
    }

  private[this] def toInstallationDateDataCounter(
   fetchData: => Seq[Long]): CatsService[Seq[DataCounter]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val now = new DateTime()
          val moreOfTwoMoths = "moreOfTwoMoths"
          val dates = Seq(
            InstallationDateInterval("oneWeek", now.minusWeeks(1)),
            InstallationDateInterval("twoWeeks", now.minusWeeks(2)),
            InstallationDateInterval("oneMonth", now.minusMonths(1)),
            InstallationDateInterval("twoMonths", now.minusMonths(2)),
            InstallationDateInterval("fourMonths", now.minusMonths(4)),
            InstallationDateInterval("sixMonths", now.minusMonths(6)))
          val data = fetchData
          data.foldLeft(Seq.empty[DataCounter]) { (acc, date) =>
            val installationDate = new DateTime(date)
            val term = termInterval(installationDate, dates) map (_.term) getOrElse moreOfTwoMoths
            val lastWithSameTerm = acc.lastOption flatMap {
              case last if last.term == term => Some(last)
              case _ => None
            }
            lastWithSameTerm map { c =>
              acc.dropRight(1) :+ c.copy(count = c.count + 1)
            } getOrElse acc :+ DataCounter(term, 1)
          }
        }
      }
    }

  private[this] def termInterval(installationDate: DateTime, intervals: Seq[InstallationDateInterval]): Option[InstallationDateInterval] =
    intervals find { interval =>
      installationDate.isAfter(interval.date)
    }

  private[this] def createMapValues(data: AppData) =
    Map[String, Any](
      name -> data.name,
      packageName -> data.packageName,
      className -> data.className,
      category -> data.category,
      imagePath -> data.imagePath,
      dateInstalled -> data.dateInstalled,
      dateUpdate -> data.dateUpdate,
      version -> data.version,
      installedFromGooglePlay -> data.installedFromGooglePlay)

  case class InstallationDateInterval(term: String, date: DateTime)

}
