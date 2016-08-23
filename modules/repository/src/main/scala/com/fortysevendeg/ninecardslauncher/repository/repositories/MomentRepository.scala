package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toMoment
import com.fortysevendeg.ninecardslauncher.repository.model.{Moment, MomentData}
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import com.fortysevendeg.ninecardslauncher.repository.repositories.RepositoryUtils._

import scala.language.postfixOps
import scalaz.concurrent.Task

class MomentRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val momentUri = uriCreator.parse(momentUriString)

  val momentNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$momentUriPath")

  def addMoment(data: MomentData): ServiceDef2[Moment, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = momentUri,
            values = values,
            notificationUris = Seq(momentNotificationUri))

          Moment(id = id, data = data)
        }
      }
    }

  def addMoments(datas: Seq[MomentData]): ServiceDef2[Seq[Moment], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {

          val values = datas map createMapValues

          val ids = contentResolverWrapper.inserts(
            authority = NineCardsUri.authorityPart,
            uri = momentUri,
            allValues = values,
            notificationUris = Seq(momentNotificationUri))

          datas zip ids map {
            case (data, id) => Moment(id = id, data = data)
          }
        }
      }
    }

  def deleteMoments(where: String = ""): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = momentUri,
            where = where,
            notificationUris = Seq(momentNotificationUri))
        }
      }
    }

  def deleteMoment(moment: Moment): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = momentUri,
            id = moment.id,
            notificationUris = Seq(momentNotificationUri))
        }
      }
    }

  def findMomentById(id: Int): ServiceDef2[Option[Moment], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = momentUri,
            id = id,
            projection = allFields)(getEntityFromCursor(momentEntityFromCursor)) map toMoment
        }
      }
    }

  def fetchMoments(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): ServiceDef2[Seq[Moment], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = momentUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy)(getListFromCursor(momentEntityFromCursor)) map toMoment
        }
      }
    }

  def fetchIterableMoments(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): ServiceDef2[IterableCursor[Moment], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = momentUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(momentFromCursor)
        }
      }
    }

  def updateMoment(item: Moment): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(item.data)

          contentResolverWrapper.updateById(
            uri = momentUri,
            id = item.id,
            values = values,
            notificationUris = Seq(momentNotificationUri))
        }
      }
    }

  private[this] def createMapValues(data: MomentData) =
    Map[String, Any](
      collectionId -> (data.collectionId orNull),
      timeslot -> data.timeslot,
      wifi -> data.wifi,
      headphone -> data.headphone,
      momentType -> flatOrNull(data.momentType))
}
