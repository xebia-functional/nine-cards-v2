package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.api.RequestConfig
import com.fortysevendeg.ninecardslauncher.services.api.models.User
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import rapture.core.Result

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import EitherT._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

class ApiUtils(persistenceServices: PersistenceServices) {

  def getRequestConfig(implicit context: ContextSupport): Task[NineCardsException \/ RequestConfig] = {
    for {
      token <- getSessionToken ▹ eitherT
      androidId <- persistenceServices.getAndroidId ▹ eitherT
    } yield RequestConfig(deviceId = androidId, token = token)
  }

  private[this] def getSessionToken(implicit context: ContextSupport): Task[NineCardsException \/ String] =
    persistenceServices.getUser map {
      case \/-(User(_, Some(sessionToken), _, _)) => \/-(sessionToken) //TODO refactor to named params once available in Scala
      case -\/(ex) => -\/(ex)
      case _ => -\/(NineCardsException("Session token doesn't exists"))
    }

  def getRequestConfigServiceF2(implicit context: ContextSupport): ServiceDef2[RequestConfig, NineCardsException] = Service {
    getRequestConfig map {
      case -\/(ex) => Result.errata(NineCardsException(msg = "Android Id not found", cause = ex.some))
      case \/-(r) => Result.answer(r)
    }
  }

}
