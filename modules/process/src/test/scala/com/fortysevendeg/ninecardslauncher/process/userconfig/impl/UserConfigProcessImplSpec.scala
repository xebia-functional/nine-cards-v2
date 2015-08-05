package com.fortysevendeg.ninecardslauncher.process.userconfig.impl

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ApiServices, GetUserConfigResponse}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task


trait UserConfigProcessSpecification
  extends Specification
  with Mockito {

  val apiServiceException = ApiServiceException("")

  trait UserConfigProcessScope
    extends Scope
    with UserConfigProcessData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val mockApiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]

    val userConfigProcess = new UserConfigProcessImpl(mockApiServices, mockPersistenceServices) {
      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfig(contextSupport) returns
        Service(Task(Result.answer(requestConfig)))
    }

  }

  trait ValidateUserConfigScope {
    self: UserConfigProcessScope =>

    mockApiServices.getUserConfig()(requestConfig) returns
      Service(Task(Result.answer(GetUserConfigResponse(statusCodeOk, userConfig))))

  }

  trait ErrorUserConfigScope {
    self: UserConfigProcessScope =>

    mockApiServices.getUserConfig()(requestConfig) returns
      Service(Task(Errata(apiServiceException)))
  }

}

class UserConfigProcessImplSpec
  extends UserConfigProcessSpecification {

  "Get UserInfo in UserConfigProcess" should {

    "returns an equal number of devices to user config" in
      new UserConfigProcessScope with ValidateUserConfigScope {
        val result = userConfigProcess.getUserInfo(contextSupport).run.run
        result must beLike {
          case Answer(userInfo) =>
            userInfo.devices.length shouldEqual userConfig.devices.length
        }
      }

    "returns a UserConfigException if user config service fails" in
      new UserConfigProcessScope with ErrorUserConfigScope {
        val result = userConfigProcess.getUserInfo(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[UserConfigException]
          }
        }
      }

  }

  "Get UserCollection in UserConfigProcess" should {

    "returns an equal number of collections in existing deviceId to number of collections in user config" in
      new UserConfigProcessScope with ValidateUserConfigScope {
        val result = userConfigProcess.getUserCollection(firstDeviceId)(contextSupport).run.run
        result must beLike {
          case Answer(userCollections) =>
            val device = userConfig.devices.find(_.deviceId == firstDeviceId)
            val count = device map (_.collections.length) getOrElse -1
            userCollections.length shouldEqual count
            userCollections map (_.name) shouldEqual device.map(_.collections.map(_.name)).getOrElse("")
        }
      }

    "returns a UserConfigException if not exist deviceId" in
      new UserConfigProcessScope with ValidateUserConfigScope {
        val result = userConfigProcess.getUserCollection(noDeviceId)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[UserConfigException]
          }
        }
      }

    "returns a UserConfigException if user config service fails" in
      new UserConfigProcessScope with ErrorUserConfigScope {
        val result = userConfigProcess.getUserCollection(firstDeviceId)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[UserConfigException]
          }
        }
      }

  }

}
