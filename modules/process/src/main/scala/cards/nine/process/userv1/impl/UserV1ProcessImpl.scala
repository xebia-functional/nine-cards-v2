package cards.nine.process.userv1.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{UserV1, RequestConfigV1, User}
import cards.nine.process.userv1._
import cards.nine.process.userv1.models.{Device, UserV1Info}
import cards.nine.services.api._
import cards.nine.services.persistence.PersistenceServices
import cats.syntax.either._
import monix.eval.Task

class UserV1ProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends UserV1Process
  with ImplicitsUserV1Exception
  with UserV1Conversions {

  private[this] val noActiveUserErrorMsg = "No active user"
  private[this] val marketTokenErrorMsg = "Market token not available"
  private[this] val userNotLoggedMsg = "Can't authenticate user against backend V1"

  override def getUserInfo(deviceName: String, oauthScopes: Seq[String])(implicit context: ContextSupport) = {

    def loginV1(user: User, androidId: String): TaskService[LoginResponseV1] =
      (user.email, user.marketToken) match {
        case (Some(email), Some(marketToken)) =>
          val device = Device(
            name = deviceName,
            deviceId = androidId,
            secretToken = marketToken,
            permissions = oauthScopes)
          apiServices.loginV1(email, toGoogleDevice(device))
        case _ =>
          TaskService(Task(Either.left(UserV1Exception(marketTokenErrorMsg))))
      }

    def requestConfig(
      androidId: String,
      maybeSessionToken: Option[String],
      marketToken: Option[String]): TaskService[UserV1] =
      maybeSessionToken match {
        case Some(sessionToken) =>
          apiServices.getUserConfigV1()(RequestConfigV1(androidId, sessionToken, marketToken))
        case _ =>
          TaskService(Task(Either.left(UserV1Exception(userNotLoggedMsg))))
      }

    def loadUserConfig(userId: Int): TaskService[UserV1Info] =
      (for {
        user <- persistenceServices.findUserById(userId).resolveOption()
        androidId <- persistenceServices.getAndroidId
        loginResponse <- loginV1(user, androidId)
        userConfig <- requestConfig(androidId, loginResponse.sessionToken, user.marketToken)
      } yield toUserInfo(androidId, userConfig)) resolveLeft {
        case e: ApiServiceV1ConfigurationException => Left(UserV1ConfigurationException(e.getMessage, Some(e)))
        case e => Left(userConfigException(e))
      }

    context.getActiveUserId match {
      case Some(id) => loadUserConfig(id)
      case None =>  TaskService(Task(Either.left(UserV1Exception(noActiveUserErrorMsg))))
    }

  }

}
