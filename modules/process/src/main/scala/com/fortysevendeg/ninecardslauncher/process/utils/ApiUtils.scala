package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ImplicitsApiServiceExceptions, RequestConfig, RequestConfigV1}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServices}
import rapture.core.{Answer, Errata, Result}
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

import scalaz.concurrent.Task

class ApiUtils(persistenceServices: PersistenceServices)
  extends ImplicitsApiServiceExceptions {

  /**
    * @deprecated the sessionToken is not stored anymore here
    */
  def getRequestConfigV1(implicit context: ContextSupport): ServiceDef2[RequestConfigV1, ApiServiceException] = {
    (for {
      (token, marketToken) <- context.getActiveUserId map getTokens getOrElse Service(Task(Result.errata(ApiServiceException("Missing user id"))))
      androidId <- persistenceServices.getAndroidId
    } yield RequestConfigV1(deviceId = androidId, token = token, marketToken = marketToken)).resolve[ApiServiceException]
  }

  private[this] def getTokens(id: Int)(implicit context: ContextSupport): ServiceDef2[(String, Option[String]), ApiServiceException] = Service {
    persistenceServices.findUserById(FindUserByIdRequest(id)).run map {
      case Answer(Some(User(_, _, _, Some(sessionToken), _, marketToken, _, _, _, _, _))) =>
        //TODO refactor to named params once available in Scala
        Result.answer[(String, Option[String]), ApiServiceException]((sessionToken, marketToken))
      case _ => Result.errata(ApiServiceException("Session token doesn't exists"))
    }
  }

  def getRequestConfig(implicit context: ContextSupport): ServiceDef2[RequestConfig, ApiServiceException] = {

    def loadUser(userId: Int) =
      (for {
        Some(user) <- persistenceServices.findUserById(FindUserByIdRequest(userId))
        androidId <- persistenceServices.getAndroidId
        (apiKey, sessionToken) <- loadTokens(user)
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
