package com.fortysevendeg.ninecardslauncher.modules.user.impl

import java.io.File

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.ContextWrapperProvider
import com.fortysevendeg.ninecardslauncher.models.{Installation, User}
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.user.{SignInResponse, UserServices, UserServicesComponent}
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._
import com.fortysevendeg.ninecardslauncher.utils.FileUtils
import com.fortysevendeg.ninecardslauncher.commons.Service

import scala.util.{Try, Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

trait UserServicesComponentImpl
  extends UserServicesComponent {

  self: ContextWrapperProvider with ApiServicesComponent =>

  lazy val userServices = new UserServicesImpl

  class UserServicesImpl
    extends UserServices
    with Conversions
    with FileUtils {

    val DeviceType = "ANDROID"
    val FilenameUser = "__user_entity__"
    val FilenameInstallation = "__installation_entity__"

    private val BasicInstallation = Installation(id = None, deviceType = Some(DeviceType), deviceToken = None, userId = None)

    private var synchronizingChangesInstallation: Boolean = false
    private var pendingSynchronizedInstallation: Boolean = false

    override def register(): Unit =
      if (!getFileInstallation.exists()) {
        saveInstallation(BasicInstallation)
      }


    override def unregister(): Unit = {
      saveInstallation(BasicInstallation)
      synchronizeInstallation()
      val fileUser = getFileUser
      if (fileUser.exists()) fileUser.delete()
    }

    // TODO We have to store the information in Database. Serialization it's temporarily
    override def getUser: Option[User] =
      loadFile[User](getFileUser) match {
        case Success(us) => Some(us)
        case Failure(ex) => None
      }

    override def getInstallation: Option[Installation] =
      loadFile[Installation](getFileInstallation) match {
        case Success(inst) => Some(inst)
        case Failure(ex) => None
      }

    override def signIn: Service[LoginRequest, SignInResponse] =
      request => {
        apiServices.login(request) map {
          response =>
            response.user map {
              user =>
                saveUser(user)
                getInstallation map {
                  i =>
                    saveInstallation(i.copy(userId = user.id))
                    synchronizeInstallation()
                }
                SignInResponse(response.statusCode, true)
            } getOrElse SignInResponse(response.statusCode, false)
        }
      }

    override def getAndroidId: Option[String] = Try {
      val cursor = Option(contextProvider.application.getContentResolver.query(Uri.parse(ContentGServices), null, null, Array(AndroidId), null))
      cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
    } match {
      case Success(id) => id
      case Failure(ex) => None
    }

    private def saveInstallation(installation: Installation) = writeFile[Installation](getFileInstallation, installation)

    private def saveUser(user: User) = writeFile[User](getFileUser, user)

    private def getFileInstallation = new File(contextProvider.application.getFilesDir, FilenameInstallation)

    private def getFileUser = new File(contextProvider.application.getFilesDir, FilenameUser)

    private def synchronizeInstallation(): Unit =
      synchronizingChangesInstallation match {
        case true => pendingSynchronizedInstallation = true
        case _ =>
          synchronizingChangesInstallation = true
          getInstallation map {
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
                    response.installation map saveInstallation
                    if (pendingSynchronizedInstallation) {
                      pendingSynchronizedInstallation = false
                      synchronizeInstallation()
                    }
                }
              }
          }
      }

  }

}

