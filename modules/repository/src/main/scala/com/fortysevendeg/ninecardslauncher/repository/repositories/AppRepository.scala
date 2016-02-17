package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toApp
import com.fortysevendeg.ninecardslauncher.repository.model.{App, AppData, DataCounter}
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import org.joda.time.DateTime

import scalaz.concurrent.Task

class AppRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val appUri = uriCreator.parse(appUriString)

  val abc = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ"

  val wildcard = "#"

  val game = "GAME"

  def addApp(data: AppData): ServiceDef2[App, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            name -> data.name,
            packageName -> data.packageName,
            className -> data.className,
            category -> data.category,
            imagePath -> data.imagePath,
            colorPrimary -> data.colorPrimary,
            dateInstalled -> data.dateInstalled,
            dateUpdate -> data.dateUpdate,
            version -> data.version,
            installedFromGooglePlay -> data.installedFromGooglePlay)

          val id = contentResolverWrapper.insert(
            uri = appUri,
            values = values)

          App(
            id = id,
            data = data)
        }
      }
    }

  def deleteApps(where: String = ""): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = appUri,
            where = where)
        }
      }
    }

  def deleteApp(app: App): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = appUri,
            id = app.id)
        }
      }
    }

  def deleteAppByPackage(packageName: String): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = appUri,
            where = s"${AppEntity.packageName} = ?",
            whereParams = Seq(packageName))
        }
      }
    }

  def fetchApps(orderBy: String = ""): ServiceDef2[Seq[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
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
    orderBy: String = ""): ServiceDef2[IterableCursor[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = appUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(appFromCursor)
        }
      }
    }

  def fetchAlphabeticalAppsCounter: ServiceDef2[Seq[DataCounter], RepositoryException] =
    toDataCounter(
      fetchData = getNamesAlphabetically,
      normalize = (name: String) => name.substring(0, 1).toUpperCase match {
        case t if abc.contains(t) => t
        case _ => wildcard
      })

  def fetchCategorizedAppsCounter: ServiceDef2[Seq[DataCounter], RepositoryException] =
    toDataCounter(
      fetchData = getCategoriesAlphabetically,
      normalize = {
        case t if t.startsWith(game) => game
        case t => t
      })

  def fetchInstallationDateAppsCounter: ServiceDef2[Seq[DataCounter], RepositoryException] =
    toInstallationDateDataCounter(fetchData = getInstallationDate)

  def findAppById(id: Int): ServiceDef2[Option[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = appUri,
            id = id,
            projection = allFields)(getEntityFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchAppByPackage(packageName: String): ServiceDef2[Option[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetch(
            uri = appUri,
            projection = allFields,
            where = s"${AppEntity.packageName} = ?",
            whereParams = Seq(packageName))(getEntityFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchAppsByCategory(category: String, orderBy: String = ""): ServiceDef2[Seq[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = appUri,
            projection = allFields,
            where = s"${AppEntity.category} = ?",
            whereParams = Seq(category),
            orderBy = orderBy)(getListFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchIterableAppsByCategory(category: String, orderBy: String = ""): ServiceDef2[IterableCursor[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = appUri,
            projection = allFields,
            where = s"${AppEntity.category} = ?",
            whereParams = Seq(category),
            orderBy = orderBy).toIterator(appFromCursor)
        }
      }
    }

  def updateApp(app: App): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            name -> app.data.name,
            packageName -> app.data.packageName,
            className -> app.data.className,
            category -> app.data.category,
            imagePath -> app.data.imagePath,
            colorPrimary -> app.data.colorPrimary,
            dateInstalled -> app.data.dateInstalled,
            dateUpdate -> app.data.dateUpdate,
            version -> app.data.version,
            installedFromGooglePlay -> app.data.installedFromGooglePlay)

          contentResolverWrapper.updateById(
            uri = appUri,
            id = app.id,
            values = values
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

  private[this] def toDataCounter(
    fetchData: => Seq[String],
    normalize: (String) => String = (term) => term): ServiceDef2[Seq[DataCounter], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
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
   fetchData: => Seq[Long]): ServiceDef2[Seq[DataCounter], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
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

  case class InstallationDateInterval(term: String, date: DateTime)

}
