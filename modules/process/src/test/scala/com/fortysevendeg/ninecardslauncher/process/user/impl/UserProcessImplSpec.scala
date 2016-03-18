package com.fortysevendeg.ninecardslauncher.process.user.impl

import java.io.File

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models.{DeviceType, GoogleDevice}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer, Result}

import scalaz.concurrent.Task

trait UserProcessSpecification
  extends Specification
  with Mockito {

  val apiServiceException = ApiServiceException("")

  val installationNotFoundException = InstallationNotFoundException("")

  val persistenceServiceException = PersistenceServiceException("")

  trait UserProcessScope
    extends Scope
    with UserProcessData {

    val appIconDir = mock[File]
    appIconDir.getPath returns fileFolder

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getAppIconsDir returns appIconDir
    contextSupport.getResources returns resources
    contextSupport.getActiveUserId returns Some(userDBId)
    contextSupport.setActiveUserId(userDBId)

    val mockApiServices = mock[ApiServices]

    mockApiServices.login(
      email = email,
      device = googleDevice) returns
      Service(Task(Result.answer(LoginResponse(statusCodeUser, user))))

    mockApiServices.createInstallation(any, any, any) returns
      Service(Task(Result.answer(InstallationResponse(statusCodeOk, initialInstallation))))

    mockApiServices.updateInstallation(
      id = installationId,
      deviceType = installation.deviceType,
      deviceToken = installation.deviceToken,
      userId = installation.userId) returns
      Service(Task(Result.answer(UpdateInstallationResponse(installationStatusCode))))

    val mockPersistenceServices = mock[PersistenceServices]

    mockPersistenceServices.findUserById(any[FindUserByIdRequest]) returns
      Service(Task(Result.answer(Some(persistenceUser))))

    mockPersistenceServices.updateUser(any[UpdateUserRequest]) returns
      Service(Task(Result.answer(userDBId)))

    mockPersistenceServices.addUser(any[AddUserRequest]) returns
      Service(Task(Result.answer(persistenceUser)))

    mockPersistenceServices.getAndroidId(contextSupport) returns
      Service(Task(Result.answer(deviceId)))

    val userProcess = new UserProcessImpl(mockApiServices, mockPersistenceServices)

  }

  trait ActiveUserNoneProcessScope {
    self: UserProcessScope =>

    contextSupport.getActiveUserId returns None

  }

  trait CreateInstallationErrorUserProcessScope {

    self: UserProcessScope =>

    mockApiServices.createInstallation(any, any, any) returns
      Service(Task(Errata(apiServiceException)))

  }

  trait InstallationDontExistsUserProcessScope {

    self: UserProcessScope =>

    mockPersistenceServices.findUserById(any[FindUserByIdRequest]) returns
      Service(Task(Result.answer(None)))

  }

    trait InstallationErrorUserProcessScope {

      self: UserProcessScope =>

      mockPersistenceServices.findUserById(any[FindUserByIdRequest]) returns
        Service(Task(Result.errata(persistenceServiceException)))

    }

  trait LoginErrorUserProcessScope {

    self: UserProcessScope =>

    mockApiServices.login(
      email = email,
      device = googleDevice) returns
      Service(Task(Result.errata(apiServiceException)))

  }

  trait SaveUserErrorUserProcessScope {

    self: UserProcessScope =>

    mockPersistenceServices.updateUser(any[UpdateUserRequest]) returns
      Service(Task(Errata(persistenceServiceException)))

  }

}

class UserProcessImplSpec
  extends UserProcessSpecification {

  "Sign In in UserProcess" should {

    "returns status code with initial installation calling to create installation in ApiServices" in
      new UserProcessScope {
        val result = userProcess.signIn(email, deviceName, secretToken, permissions)(contextSupport).run.run

        there was one(mockApiServices).login(anyString, any[GoogleDevice])
        there was one(mockApiServices).createInstallation(any[Option[DeviceType]], any[Option[String]], any[Option[String]])
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userDBId))
        there was one(mockPersistenceServices).updateUser(any[UpdateUserRequest])

        result must beLike {
          case Answer(signInResponse) =>
            signInResponse.statusCode shouldEqual statusCodeUser
        }
      }

      "returns status code with full installation calling to update installation in ApiServices" in
        new UserProcessScope {
          val result = userProcess.signIn(email, deviceName, secretToken, permissions)(contextSupport).run.run

          there was one(mockApiServices).login(anyString, any[GoogleDevice])
          there was one(mockApiServices).updateInstallation(any[String], any[Option[DeviceType]], any[Option[String]], any[Option[String]])
          there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userDBId))
          there was one(mockPersistenceServices).updateUser(any[UpdateUserRequest])

          result must beLike {
            case Answer(signInResponse) =>
              signInResponse.statusCode shouldEqual statusCodeUser
          }
        }

      "returns a UserException if login in ApiService returns a exception and shouldn't sync installation" in
        new UserProcessScope with LoginErrorUserProcessScope {
          val result = userProcess.signIn(email, deviceName, secretToken, permissions)(contextSupport).run.run
          there was one(mockApiServices).login(anyString, any[GoogleDevice])
          there was exactly(0)(mockApiServices).createInstallation(any[Option[DeviceType]], any[Option[String]], any[Option[String]])
          there was exactly(0)(mockApiServices).updateInstallation(any[String], any[Option[DeviceType]], any[Option[String]], any[Option[String]])

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beAnInstanceOf[UserException]
            }
          }
        }

      "returns a UserException if save user fails and shouldn't sync installation" in
        new UserProcessScope with SaveUserErrorUserProcessScope {
          val result = userProcess.signIn(email, deviceName, secretToken, permissions)(contextSupport).run.run
          there was one(mockApiServices).login(anyString, any[GoogleDevice])
          there was one(mockPersistenceServices).updateUser(any[UpdateUserRequest])
          there was exactly(0)(mockApiServices).createInstallation(any[Option[DeviceType]], any[Option[String]], any[Option[String]])
          there was exactly(0)(mockApiServices).updateInstallation(any[String], any[Option[DeviceType]], any[Option[String]], any[Option[String]])

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beAnInstanceOf[UserException]
            }
          }
        }

  }

  "Register in UserProcess" should {

    "register user when the user don't exists in local" in
      new UserProcessScope with InstallationDontExistsUserProcessScope {
        val result = userProcess.register(contextSupport).run.run
        there was one(contextSupport).setActiveUserId(userDBId)

        result must beLike {
          case Answer(r) => r shouldEqual (())
        }
      }


    "return UserException when the user return Exception in local" in
      new UserProcessScope with InstallationErrorUserProcessScope {
        val result = userProcess.register(contextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[UserException]
          }
        }
      }

    "Don't store active user id when the user exists in local" in new UserProcessScope {
      val result = userProcess.register(contextSupport).run.run
      there was exactly(0)(contextSupport).setActiveUserId(userDBId)

      result must beLike {
        case Answer(r) => r shouldEqual (())
      }
    }

  }

  "Unregister in UserProcess" should {

    "unregister user and call to create installation in ApiService" in
      new UserProcessScope {
        val result = userProcess.unregister(contextSupport).run.run
        there was one(mockApiServices).createInstallation(any[Option[DeviceType]], any[Option[String]], any[Option[String]])
        there was one(mockPersistenceServices).updateUser(any[UpdateUserRequest])

        result must beLike {
          case Answer(r) => r shouldEqual (())
        }
      }

    "returns a UserException if sync fails" in
      new UserProcessScope with CreateInstallationErrorUserProcessScope {
        val result = userProcess.unregister(contextSupport).run.run
        there was one(mockApiServices).createInstallation(any[Option[DeviceType]], any[Option[String]], any[Option[String]])
        there was exactly(0)(mockPersistenceServices).updateUser(any[UpdateUserRequest])

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[UserException]
          }
        }
      }

  }

}
