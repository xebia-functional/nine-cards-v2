package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.services.api.{ImplicitsApiServiceExceptions, ApiServiceException, RequestConfig}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServices}
import rapture.core.{Answer, Result}
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

import scalaz.concurrent.Task

class ApiUtils(persistenceServices: PersistenceServices)
  extends ImplicitsApiServiceExceptions {

  def getRequestConfig(implicit context: ContextSupport): ServiceDef2[RequestConfig, ApiServiceException] = {
    (for {
      (token, androidToken) <- context.getActiveUserId map getTokens getOrElse Service(Task(Result.errata(ApiServiceException("Missing user id"))))
      androidId <- persistenceServices.getAndroidId
    } yield RequestConfig(deviceId = androidId, token = token, androidToken = androidToken)).resolve[ApiServiceException]
  }

  private[this] def getTokens(id: Int)(implicit context: ContextSupport): ServiceDef2[(String, Option[String]), ApiServiceException] = Service {
    persistenceServices.findUserById(FindUserByIdRequest(id)).run map {
      case Answer(Some(User(_, _, _, Some(sessionToken), _, _, androidToken, _, _, _))) => Result.answer[(String, Option[String]), ApiServiceException]((sessionToken, androidToken)) //TODO refactor to named params once available in Scala
      case _ => Result.errata(ApiServiceException("Session token doesn't exists"))
    }
  }
}
