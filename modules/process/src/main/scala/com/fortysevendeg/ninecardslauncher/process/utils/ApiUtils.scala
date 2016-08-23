package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ImplicitsApiServiceExceptions, RequestConfig}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServices}
import rapture.core.{Answer, Errata, Result}
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

import scalaz.concurrent.Task

class ApiUtils(persistenceServices: PersistenceServices)
  extends ImplicitsApiServiceExceptions {

  def getRequestConfig(implicit context: ContextSupport): ServiceDef2[RequestConfig, ApiServiceException] = {

    def loadUser(userId: Int) =
      (for {
        Some(user) <- persistenceServices.findUserById(FindUserByIdRequest(userId))
        (apiKey, sessionToken) <- loadTokens(user)
        androidId <- persistenceServices.getAndroidId
      } yield RequestConfig(apiKey, sessionToken, androidId, user.marketToken)).resolve[ApiServiceException]

    def loadTokens(user: User): ServiceDef2[(String, String), ApiServiceException] =
      (user.apiKey, user.sessionToken) match {
        case (Some(apiKey), Some(sessionToken)) => Service(Task(Answer((apiKey, sessionToken))))
        case _ => Service(Task(Errata(ApiServiceException("Session token doesn't exists"))))
      }

    context.getActiveUserId match {
      case Some(id) => loadUser(id)
      case None =>  Service(Task(Result.errata(ApiServiceException("Missing user id"))))
    }

  }

}
