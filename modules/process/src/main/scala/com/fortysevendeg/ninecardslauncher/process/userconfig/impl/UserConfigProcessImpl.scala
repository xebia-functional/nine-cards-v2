package com.fortysevendeg.ninecardslauncher.process.userconfig.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.userconfig.{ImplicitsUserConfigException, UserConfigConversions, UserConfigException, UserConfigProcess}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.api.models.{UserConfigCollection, UserConfigDevice}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import rapture.core.Result

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

  private[this] def getUserConfigCollections(device: Option[UserConfigDevice]): ServiceDef2[Seq[UserConfigCollection], UserConfigException] =
    Service {
      Task {
        device map (device => Result.answer[Seq[UserConfigCollection], UserConfigException](device.collections)) getOrElse
          Result.errata[Seq[UserConfigCollection], UserConfigException](UserConfigException("Device don't found"))
      }
    }

}
