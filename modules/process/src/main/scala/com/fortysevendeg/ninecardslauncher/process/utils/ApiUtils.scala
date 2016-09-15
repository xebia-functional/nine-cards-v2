package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ImplicitsApiServiceExceptions, RequestConfig}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServices}
import monix.eval.Task
import cats.syntax.either._


class ApiUtils(persistenceServices: PersistenceServices)
  extends ImplicitsApiServiceExceptions {

  def getRequestConfig(implicit context: ContextSupport): TaskService[RequestConfig] = {

    def loadUser(userId: Int): TaskService[RequestConfig] =
      (for {
        user <- persistenceServices.findUserById(FindUserByIdRequest(userId)).resolveOption()
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
