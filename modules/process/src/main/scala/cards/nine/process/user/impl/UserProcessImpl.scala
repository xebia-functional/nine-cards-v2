package cards.nine.process.user.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{RequestConfig, User, UserData, UserProfile}
import cards.nine.process.user._
import cards.nine.services.api.ApiServices
import cards.nine.services.persistence._
import cats.syntax.either._
import monix.eval.Task

class UserProcessImpl(
  apiServices: ApiServices,
  persistenceServices: PersistenceServices)
  extends UserProcess
  with ImplicitsUserException {

  private[this] val noActiveUserErrorMessage = "No active user"

  val emptyUser = UserData(None, None, None, None, None, None, None, UserProfile(None, None, None))

  override def signIn(email: String, androidMarketToken: String, emailTokenId: String)(implicit context: ContextSupport) = {
    withActiveUser { id =>
      (for {
        androidId <- persistenceServices.getAndroidId
        loginResponse <- apiServices.login(email, androidId, emailTokenId)
        userDB <- findUserById(id)
        updateUser = userDB.copy(
          id = id,
          email = Some(email),
          apiKey = Option(loginResponse.apiKey),
          sessionToken = Option(loginResponse.sessionToken),
          marketToken = Option(androidMarketToken))
        _ <- persistenceServices.updateUser(updateUser)
      } yield ()).resolve[UserException]
    }
  }

  override def register(implicit context: ContextSupport) = {

    def checkOrAddUser(id: Int)(implicit context: ContextSupport): TaskService[User] =
      (for {
        maybeUser <- persistenceServices.findUserById(id)
        user <- maybeUser map (user => TaskService(Task(Either.right(user)))) getOrElse {
          persistenceServices.addUser(emptyUser)
        }
      } yield user).resolve[UserException]

    def getFirstOrAddUser(implicit context: ContextSupport): TaskService[User] =
      (for {
        maybeUsers <- persistenceServices.fetchUsers
        user <- maybeUsers.headOption map (user => TaskService(Task(Either.right(user)))) getOrElse {
          persistenceServices.addUser(emptyUser)
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
      val update = User(id, None, None, None, None, None, None, None, UserProfile(None, None, None))
      (for {
        user <- findUserById(id)
        _ <- persistenceServices.updateUser(update)
        _ <- syncInstallation(user.apiKey, user.sessionToken, None)
      } yield ()).resolve[UserException]
    }

  override def getUser(implicit context: ContextSupport) =
    withActiveUser(findUserById(_).resolve[UserException])

  override def updateUserDevice(
    deviceName: String,
    deviceCloudId: String,
    deviceToken: Option[String] = None)(implicit context: ContextSupport) =
    withActiveUser { userId =>
      (for {
        user <- findUserById(userId)
        newUser = user.copy(
          deviceName = Option(deviceName),
          deviceCloudId = Option(deviceCloudId),
          deviceToken = deviceToken orElse user.deviceToken)
        _ <- persistenceServices.updateUser(user = newUser)
        _ <- syncInstallation(user.apiKey, user.sessionToken, newUser.deviceToken)
      } yield ()).resolve[UserException]
    }

  override def updateDeviceToken(deviceToken: String)(implicit context: ContextSupport) =
    withActiveUser { userId =>
      (for {
        user <- findUserById(userId)
        _ <- persistenceServices.updateUser(user.copy(id = userId, deviceToken = Option(deviceToken)))
        _ <- syncInstallation(user.apiKey, user.sessionToken, Option(deviceToken))
      } yield ()).resolve[UserException]
    }

  private[this] def withActiveUser[T](f: Int => TaskService[T])(implicit context: ContextSupport) =
    context.getActiveUserId map f getOrElse {
      TaskService(Task(Either.left(UserException(noActiveUserErrorMessage))))
    }

  private[this] def syncInstallation(
    maybeApiKey: Option[String],
    maybeSessionToken: Option[String],
    deviceToken: Option[String])(implicit context: ContextSupport): TaskService[Unit] =
    (maybeApiKey, maybeSessionToken) match {
      case (Some(apiKey), Some(sessionToken)) if deviceToken.nonEmpty =>
        (for {
          androidId <- persistenceServices.getAndroidId
          _ <- apiServices.updateInstallation(deviceToken)(RequestConfig(apiKey, sessionToken, androidId))
        } yield ()).resolve[UserException]
      case _ => TaskService.right(0)
    }

  private[this] def findUserById(id: Int): TaskService[User] =
    persistenceServices.findUserById(id).resolveOption(s"Can't find the user with id $id")

}
