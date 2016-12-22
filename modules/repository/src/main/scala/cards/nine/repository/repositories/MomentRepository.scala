package cards.nine.repository.repositories

import cards.nine.commons.CatchAll
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.contentresolver.NotificationUri._
import cards.nine.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.IterableCursor
import cards.nine.models.IterableCursor._
import cards.nine.repository.Conversions.toMoment
import cards.nine.repository.model.{Moment, MomentData}
import cards.nine.repository.provider.MomentEntity._
import cards.nine.repository.provider.NineCardsUri._
import cards.nine.repository.provider.{MomentEntity, NineCardsUri}
import cards.nine.repository.repositories.RepositoryUtils._
import cards.nine.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scala.language.postfixOps

class MomentRepository(contentResolverWrapper: ContentResolverWrapper, uriCreator: UriCreator)
    extends ImplicitsRepositoryExceptions {

  val momentUri = uriCreator.parse(momentUriString)

  val momentNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$momentUriPath")

  def addMoment(data: MomentData): TaskService[Moment] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(data)

        val id = contentResolverWrapper
          .insert(uri = momentUri, values = values, notificationUris = Seq(momentNotificationUri))

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
        contentResolverWrapper
          .delete(uri = momentUri, where = where, notificationUris = Seq(momentNotificationUri))
      }
    }

  def deleteMoment(id: Int): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper
          .deleteById(uri = momentUri, id = id, notificationUris = Seq(momentNotificationUri))
      }
    }

  def findMomentById(id: Int): TaskService[Option[Moment]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.findById(uri = momentUri, id = id, projection = allFields)(
          getEntityFromCursor(momentEntityFromCursor)) map toMoment
      }
    }

  def fetchMomentByCollectionId(collectionId: Int): TaskService[Option[Moment]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.fetch(
          uri = momentUri,
          projection = allFields,
          where = s"${MomentEntity.collectionId} = ?",
          whereParams = Seq(collectionId.toString),
          orderBy = "")(getEntityFromCursor(momentEntityFromCursor)) map toMoment
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
        contentResolverWrapper
          .getCursor(
            uri = momentUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy)
          .toIterator(momentFromCursor)
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
      timeslot     -> data.timeslot,
      wifi         -> data.wifi,
      bluetooth    -> data.bluetooth,
      headphone    -> data.headphone,
      momentType   -> flatOrNull(data.momentType))
}
