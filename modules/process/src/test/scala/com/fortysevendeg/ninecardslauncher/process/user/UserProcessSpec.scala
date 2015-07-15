package com.fortysevendeg.ninecardslauncher.process.user

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.user.impl.UserProcessImpl
import com.fortysevendeg.ninecardslauncher.services.api.models.{User, GoogleDevice}
import com.fortysevendeg.ninecardslauncher.services.api.{UpdateInstallationResponse, InstallationResponse, ApiServices, LoginResponse}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task


trait UserProcessSpecification
  extends Specification
  with Mockito
  with MockUserProcess {

  val exception = NineCardsException("")

  trait UserProcessScope
    extends Scope{

    val mockApiServices = mock[ApiServices]

    mockApiServices.login(
      email = email,
      device = googleDevice) returns
      Task(\/-(LoginResponse(userStatusCode, user)))

    mockApiServices.createInstallation(
      id = initialInstallation.id,
      deviceType = initialInstallation.deviceType,
      deviceToken = initialInstallation.deviceToken,
      userId = initialInstallation.userId) returns
      Task(\/-(InstallationResponse(200, initialInstallation)))

    mockApiServices.updateInstallation(
      id = installation.id,
      deviceType = installation.deviceType,
      deviceToken = installation.deviceToken,
      userId = installation.userId) returns
      Task(\/-(UpdateInstallationResponse(installationStatusCode)))

    val mockPersistenceServices = mock[PersistenceServices]

    mockPersistenceServices.saveUser(user)(contextSupport) returns
      Task(\/-(()))

    mockPersistenceServices.saveInstallation(initialInstallation)(contextSupport) returns
      Task(\/-(()))

    mockPersistenceServices.getInstallation(contextSupport) returns
      Task(\/-(installation))

    mockPersistenceServices.resetUser(contextSupport) returns
      Task(\/-(true))

    val userProcess = new UserProcessImpl(mockApiServices, mockPersistenceServices)

  }

  trait InitialUserProcessScope
    extends UserProcessScope {

    mockPersistenceServices.getInstallation(contextSupport) returns
      Task(\/-(initialInstallation))

  }

  trait CreateInstallationErrorUserProcessScope
    extends UserProcessScope {

    mockApiServices.createInstallation(
      id = initialInstallation.id,
      deviceType = initialInstallation.deviceType,
      deviceToken = initialInstallation.deviceToken,
      userId = initialInstallation.userId) returns
      Task(-\/(exception))

  }

  trait InstallationErrorUserProcessScope
    extends UserProcessScope {

    mockPersistenceServices.getInstallation(contextSupport) returns
      Task(-\/(exception))

  }

  trait LoginErrorUserProcessScope
    extends UserProcessScope {

    mockApiServices.login(
      email = email,
      device = googleDevice) returns
      Task(-\/(exception))

  }

  trait SaveUserErrorUserProcessScope
    extends UserProcessScope {

    mockPersistenceServices.saveUser(user)(contextSupport) returns
      Task(-\/(exception))

  }

}

class UserProcessSpec
  extends UserProcessSpecification
  with DisjunctionMatchers {

  "Sign In in UserProcess" should {

    "returns status code with initial installation calling to create installation in ApiServices" in new InitialUserProcessScope {
      val result = userProcess.signIn(email, googleDevice)(contextSupport).run

      there was one(mockApiServices).login(anyString, any[GoogleDevice])
      there was one(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
      there was one(mockPersistenceServices).saveUser(user)(contextSupport)
      there was one(mockPersistenceServices).getInstallation(contextSupport)

      result must be_\/-[SignInResponse].which {
        signInResponse =>
          signInResponse.statusCode shouldEqual userStatusCode
      }
    }

    "returns status code with full installation calling to update installation in ApiServices" in new UserProcessScope {
      val result = userProcess.signIn(email, googleDevice)(contextSupport).run

      there was one(mockApiServices).login(anyString, any[GoogleDevice])
      there was one(mockApiServices).updateInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
      there was one(mockPersistenceServices).saveUser(user)(contextSupport)
      there was one(mockPersistenceServices).getInstallation(contextSupport)

      result must be_\/-[SignInResponse].which {
        signInResponse =>
          signInResponse.statusCode shouldEqual userStatusCode
      }
    }

    "returns a NineCardsException if login in ApiService returns a exception and shouldn't sync installation" in new LoginErrorUserProcessScope {
      val result = userProcess.signIn(email, googleDevice)(contextSupport).run
      there was one(mockApiServices).login(anyString, any[GoogleDevice])
      there was exactly(0)(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
      there was exactly(0)(mockApiServices).updateInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
      result must be_-\/[NineCardsException]
    }

    "returns a NineCardsException if save user fails and shouldn't sync installation" in new SaveUserErrorUserProcessScope {
      val result = userProcess.signIn(email, googleDevice)(contextSupport).run
      there was one(mockApiServices).login(anyString, any[GoogleDevice])
      there was one(mockPersistenceServices).saveUser(user)(contextSupport)
      there was exactly(0)(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
      there was exactly(0)(mockApiServices).updateInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
      result must be_-\/[NineCardsException]
    }

  }

  "Register in UserProcess" should {

    "save installation when the installation don't exists in local" in new InstallationErrorUserProcessScope {
      val result = userProcess.register(contextSupport).run
      there was one(mockPersistenceServices).saveInstallation(initialInstallation)(contextSupport)
      result must be_\/-[Unit]
    }

    "don't save installation when the installation exists in local" in new UserProcessScope {
      val result = userProcess.register(contextSupport).run
      there was exactly(0)(mockPersistenceServices).saveInstallation(initialInstallation)(contextSupport)
      result must be_\/-[Unit]
    }

  }

  "Unregister in UserProcess" should {

    "save initial installation and call to create installation in ApiService" in new UserProcessScope {
      val result = userProcess.unregister(contextSupport).run
      there was one(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
      there was one(mockPersistenceServices).saveInstallation(initialInstallation)(contextSupport)
      there was one(mockPersistenceServices).resetUser(contextSupport)
      result must be_\/-[Unit]
    }

    "returns a NineCardsException if sync fails" in new CreateInstallationErrorUserProcessScope {
      val result = userProcess.unregister(contextSupport).run
      there was one(mockApiServices).createInstallation(any[Option[String]], any[Option[String]], any[Option[String]], any[Option[String]])
      there was exactly(0)(mockPersistenceServices).saveInstallation(initialInstallation)(contextSupport)
      there was exactly(0)(mockPersistenceServices).resetUser(contextSupport)
      result must be_-\/[NineCardsException]
    }

  }

}
