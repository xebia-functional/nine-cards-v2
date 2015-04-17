package com.fortysevendeg.ninecardslauncher.modules.user.impl

import java.io.File

import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.user.{UserServices, UserServicesComponent}
import com.fortysevendeg.ninecardslauncher.utils.FileUtils

import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

trait UserServicesComponentImpl
  extends UserServicesComponent {

  self: AppContextProvider with ApiServicesComponent =>

  lazy val userServices = new UserServicesImpl

  class UserServicesImpl
    extends UserServices
    with FileUtils {

    val BasicInstallation = Some(Installation(id = None, deviceType = Some(DeviceType), deviceToken = None, userId = None))

    var installation: Option[Installation] = None

    var user: Option[User] = None

    val DeviceType = "ANDROID"
    val FilenameUser = "__user_entity__"
    val FilenameInstallation = "__installation_entity__"

    override def register(): Unit = {
      loadFile[Installation](getFileInstallation) match {
        case Success(inst) => installation = Some(inst)
        case Failure(ex) => installation = BasicInstallation
      }

      loadFile[User](getFileUser) match {
        case Success(us) => user = Some(us)
      }
    }

    override def unregister(): Unit = {
      installation = BasicInstallation
      saveInstallation
      // TODO synchronize installation in server here
      val fileUser = getFileUser
      if (fileUser.exists()) fileUser.delete()
      user = None
    }

    override def login(request: LoginRequest): Unit = {
      val loginResponse = user map {
        u =>
          apiServices.linkGoogleAccount(
            LinkGoogleAccountRequest(
              deviceId = request.device.devideId,
              token = request.device.secretToken,
              email = request.email,
              devices = List(request.device)
            ))
      } getOrElse {
        apiServices.login(request)
      }
      loginResponse map {
        response =>
          response.user map {
            u =>
              user = Some(u)
              saveUser
              installation map {
                i =>
                  installation = Some(i.copy(userId = u.id))
                  saveInstallation
                // TODO synchronize installation in server here
              }
          }
      }
    }

    private def saveInstallation = installation map (writeFile[Installation](getFileInstallation, _))

    private def saveUser = user map (writeFile[User](getFileUser, _))

    private def getFileInstallation = new File(appContextProvider.get.getFilesDir, FilenameInstallation)

    private def getFileUser = new File(appContextProvider.get.getFilesDir, FilenameUser)

  }

}

