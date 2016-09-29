package com.fortysevendeg.ninecardslauncher.process.user.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.user._
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServices, RequestConfig}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}
import monix.eval.Task
import cats.syntax.either._


class UserProcessImpl(
  apiServices: ApiServices,
  persistenceServices: PersistenceServices)
  extends UserProcess
  with ImplicitsUserException
  with Conversions {

  private[this] val noActiveUserErrorMessage = "No active user"

  val emptyUserRequest = AddUserRequest(None, None, None, None, None, None, None, None, None, None)

  override def signIn(email: String, androidMarketToken: String, emailTokenId: String)(implicit context: ContextSupport) = {
    withActiveUser { id =>
      (for {
        androidId <- persistenceServices.getAndroidId
        loginResponse <- apiServices.login(email, androidId, emailTokenId)
        userDB <- persistenceServices.findUserById(FindUserByIdRequest(id)).resolveOption()
        updateUser = userDB.copy(
          email = Some(email),
          apiKey = Option(loginResponse.apiKey),
          sessionToken = Option(loginResponse.sessionToken),
          marketToken = Option(androidMarketToken))
        _ <- persistenceServices.updateUser(toUpdateRequest(id, updateUser))
      } yield ()).resolve[UserException]
    }
  }

  override def register(implicit context: ContextSupport) = {

    def checkOrAddUser(id: Int)(implicit context: ContextSupport): TaskService[ServicesUser] =
      (for {
        maybeUser <- persistenceServices.findUserById(FindUserByIdRequest(id))
        user <- maybeUser map (user => TaskService(Task(Either.right(user)))) getOrElse {
          persistenceServices.addUser(emptyUserRequest)
        }
      } yield user).resolve[UserException]

    def getFirstOrAddUser(implicit context: ContextSupport): TaskService[ServicesUser] =
      (for {
        maybeUsers <- persistenceServices.fetchUsers
        user <- maybeUsers.headOption map (user => TaskService(Task(Either.right(user)))) getOrElse {
          persistenceServices.addUser(emptyUserRequest)
        }
      } yield user).resolve[UserException]

    context.getActiveUserId map { id =>
      (for {
        user <- checkOrAddUser(id)
        _ = if (id != user.id) context.setActiveUserId(user.id)
      } yield ()).resolve[UserException]
    } getOrElse {
      (for {
        user <- getFirstOrAddUser
        _ = context.setActiveUserId(user.id)
      } yield ()).resolve[UserException]
    }
  }

  override def unregister(implicit context: ContextSupport) =
    withActiveUser { id =>
      val update = UpdateUserRequest(id, None, None, None, None, None, None, None, None, None, None)
      (for {
        user <- persistenceServices.findUserById(FindUserByIdRequest(id)).resolveOption()
        _ <- persistenceServices.updateUser(update)
        _ <- syncInstallation(user.apiKey, user.sessionToken, user.deviceToken, None)
      } yield ()).resolve[UserException]
    }

  override def getUser(implicit context: ContextSupport) =
    withActiveUser { id =>
      (for {
        user <- persistenceServices.findUserById(FindUserByIdRequest(id)).resolveOption()
      } yield toUser(user)).resolve[UserException]
    }

  override def updateUserDevice(
    deviceName: String,
    deviceCloudId: String,
    deviceToken: Option[String] = None)(implicit context: ContextSupport) =
    withActiveUser { id =>
      (for {
        user <- persistenceServices.findUserById(FindUserByIdRequest(id)).resolveOption()
        newUser = user.copy(
          deviceName = Option(deviceName),
          deviceCloudId = Option(deviceCloudId),
          deviceToken = deviceToken orElse user.deviceToken)
        _ <- persistenceServices.updateUser(toUpdateRequest(id = id,user = newUser))
        _ <- syncInstallation(user.apiKey, user.sessionToken, user.deviceToken, newUser.deviceToken)
      } yield ()).resolve[UserException]
    }

  override def updateDeviceToken(deviceToken: String)(implicit context: ContextSupport) =
    withActiveUser { id =>
      (for {
        user <- persistenceServices.findUserById(FindUserByIdRequest(id)).resolveOption()
        _ <- persistenceServices.updateUser(toUpdateRequest(id, user.copy(deviceToken = Option(deviceToken))))
        _ <- syncInstallation(user.apiKey, user.sessionToken, user.deviceToken, Option(deviceToken))
      } yield ()).resolve[UserException]
    }

  private[this] def withActiveUser[T](f: Int => TaskService[T])(implicit context: ContextSupport) =
    context.getActiveUserId map f getOrElse {
      TaskService(Task(Either.left(UserException(noActiveUserErrorMessage))))
    }

  private[this] def syncInstallation(
    maybeApiKey: Option[String],
    maybeSessionToken: Option[String],
    previousDeviceToken: Option[String],
    deviceToken: Option[String])(implicit context: ContextSupport): TaskService[Int] =
    (maybeApiKey, maybeSessionToken) match {
      case (Some(apiKey), Some(sessionToken)) if previousDeviceToken != deviceToken =>
        (for {
          androidId <- persistenceServices.getAndroidId
          response <- apiServices.updateInstallation(deviceToken)(RequestConfig(apiKey, sessionToken, androidId))
        } yield response.statusCode).resolve[UserException]
      case _ => TaskService(Task(Either.right(0)))
    }

}
