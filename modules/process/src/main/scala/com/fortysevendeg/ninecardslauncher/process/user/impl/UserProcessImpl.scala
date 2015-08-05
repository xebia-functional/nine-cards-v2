package com.fortysevendeg.ninecardslauncher.process.user.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.process.user.{Conversions, SignInResponse, UserProcess}
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.api.models.Installation
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices

import scalaz.concurrent.Task
import scalaz._
import Scalaz._
import EitherT._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

class UserProcessImpl(
  apiServices: ApiServices,
  persistenceServices: PersistenceServices)
  extends UserProcess
  with Conversions {

  private[this] val deviceType = "ANDROID"

  private[this] val basicInstallation = Installation(id = None, deviceType = Some(deviceType), deviceToken = None, userId = None)

  override def signIn(email: String, device: Device)(implicit context: ContextSupport): Task[NineCardsException \/ SignInResponse] =
    for {
      loginResponse <- apiServices.login(email, toGoogleDevice(device)) ▹ eitherT
      _ <- persistenceServices.saveUser(loginResponse.user) ▹ eitherT
      installation <- persistenceServices.getInstallation ▹ eitherT
      _ <- syncInstallation(installation) ▹ eitherT
    } yield SignInResponse(loginResponse.statusCode)

  override def register(implicit context: ContextSupport): Task[NineCardsException \/ Unit] =
    for {
      exists <- persistenceServices.existsInstallation ▹ eitherT
      _ <- (if (!exists) persistenceServices.saveInstallation(basicInstallation) else Task{\/-(())}) ▹ eitherT
    } yield (())

  override def unregister(implicit context: ContextSupport): Task[NineCardsException \/ Unit] =
    for {
      _ <- syncInstallation(basicInstallation) ▹ eitherT
      _ <- persistenceServices.resetUser ▹ eitherT
    } yield (())

  private[this] def syncInstallation(installation: Installation)(implicit context: ContextSupport): Task[NineCardsException \/ Int] =
    installation.id map {
      id =>
        apiServices.updateInstallation(Option(id), installation.deviceType, installation.deviceToken, installation.userId) map {
          case \/-(r) => \/-(r.statusCode)
          case -\/(ex) => -\/(NineCardsException(msg = "Installation not updated", cause = ex.some))
        }
    } getOrElse {
      for {
        installationResponse <- apiServices.createInstallation(installation.deviceType, installation.deviceToken, installation.userId) ▹ eitherT
        saved <- persistenceServices.saveInstallation(installationResponse.installation) ▹ eitherT
      } yield installationResponse.statusCode
    }
}
