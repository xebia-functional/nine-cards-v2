package com.fortysevendeg.ninecardslauncher.process.utils

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ImplicitsApiServiceExceptions, RequestConfig}
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, FindUserByIdRequest, PersistenceServices}

import scalaz.concurrent.Task

class ApiUtils(persistenceServices: PersistenceServices)
  extends ImplicitsApiServiceExceptions {

  def getRequestConfig(implicit context: ContextSupport): CatsService[RequestConfig] = {

    def getUser = context.getActiveUserId match {
      case Some(id) => persistenceServices.findUserById(FindUserByIdRequest(id)).resolveOption()
      case _ => CatsService(Task(Xor.left(PersistenceServiceException("Missing user id"))))
    }

    def getSessionToken(sessionToken: Option[String]) = sessionToken match {
      case Some(st) => CatsService(Task(Xor.right(st)))
      case _ => CatsService(Task(Xor.left(PersistenceServiceException("No session token available"))))
    }

    (for {
      user <- getUser
      sessionToken <- getSessionToken(user.sessionToken)
      androidId <- persistenceServices.getAndroidId
    } yield RequestConfig(deviceId = androidId, token = sessionToken, marketToken = user.marketToken)).resolve[ApiServiceException]
  }

}
