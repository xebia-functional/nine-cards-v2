package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toUser
import com.fortysevendeg.ninecardslauncher.repository.model.{User, UserData}
import com.fortysevendeg.ninecardslauncher.repository.provider.UserEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.repository.repositories.RepositoryUtils._

import scala.language.postfixOps
import scalaz.concurrent.Task

class UserRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val userUri = uriCreator.parse(userUriString)

  val userNotificationUri = uriCreator.parse(userUriNotificationString)

  def addUser(data: UserData): CatsService[RepositoryException, User] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = userUri,
            values = values,
            notificationUri = Some(userNotificationUri))

          User(id = id, data = data)
        }
      }
    }

  def deleteUsers(where: String = ""): CatsService[RepositoryException, Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = userUri,
            where = where,
            notificationUri = Some(userNotificationUri))
        }
      }
    }

  def deleteUser(user: User): CatsService[RepositoryException, Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = userUri,
            id = user.id,
            notificationUri = Some(userNotificationUri))
        }
      }
    }

  def findUserById(id: Int): CatsService[RepositoryException, Option[User]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = userUri,
            id = id,
            projection = allFields)(getEntityFromCursor(userEntityFromCursor)) map toUser
        }
      }
    }

  def fetchUsers: CatsService[RepositoryException, Seq[User]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = userUri,
            projection = allFields)(getListFromCursor(userEntityFromCursor)) map toUser
        }
      }
    }

  def fetchIterableUsers(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): CatsService[RepositoryException, IterableCursor[User]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = userUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(userFromCursor)
        }
      }
    }

  def updateUser(item: User): CatsService[RepositoryException, Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(item.data)

          contentResolverWrapper.updateById(
            uri = userUri,
            id = item.id,
            values = values,
            notificationUri = Some(userNotificationUri))
        }
      }
    }

  private[this] def createMapValues(data: UserData) =
    Map[String, Any](
      userId -> flatOrNull(data.userId),
      email -> flatOrNull(data.email),
      sessionToken -> flatOrNull(data.sessionToken),
      installationId -> flatOrNull(data.installationId),
      deviceToken -> flatOrNull(data.deviceToken),
      androidToken -> flatOrNull(data.androidToken),
      name -> flatOrNull(data.name),
      avatar -> flatOrNull(data.avatar),
      cover -> flatOrNull(data.cover),
      deviceName -> flatOrNull(data.deviceName),
      deviceCloudId -> flatOrNull(data.deviceCloudId))
}
