package com.fortysevendeg.ninecardslauncher.process.user.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.user._
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.api.models.AndroidDevice
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User
import rapture.core.{Answer, Errata, Result, Unforeseen}

import scalaz.concurrent.Task

class UserProcessImpl(
  apiServices: ApiServices,
  persistenceServices: PersistenceServices)
  extends UserProcess
  with ImplicitsUserException
  with Conversions {

  private[this] val syncInstallationErrorMessage = "Installation not updated"

  private[this] val noActiveUserErrorMessage = "No active user"

  val emptyUserRequest = AddUserRequest(None, None, None, None, None, None)

  override def signIn(email: String, device: Device)(implicit context: ContextSupport) = {
    context.getActiveUserId map { id =>
      (for {
        loginResponse <- apiServices.login(email, toGoogleDevice(device))
        Some(userDB) <- persistenceServices.findUserById(FindUserByIdRequest(id))
        _ <- persistenceServices.updateUser(toUpdateRequest(id, userDB, loginResponse, device))
        _ <- syncInstallation(id, None, loginResponse.user.id, None)
      } yield SignInResponse(loginResponse.statusCode)).resolve[UserException]
    } getOrElse {
      Service(Task(Result.errata[SignInResponse, UserException](UserException(noActiveUserErrorMessage))))
    }
  }

  override def register(implicit context: ContextSupport) =
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

  override def unregister(implicit context: ContextSupport) =
    context.getActiveUserId map { id =>
      val update = UpdateUserRequest(id, None, None, None, None, None, None)
      (for {
        _ <- syncInstallation(id, None, None, None)
        _ <- persistenceServices.updateUser(update)
      } yield ()).resolve[UserException]
    } getOrElse {
      Service(Task(Result.answer[Unit, UserException](())))
    }

  private[this] def getFirstOrAddUser(implicit context: ContextSupport): ServiceDef2[User, UserException] =
    (for {
      maybeUsers <- persistenceServices.fetchUsers
      user <- maybeUsers.headOption map (user => Service(Task(Result.answer[User, PersistenceServiceException](user)))) getOrElse {
        persistenceServices.addUser(emptyUserRequest)
      }
    } yield user).resolve[UserException]

  private[this] def checkOrAddUser(id: Int)(implicit context: ContextSupport): ServiceDef2[User, UserException] =
    (for {
      maybeUser <- persistenceServices.findUserById(FindUserByIdRequest(id))
      user <- maybeUser map (user => Service(Task(Result.answer[User, PersistenceServiceException](user)))) getOrElse {
        persistenceServices.addUser(emptyUserRequest)
      }
    } yield user).resolve[UserException]

  private[this] def syncInstallation(
    id: Int,
    installationId: Option[String],
    userId: Option[String],
    deviceToken: Option[String])(implicit context: ContextSupport): ServiceDef2[Int, UserException] =
    installationId map { id =>
      Service {
        apiServices.updateInstallation(
          id = id,
          deviceType = Some(AndroidDevice),
          deviceToken = deviceToken,
          userId = userId).run map {
          case Answer(r) => Result.answer[Int, UserException](r.statusCode)
          // TODO - This need to be improved in ticket 9C-214
          case Errata(_) => Result.errata[Int, UserException](UserException(syncInstallationErrorMessage))
          case Unforeseen(ex) => Result.errata[Int, UserException](UserException(syncInstallationErrorMessage, Some(ex)))
        }
      }
    } getOrElse {
      (for {
        installationResponse <- apiServices.createInstallation(
          deviceType = Some(AndroidDevice),
          deviceToken = deviceToken,
          userId = userId)
        Some(userDB) <- persistenceServices.findUserById(FindUserByIdRequest(id))
        _ <- persistenceServices.updateUser(toUpdateRequest(id, userDB, installationResponse))
      } yield installationResponse.statusCode).resolve[UserException]
    }

}
