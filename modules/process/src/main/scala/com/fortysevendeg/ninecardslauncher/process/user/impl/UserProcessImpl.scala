package com.fortysevendeg.ninecardslauncher.process.user.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.user._
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.api.models.Installation
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceExceptions.PersistenceServiceException
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

class UserProcessImpl(
  apiServices: ApiServices,
  persistenceServices: PersistenceServices)
  extends UserProcess
  with ImplicitsUserException
  with Conversions {

  private[this] val deviceType = "ANDROID"

  private[this] val basicInstallation = Installation(id = None, deviceType = Some(deviceType), deviceToken = None, userId = None)

  override def signIn(email: String, device: Device)(implicit context: ContextSupport) = (for {
    loginResponse <- apiServices.login(email, toGoogleDevice(device))
    _ <- persistenceServices.saveUser(loginResponse.user)
    installation <- persistenceServices.getInstallation
    _ <- syncInstallation(installation)
  } yield SignInResponse(loginResponse.statusCode)).resolve[UserException]

  override def register(implicit context: ContextSupport) = (for {
    exists <- persistenceServices.existsInstallation
    _ <- if (!exists) persistenceServices.saveInstallation(basicInstallation)
    else Service {
      Task(Result.answer[Unit, PersistenceServiceException](()))
    }
  } yield (())).resolve[UserException]

  override def unregister(implicit context: ContextSupport) = (for {
    _ <- syncInstallation(basicInstallation)
    _ <- persistenceServices.resetUser
  } yield (())).resolve[UserException]

  private[this] def syncInstallation(installation: Installation)(implicit context: ContextSupport): ServiceDef2[Int, UserException] =
    installation.id map {
      id =>
        Service {
          apiServices.updateInstallation(Option(id), installation.deviceType, installation.deviceToken, installation.userId).run map {
            case Answer(r) => Result.answer[Int, UserException](r.statusCode)
            case Errata(ex) => Result.errata[Int, UserException](UserException(message = "Installation not updated"))
          }
        }
    } getOrElse {
      (for {
        installationResponse <- apiServices.createInstallation(None, installation.deviceType, installation.deviceToken, installation.userId)
        saved <- persistenceServices.saveInstallation(installationResponse.installation)
      } yield installationResponse.statusCode).resolve[UserException]
    }
}
