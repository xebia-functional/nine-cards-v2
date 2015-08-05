package com.fortysevendeg.ninecardslauncher.process.user.impl

import java.io.File

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models.GoogleDevice
import com.fortysevendeg.ninecardslauncher.services.persistence.{InstallationNotFoundException, PersistenceServiceException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

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

    val mockApiServices = mock[ApiServices]

    mockApiServices.login(
      email = email,
      device = googleDevice) returns
      Service(Task(Result.answer(LoginResponse(statusCodeUser, user))))

    mockApiServices.createInstallation(
      id = initialInstallation.id,
      deviceType = initialInstallation.deviceType,
      deviceToken = initialInstallation.deviceToken,
      userId = initialInstallation.userId) returns
      Service(Task(Result.answer(InstallationResponse(statusCodeOk, initialInstallation))))

    mockApiServices.updateInstallation(
      id = installation.id,
      deviceType = installation.deviceType,
      deviceToken = installation.deviceToken,
      userId = installation.userId) returns
      Service(Task(Result.answer(UpdateInstallationResponse(installationStatusCode))))

    val mockPersistenceServices = mock[PersistenceServices]

    mockPersistenceServices.saveUser(user)(contextSupport) returns
      Service(Task(Result.answer(())))

    mockPersistenceServices.saveInstallation(initialInstallation)(contextSupport) returns
      Service(Task(Result.answer(())))

    mockPersistenceServices.getInstallation(contextSupport) returns
      Service(Task(Result.answer(installation)))

    mockPersistenceServices.existsInstallation(contextSupport) returns
      Service(Task(Result.answer(true)))

    mockPersistenceServices.resetUser(contextSupport) returns
      Service(Task(Result.answer(true)))

    val userProcess = new UserProcessImpl(mockApiServices, mockPersistenceServices)

  }

  trait InitialUserProcessScope {

    self: UserProcessScope =>

    mockPersistenceServices.existsInstallation(contextSupport) returns
      Service(Task(Result.answer(false)))

  }

  trait CreateInstallationErrorUserProcessScope {

    self: UserProcessScope =>

    mockApiServices.createInstallation(
      id = initialInstallation.id,
      deviceType = initialInstallation.deviceType,
      deviceToken = initialInstallation.deviceToken,
      userId = initialInstallation.userId) returns
      Service(Task(Errata(apiServiceException)))

  }

  trait InstallationErrorUserProcessScope {

    self: UserProcessScope =>

    mockPersistenceServices.getInstallation(contextSupport) returns
      Service(Task(Errata(installationNotFoundException)))

  }

  trait LoginErrorUserProcessScope {

    self: UserProcessScope =>

    mockApiServices.login(
      email = email,
      device = googleDevice) returns
      Service(Task(Errata(apiServiceException)))

  }

  trait SaveUserErrorUserProcessScope {

    self: UserProcessScope =>

    mockPersistenceServices.saveUser(user)(contextSupport) returns
      Service(Task(Errata(persistenceServiceException)))

  }

}

class UserProcessImplSpec
  extends UserProcessSpecification {

  "Sign In in UserProcess" should {

    "returns status code with initial installation calling to create installation in ApiServices" in
      new UserProcessScope with InitialUserProcessScope {
        val result = userProcess.signIn(email, device)(contextSupport).run.run

        there was one(mockApiServices).login(anyString, any[GoogleDevice])
        there was one(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
        there was one(mockPersistenceServices).saveUser(user)(contextSupport)
        there was one(mockPersistenceServices).getInstallation(contextSupport)

        result must beLike {
          case Answer(signInResponse) =>
            signInResponse.statusCode shouldEqual statusCodeUser
        }
      }

    "returns status code with full installation calling to update installation in ApiServices" in
      new UserProcessScope {
        val result = userProcess.signIn(email, device)(contextSupport).run.run

        there was one(mockApiServices).login(anyString, any[GoogleDevice])
        there was one(mockApiServices).updateInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
        there was one(mockPersistenceServices).saveUser(user)(contextSupport)
        there was one(mockPersistenceServices).getInstallation(contextSupport)

        result must beLike {
          case Answer(signInResponse) =>
            signInResponse.statusCode shouldEqual statusCodeUser
        }
      }

    "returns a UserException if login in ApiService returns a exception and shouldn't sync installation" in
      new UserProcessScope with LoginErrorUserProcessScope {
        val result = userProcess.signIn(email, device)(contextSupport).run.run
        there was one(mockApiServices).login(anyString, any[GoogleDevice])
        there was exactly(0)(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
        there was exactly(0)(mockApiServices).updateInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[UserException]
          }
        }
      }

    "returns a UserException if save user fails and shouldn't sync installation" in
      new UserProcessScope with SaveUserErrorUserProcessScope {
        val result = userProcess.signIn(email, device)(contextSupport).run.run
        there was one(mockApiServices).login(anyString, any[GoogleDevice])
        there was one(mockPersistenceServices).saveUser(user)(contextSupport)
        there was exactly(0)(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
        there was exactly(0)(mockApiServices).updateInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[UserException]
          }
        }
      }

  }

  "Register in UserProcess" should {

    "save installation when the installation don't exists in local" in
      new UserProcessScope with InstallationErrorUserProcessScope {
        val result = userProcess.register(contextSupport).run.run
        there was one(mockPersistenceServices).saveInstallation(initialInstallation)(contextSupport)

        result must beLike {
          case Answer(r) => r shouldEqual (())
        }
      }

    "don't save installation when the installation exists in local" in new UserProcessScope {
      val result = userProcess.register(contextSupport).run.run
      there was exactly(0)(mockPersistenceServices).saveInstallation(initialInstallation)(contextSupport)

      result must beLike {
        case Answer(r) => r shouldEqual (())
      }
    }

  }

  "Unregister in UserProcess" should {

    "save initial installation and call to create installation in ApiService" in
      new UserProcessScope {
        val result = userProcess.unregister(contextSupport).run.run
        there was one(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
        there was one(mockPersistenceServices).saveInstallation(initialInstallation)(contextSupport)
        there was one(mockPersistenceServices).resetUser(contextSupport)

        result must beLike {
          case Answer(r) => r shouldEqual (())
        }
      }

    "returns a UserException if sync fails" in
      new UserProcessScope with CreateInstallationErrorUserProcessScope {
        val result = userProcess.unregister(contextSupport).run.run
        there was one(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
        there was exactly(0)(mockPersistenceServices).saveInstallation(initialInstallation)(contextSupport)
        there was exactly(0)(mockPersistenceServices).resetUser(contextSupport)

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[UserException]
          }
        }
      }

  }

}
