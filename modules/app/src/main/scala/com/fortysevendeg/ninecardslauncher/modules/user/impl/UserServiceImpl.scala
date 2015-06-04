package com.fortysevendeg.ninecardslauncher.modules.user.impl

import com.fortysevendeg.ninecardslauncher.models.{GoogleDevice, Installation}
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.repository.RepositoryServices
import com.fortysevendeg.ninecardslauncher.modules.user._
import com.fortysevendeg.ninecardslauncher.utils.FileUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserServiceImpl(
    apiServices: ApiServices,
    repositoryServices: RepositoryServices)
    extends UserService
    with Conversions
    with FileUtils {

  val DeviceType = "ANDROID"

  private val BasicInstallation = Installation(id = None, deviceType = Some(DeviceType), deviceToken = None, userId = None)

  private var synchronizingChangesInstallation: Boolean = false
  private var pendingSynchronizedInstallation: Boolean = false

  override def register(): Unit = repositoryServices.saveInstallation(BasicInstallation)

  override def unregister(): Unit = {
    repositoryServices.saveInstallation(BasicInstallation)
    synchronizeInstallation()
    repositoryServices.resetUser
  }

  override def signIn(email: String, device: GoogleDevice): Future[Int] =
    apiServices.login(LoginRequest(email, device)) map {
      response =>
        response.user map {
          user =>
            repositoryServices.saveUser(user)
            repositoryServices.getInstallation map {
              i =>
                repositoryServices.saveInstallation(i.copy(userId = user.id))
                synchronizeInstallation
            }
            response.statusCode
        } getOrElse (throw UserNotFoundException())
    } recover {
      case _ => throw UserUnexpectedException()
    }

  private def synchronizeInstallation(): Unit =
    synchronizingChangesInstallation match {
      case true => pendingSynchronizedInstallation = true
      case _ =>
        synchronizingChangesInstallation = true
        repositoryServices.getInstallation map {
          inst =>
            inst.id map {
              id =>
                apiServices.updateInstallation(toInstallationRequest(inst)) map {
                  response =>
                    synchronizingChangesInstallation = false
                    if (pendingSynchronizedInstallation) {
                      pendingSynchronizedInstallation = false
                      synchronizeInstallation()
                    }
                }
            } getOrElse {
              apiServices.createInstallation(toInstallationRequest(inst)) map {
                response =>
                  synchronizingChangesInstallation = false
                  response.installation map repositoryServices.saveInstallation
                  if (pendingSynchronizedInstallation) {
                    pendingSynchronizedInstallation = false
                    synchronizeInstallation()
                  }
              }
            }
        }
    }

}



