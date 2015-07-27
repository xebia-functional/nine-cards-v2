package com.fortysevendeg.ninecardslauncher.process.userconfig

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.userconfig.impl.UserConfigProcessImpl
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollection, UserInfo}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{GetUserConfigResponse, ApiServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task


trait UserConfigProcessSpecification
  extends Specification
  with Mockito {

  val exception = NineCardsException("")

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
      apiUtils.getRequestConfig(contextSupport) returns Task(\/-(requestConfig))
    }

  }

  trait ValidateUserConfigScope {
    self: UserConfigProcessScope =>

    mockApiServices.getUserConfig()(requestConfig) returns
      Task(\/-(GetUserConfigResponse(200, userConfig)))

  }

  trait ErrorUserConfigScope {
    self: UserConfigProcessScope =>

    mockApiServices.getUserConfig()(requestConfig) returns
      Task(-\/(exception))
  }

}

class UserConfigProcessSpec
  extends UserConfigProcessSpecification
  with DisjunctionMatchers {

  "Get UserInfo in UserConfigProcess" should {

    "returns an equal number of devices to user config" in
      new UserConfigProcessScope with ValidateUserConfigScope {
        val result = userConfigProcess.getUserInfo(contextSupport).run
        result must be_\/-[UserInfo].which {
          userInfo =>
            userInfo.devices.length shouldEqual userConfig.devices.length
        }
      }

    "returns a NineCardsException if user config service fails" in
      new UserConfigProcessScope with ErrorUserConfigScope {
        val result = userConfigProcess.getUserInfo(contextSupport).run
        result must be_-\/[NineCardsException]
      }

  }

  "Get UserCollection in UserConfigProcess" should {

    "returns an equal number of collections in existing deviceId to number of collections in user config" in
      new UserConfigProcessScope with ValidateUserConfigScope {
        val result = userConfigProcess.getUserCollection(firstDeviceId)(contextSupport).run
        result must be_\/-[Seq[UserCollection]].which {
          userCollections =>
            val device = userConfig.devices.find(_.deviceId == firstDeviceId)
            val count = device map (_.collections.length) getOrElse -1
            userCollections.length shouldEqual count
        }
      }

    "returns a NineCardsException if not exist deviceId" in
      new UserConfigProcessScope with ValidateUserConfigScope {
        val result = userConfigProcess.getUserCollection(noDeviceId)(contextSupport).run
        result must be_-\/[NineCardsException]
      }

    "returns a NineCardsException if user config service fails" in
      new UserConfigProcessScope with ErrorUserConfigScope {
        val result = userConfigProcess.getUserCollection(firstDeviceId)(contextSupport).run
        result must be_-\/[NineCardsException]
      }

  }

}
