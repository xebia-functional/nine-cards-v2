package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toUser
import com.fortysevendeg.ninecardslauncher.repository.model.{User, UserData}
import com.fortysevendeg.ninecardslauncher.repository.provider.UserEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.repository.repositories.RepositoryUtils._

import scala.language.postfixOps
import monix.eval.Task

class UserRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val userUri = uriCreator.parse(userUriString)

  val userNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$userUriPath")

  def addUser(data: UserData): TaskService[User] =
    TaskService {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = userUri,
            values = values,
            notificationUris = Seq(userNotificationUri))

          User(id = id, data = data)
        }
      }
    }

  def deleteUsers(where: String = ""): TaskService[Int] =
    TaskService {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = userUri,
            where = where,
            notificationUris = Seq(userNotificationUri))
        }
      }
    }

  def deleteUser(user: User): TaskService[Int] =
    TaskService {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = userUri,
            id = user.id,
            notificationUris = Seq(userNotificationUri))
        }
      }
    }

  def findUserById(id: Int): TaskService[Option[User]] =
    TaskService {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = userUri,
            id = id,
            projection = allFields)(getEntityFromCursor(userEntityFromCursor)) map toUser
        }
      }
    }

  def fetchUsers: TaskService[Seq[User]] =
    TaskService {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = userUri,
            projection = allFields)(getListFromCursor(userEntityFromCursor)) map toUser
        }
      }
    }

  def fetchIterableUsers(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): TaskService[IterableCursor[User]] =
    TaskService {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = userUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(userFromCursor)
        }
      }
    }

  def updateUser(item: User): TaskService[Int] =
    TaskService {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(item.data)

          contentResolverWrapper.updateById(
            uri = userUri,
            id = item.id,
            values = values,
            notificationUris = Seq(userNotificationUri))
        }
      }
    }

  private[this] def createMapValues(data: UserData) =
    Map[String, Any](
      email -> flatOrNull(data.email),
      apiKey -> flatOrNull(data.apiKey),
      sessionToken -> flatOrNull(data.sessionToken),
      deviceToken -> flatOrNull(data.deviceToken),
      marketToken -> flatOrNull(data.marketToken),
      name -> flatOrNull(data.name),
      avatar -> flatOrNull(data.avatar),
      cover -> flatOrNull(data.cover),
      deviceName -> flatOrNull(data.deviceName),
      deviceCloudId -> flatOrNull(data.deviceCloudId))
}
