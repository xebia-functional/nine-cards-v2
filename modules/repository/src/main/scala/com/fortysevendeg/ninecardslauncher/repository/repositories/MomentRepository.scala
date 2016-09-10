package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toMoment
import com.fortysevendeg.ninecardslauncher.repository.model.{Moment, MomentData}
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import com.fortysevendeg.ninecardslauncher.repository.repositories.RepositoryUtils._

import scala.language.postfixOps

class MomentRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val momentUri = uriCreator.parse(momentUriString)

  val momentNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$momentUriPath")

  def addMoment(data: MomentData): TaskService[Moment] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(data)

        val id = contentResolverWrapper.insert(
          uri = momentUri,
          values = values,
          notificationUris = Seq(momentNotificationUri))

        Moment(id = id, data = data)
      }
    }

  def addMoments(datas: Seq[MomentData]): TaskService[Seq[Moment]] =
    TaskService {
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

  def deleteMoments(where: String = ""): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.delete(
          uri = momentUri,
          where = where,
          notificationUris = Seq(momentNotificationUri))
      }
    }

  def deleteMoment(moment: Moment): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.deleteById(
          uri = momentUri,
          id = moment.id,
          notificationUris = Seq(momentNotificationUri))
      }
    }

  def findMomentById(id: Int): TaskService[Option[Moment]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.findById(
          uri = momentUri,
          id = id,
          projection = allFields)(getEntityFromCursor(momentEntityFromCursor)) map toMoment
      }
    }

  def fetchMoments(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): TaskService[Seq[Moment]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.fetchAll(
          uri = momentUri,
          projection = allFields,
          where = where,
          whereParams = whereParams,
          orderBy = orderBy)(getListFromCursor(momentEntityFromCursor)) map toMoment
      }
    }

  def fetchIterableMoments(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): TaskService[IterableCursor[Moment]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.getCursor(
          uri = momentUri,
          projection = allFields,
          where = where,
          whereParams = whereParams,
          orderBy = orderBy).toIterator(momentFromCursor)
      }
    }

  def updateMoment(item: Moment): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(item.data)

        contentResolverWrapper.updateById(
          uri = momentUri,
          id = item.id,
          values = values,
          notificationUris = Seq(momentNotificationUri))
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
