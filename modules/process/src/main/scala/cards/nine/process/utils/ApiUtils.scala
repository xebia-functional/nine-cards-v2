package cards.nine.process.utils

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{RequestConfig, User}
import cards.nine.services.api.{ApiServiceException, ImplicitsApiServiceExceptions}
import cards.nine.services.persistence.PersistenceServices
import cats.syntax.either._
import monix.eval.Task

class ApiUtils(persistenceServices: PersistenceServices)
  extends ImplicitsApiServiceExceptions {

  def getRequestConfig(implicit context: ContextSupport): TaskService[RequestConfig] = {

    def loadUser(userId: Int): TaskService[RequestConfig] =
      (for {
        user <- persistenceServices.findUserById(userId).resolveOption()
        keys <- loadTokens(user)
        (apiKey, sessionToken) = keys
        androidId <- persistenceServices.getAndroidId
      } yield RequestConfig(apiKey, sessionToken, androidId, user.marketToken)).resolve[ApiServiceException]

    def loadTokens(user: User): TaskService[(String, String)] =
      (user.apiKey, user.sessionToken) match {
        case (Some(apiKey), Some(sessionToken)) => TaskService(Task(Either.right(apiKey, sessionToken)))
        case _ => TaskService(Task(Either.left(ApiServiceException("Session token doesn't exists"))))
      }

    context.getActiveUserId match {
      case Some(id) => loadUser(id)
      case None => TaskService(Task(Either.left(ApiServiceException("Missing user id"))))
    }

  }

}
