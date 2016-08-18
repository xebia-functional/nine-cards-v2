package com.fortysevendeg.ninecardslauncher.process.userv1.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.userv1.models.{Device, UserV1Info}
import com.fortysevendeg.ninecardslauncher.process.userv1.{ImplicitsUserV1Exception, UserV1Conversions, UserV1Exception, UserV1Process}
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServices, GetUserConfigResponse, LoginResponseV1, RequestConfigV1}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServices}
import rapture.core.Result

import scalaz.concurrent.Task

class UserV1ProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends UserV1Process
  with ImplicitsUserV1Exception
  with UserV1Conversions {

  private[this] val noActiveUserErrorMsg = "No active user"
  private[this] val marketTokenErrorMsg = "Market token not available"
  private[this] val userNotLoggedMsg = "Can't authenticate user against backend V1"

  override def getUserInfo(deviceName: String, oauthScopes: Seq[String])(implicit context: ContextSupport) = {

    def loginV1(user: ServicesUser, androidId: String): ServiceDef2[LoginResponseV1, UserV1Exception] =
      (user.email, user.marketToken) match {
        case (Some(email), Some(marketToken)) =>
          val device = Device(
            name = deviceName,
            deviceId = androidId,
            secretToken = marketToken,
            permissions = oauthScopes)
          apiServices.loginV1(email, toGoogleDevice(device)).resolve[UserV1Exception]
        case _ =>
          Service(Task(Result.errata[LoginResponseV1, UserV1Exception](UserV1Exception(marketTokenErrorMsg))))
      }

    def requestConfig(
      androidId: String,
      maybeSessionToken: Option[String],
      marketToken: Option[String]): ServiceDef2[GetUserConfigResponse, UserV1Exception] =
      maybeSessionToken match {
        case Some(sessionToken) =>
          apiServices.getUserConfigV1()(RequestConfigV1(androidId, sessionToken, marketToken)).resolve[UserV1Exception]
        case _ =>
          Service(Task(Result.errata[GetUserConfigResponse, UserV1Exception](UserV1Exception(userNotLoggedMsg))))
      }

    def loadUserConfig(userId: Int): ServiceDef2[UserV1Info, UserV1Exception] =
      (for {
        Some(user) <- persistenceServices.findUserById(FindUserByIdRequest(userId))
        androidId <- persistenceServices.getAndroidId
        loginResponse <- loginV1(user, androidId)
        userConfigResponse <- requestConfig(androidId, loginResponse.sessionToken, user.marketToken)
      } yield toUserInfo(androidId, userConfigResponse.userConfig)).resolve[UserV1Exception]

    context.getActiveUserId match {
      case Some(id) => loadUserConfig(id)
      case None =>  Service(Task(Result.errata[UserV1Info, UserV1Exception](UserV1Exception(noActiveUserErrorMsg))))
    }

  }

}
