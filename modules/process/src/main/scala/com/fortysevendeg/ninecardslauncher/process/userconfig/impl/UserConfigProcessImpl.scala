package com.fortysevendeg.ninecardslauncher.process.userconfig.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
import com.fortysevendeg.ninecardslauncher.process.userconfig.{ImplicitsUserConfigException, UserConfigConversions, UserConfigException, UserConfigProcess}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.api.models.{UserConfigCollection, UserConfigDevice}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices

import scalaz.concurrent.Task

class UserConfigProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends UserConfigProcess
  with ImplicitsUserConfigException
  with UserConfigConversions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getUserInfo(implicit context: ContextSupport) = (for {
    requestConfig <- apiUtils.getRequestConfig
    userConfigResponse <- apiServices.getUserConfig()(requestConfig)
  } yield toUserInfo(requestConfig.deviceId, userConfigResponse.userConfig)).resolve[UserConfigException]

  override def getUserCollection(deviceId: String)(implicit context: ContextSupport) = (for {
    requestConfig <- apiUtils.getRequestConfig
    userConfigResponse <- apiServices.getUserConfig()(requestConfig)
    device = userConfigResponse.userConfig.devices.find(_.deviceId == deviceId)
    userConfigCollections <- getUserConfigCollections(device)
  } yield userConfigCollections map toUserCollection).resolve[UserConfigException]

  private[this] def getUserConfigCollections(device: Option[UserConfigDevice]): CatsService[Seq[UserConfigCollection]] =
    CatsService {
      Task {
        device map (device => Xor.right(device.collections)) getOrElse
          Xor.left(UserConfigException("Device don't found"))
      }
    }

}
