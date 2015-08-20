package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.services.api.{ImplicitsApiServiceExceptions, ApiServiceException, RequestConfig}
import com.fortysevendeg.ninecardslauncher.services.api.models.User
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import rapture.core.{Answer, Result}
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

class ApiUtils(persistenceServices: PersistenceServices)
  extends ImplicitsApiServiceExceptions {

  def getRequestConfig(implicit context: ContextSupport): ServiceDef2[RequestConfig, ApiServiceException] = (for {
    token <- getSessionToken
    androidId <- persistenceServices.getAndroidId
  } yield RequestConfig(deviceId = androidId, token = token)).resolve[ApiServiceException]

  private[this] def getSessionToken(implicit context: ContextSupport): ServiceDef2[String, ApiServiceException] = Service {
    persistenceServices.getUser.run map {
      case Answer(User(_, Some(sessionToken), _, _)) => Result.answer[String, ApiServiceException](sessionToken) //TODO refactor to named params once available in Scala
      case _ => Result.errata(ApiServiceException("Session token doesn't exists"))
    }
  }


}
