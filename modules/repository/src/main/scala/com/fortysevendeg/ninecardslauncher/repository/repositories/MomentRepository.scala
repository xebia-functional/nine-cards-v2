package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
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

  val momentNotificationUri = uriCreator.parse(momentUriNotificationString)

  def addMoment(data: MomentData): CatsService[Moment] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = momentUri,
            values = values,
            notificationUri = Some(momentNotificationUri))

          Moment(id = id, data = data)
        }
      }
    }

  def addMoments(datas: Seq[MomentData]): CatsService[Seq[Moment]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {

          val values = datas map createMapValues

          val ids = contentResolverWrapper.inserts(
            authority = NineCardsUri.authorityPart,
            uri = momentUri,
            allValues = values,
            notificationUri = Some(momentNotificationUri))

          datas zip ids map {
            case (data, id) => Moment(id = id, data = data)
          }
        }
      }
    }

  def deleteMoments(where: String = ""): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = momentUri,
            where = where,
            notificationUri = Some(momentNotificationUri))
        }
      }
    }

  def deleteMoment(moment: Moment): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = momentUri,
            id = moment.id,
            notificationUri = Some(momentNotificationUri))
        }
      }
    }

  def findMomentById(id: Int): CatsService[Option[Moment]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
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
    orderBy: String = ""): CatsService[Seq[Moment]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
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
    orderBy: String = ""): CatsService[IterableCursor[Moment]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = momentUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(momentFromCursor)
        }
      }
    }

  def updateMoment(item: Moment): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(item.data)

          contentResolverWrapper.updateById(
            uri = momentUri,
            id = item.id,
            values = values,
            notificationUri = Some(momentNotificationUri))
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
