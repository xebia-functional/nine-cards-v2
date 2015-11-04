package com.fortysevendeg.nineuserslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toUser
import com.fortysevendeg.ninecardslauncher.repository.model.{User, UserData}
import com.fortysevendeg.ninecardslauncher.repository.provider.UserEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scalaz.concurrent.Task

class UserRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val userUri = uriCreator.parse(userUriString)

  def addUser(data: UserData): ServiceDef2[User, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            userId -> (data.userId orNull),
            email -> (data.email orNull),
            sessionToken -> (data.sessionToken orNull),
            installationId -> (data.installationId orNull),
            deviceToken -> (data.deviceToken orNull),
            androidToken -> (data.androidToken orNull))

          val id = contentResolverWrapper.insert(
            uri = userUri,
            values = values)

          User(id = id, data = data)
        }
      }
    }

  def deleteUser(user: User): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = userUri,
            id = user.id)
        }
      }
    }

  def findUserById(id: Int): ServiceDef2[Option[User], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = userUri,
            id = id,
            projection = allFields)(getEntityFromCursor(userEntityFromCursor)) map toUser
        }
      }
    }

  def fetchUsers: ServiceDef2[Seq[User], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = userUri,
            projection = allFields)(getListFromCursor(userEntityFromCursor)) map toUser
        }
      }
    }

  def updateUser(item: User): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            userId -> (item.data.userId orNull),
            email -> (item.data.email orNull),
            sessionToken -> (item.data.sessionToken orNull),
            installationId -> (item.data.installationId orNull),
            deviceToken -> (item.data.deviceToken orNull),
            androidToken -> (item.data.androidToken orNull))

          contentResolverWrapper.updateById(
            uri = userUri,
            id = item.id,
            values = values)
        }
      }
    }
}
