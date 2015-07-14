package com.fortysevendeg.ninecardslauncher.process.user.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.user.{SignInResponse, UserProcess}
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, GoogleDevice}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices

import scalaz.concurrent.Task
import scalaz._
import Scalaz._
import EitherT._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._

class UserProcessImpl(
  apiServices: ApiServices,
  persistenceServices: PersistenceServices
  )
  extends UserProcess {

  val DeviceType = "ANDROID"

  private val BasicInstallation = Installation(id = None, deviceType = Some(DeviceType), deviceToken = None, userId = None)

  override def signIn(email: String, device: GoogleDevice)(implicit context: ContextSupport): Task[NineCardsException \/ SignInResponse] =
    for {
      loginResponse <- apiServices.login(email, device) ▹ eitherT
      _ <- persistenceServices.saveUser(loginResponse.user) ▹ eitherT
      installation <- persistenceServices.getInstallation ▹ eitherT
      _ <- syncInstallation(installation) ▹ eitherT
    } yield SignInResponse(loginResponse.statusCode)

  override def register(implicit context: ContextSupport): Task[NineCardsException \/ Unit] = persistenceServices.getInstallation map {
    case \/-(r) => \/-(r)
    case -\/(ex) => toEnsureAttemptRun(persistenceServices.saveInstallation(BasicInstallation))
  }

  override def unregister(implicit context: ContextSupport): Task[NineCardsException \/ Unit] =
    for {
      save <- persistenceServices.saveInstallation(BasicInstallation) ▹ eitherT
      _ <- syncInstallation(BasicInstallation) ▹ eitherT
      _ <- persistenceServices.resetUser ▹ eitherT
    } yield save

  private[this] def syncInstallation(installation: Installation)(implicit context: ContextSupport): Task[NineCardsException \/ Int] =
    installation.id map {
      id =>
        apiServices.updateInstallation(Option(id), installation.deviceType, installation.deviceToken, installation.userId) map {
          case \/-(r) => \/-(r.statusCode)
          case -\/(ex) => -\/(NineCardsException(msg = "Installation not updated", cause = ex.some))
        }
    } getOrElse {
      for {
        installationResponse <- apiServices.createInstallation(None, installation.deviceType, installation.deviceToken, installation.userId) ▹ eitherT
        saved <- persistenceServices.saveInstallation(installationResponse.installation) ▹ eitherT
      } yield installationResponse.statusCode
    }
}
