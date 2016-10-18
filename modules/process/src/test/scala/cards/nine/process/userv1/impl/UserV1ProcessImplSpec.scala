package cards.nine.process.userv1.impl

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.{ApiV1TestData, UserTestData}
import cards.nine.commons.test.data.ApiV1Values._
import cards.nine.commons.test.data.UserValues._
import cards.nine.process.userv1.UserV1Exception
import cards.nine.services.api.{ApiServiceException, ApiServices}
import cards.nine.services.persistence.PersistenceServices
import cats.syntax.either._
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait UserV1ProcessSpecification
  extends Specification
  with Mockito {

  val apiServiceException = ApiServiceException("")

  trait UserV1ProcessScope
    extends Scope
    with UserTestData
    with ApiV1TestData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val mockApiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]
    mockPersistenceServices.getAndroidId(any) returns TaskService(Task(Either.right(deviceId)))

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
        result must beAnInstanceOf[Left[UserV1Exception,  _]]

        there was one(contextSupport).getActiveUserId
        there was no(mockPersistenceServices).findUserById(any)
        there was no(mockApiServices).loginV1(any, any)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a UserConfigException if the user doesn't exists in the database" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(None)))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beAnInstanceOf[Left[UserV1Exception,  _]]

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(userId)
        there was no(mockApiServices).loginV1(any, any)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a UserConfigException if the user doesn't have an email" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user.copy(email = None)))))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beAnInstanceOf[Left[UserV1Exception,  _]]

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(userId)
        there was no(mockApiServices).loginV1(any, any)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a UserConfigException if the user doesn't have a market token" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user.copy(marketToken = None)))))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beAnInstanceOf[Left[UserV1Exception,  _]]

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(userId)
        there was no(mockApiServices).loginV1(any, any)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a UserConfigException when the login doesn't return a session token" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        mockApiServices.loginV1(any, any) returns TaskService(Task(Either.right(loginResponseV1.copy(sessionToken = None))))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beAnInstanceOf[Left[UserV1Exception,  _]]

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(userId)
        there was one(mockApiServices).loginV1(email, device)
        there was no(mockApiServices).getUserConfigV1()(any)

      }

    "returns a right response when all services work fine" in
      new UserV1ProcessScope {

        contextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        mockApiServices.loginV1(any, any) returns TaskService(Task(Either.right(loginResponseV1)))
        mockApiServices.getUserConfigV1()(any) returns TaskService(Task(Either.right(userV1)))

        val result = userConfigProcess.getUserInfo(deviceName, permissions)(contextSupport).value.run
        result must beLike {
          case Right(userInfo) =>
            userInfo.devices.length shouldEqual userV1.devices.length
            userInfo.devices map (_.deviceName) shouldEqual userV1.devices.map(_.deviceName)
        }

        there was one(contextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(userId)
        there was one(mockApiServices).loginV1(email, device)
        there was one(mockApiServices).getUserConfigV1()(requestConfigV1)

      }
  }

}
