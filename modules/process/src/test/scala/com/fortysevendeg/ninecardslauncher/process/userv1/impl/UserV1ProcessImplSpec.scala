package com.fortysevendeg.ninecardslauncher.process.userv1.impl

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.userv1.UserV1Exception
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ApiServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait UserV1ProcessSpecification
  extends Specification
  with Mockito {

  val apiServiceException = ApiServiceException("")

  trait UserV1ProcessScope
    extends Scope
    with UserV1ProcessData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val mockApiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]
    mockPersistenceServices.getAndroidId(any) returns TaskService(Task(Xor.right(deviceId)))

    val userConfigProcess = new UserV1ProcessImpl(mockApiServices, mockPersistenceServices)

  }

}

class UserV1ProcessImplSpec
  extends UserV1ProcessSpecification {

  "Get UserInfo in UserConfigProcess" should {

    "returns a UserConfigException if there is no active user" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns None

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserV1Exception]
        }

        there was one(contextSupport).getActiveUserId
        there was no(mockPersistenceServices).findUserById(any)
        there was no(mockApiServices).loginV1(any, any)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a UserConfigException if the user doesn't exists in the database" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)

        mockPersistenceServices.findUserById(any) returns TaskService(Task(Xor.right(None)))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserV1Exception]
        }

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was no(mockApiServices).loginV1(any, any)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a UserConfigException if the user doesn't have an email" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)

        mockPersistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(persistenceUser.copy(email = None)))))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserV1Exception]
        }

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was no(mockApiServices).loginV1(any, any)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a UserConfigException if the user doesn't have a market token" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)

        mockPersistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(persistenceUser.copy(marketToken = None)))))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserV1Exception]
        }

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was no(mockApiServices).loginV1(any, any)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a UserConfigException when the login doesn't return a session token" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)

        mockPersistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(persistenceUser))))

        mockApiServices.loginV1(any, any) returns TaskService(Task(Xor.right(loginResponseV1.copy(sessionToken = None))))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserV1Exception]
        }

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockApiServices).loginV1(email, googleDevice)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a right response when all services work fine" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)

        mockPersistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(persistenceUser))))

        mockApiServices.loginV1(any, any) returns TaskService(Task(Xor.right(loginResponseV1)))

        mockApiServices.getUserConfigV1()(any) returns TaskService(Task(Xor.right(getUserConfigResponse)))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beLike {
          case Xor.Right(userInfo) =>
            userInfo.devices.length shouldEqual userConfig.devices.length
            userInfo.devices map (_.deviceName) shouldEqual userConfig.devices.map(_.deviceName)
        }

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockApiServices).loginV1(email, googleDevice)
        there was one(mockApiServices).getUserConfigV1()(requestConfig)

      }

  }

}
