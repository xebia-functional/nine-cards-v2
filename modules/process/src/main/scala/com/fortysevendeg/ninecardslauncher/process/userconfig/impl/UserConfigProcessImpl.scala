package com.fortysevendeg.ninecardslauncher.process.userconfig.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollection, UserInfo}
import com.fortysevendeg.ninecardslauncher.process.userconfig.{UserConfigConversions, UserConfigProcess}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.api.models.{UserConfigCollection, UserConfigDevice}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

class UserConfigProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends UserConfigProcess
  with UserConfigConversions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getUserInfo(implicit context: ContextSupport): Task[NineCardsException \/ UserInfo] =
    for {
      requestConfig <- apiUtils.getRequestConfig ▹ eitherT
      userConfigResponse <- apiServices.getUserConfig()(requestConfig) ▹ eitherT
    } yield toUserInfo(userConfigResponse.userConfig)

  override def getUserCollection(deviceId: String)(implicit context: ContextSupport): Task[NineCardsException \/ Seq[UserCollection]] =
    for {
      requestConfig <- apiUtils.getRequestConfig ▹ eitherT
      userConfigResponse <- apiServices.getUserConfig()(requestConfig) ▹ eitherT
      device = userConfigResponse.userConfig.devices.find(_.deviceId == deviceId)
      userConfigCollections <- getUserConfigCollections(device) ▹ eitherT
    } yield userConfigCollections map toUserCollection

  private[this] def getUserConfigCollections(device: Option[UserConfigDevice]): Task[NineCardsException \/ Seq[UserConfigCollection]] =
    Task { device map (device => \/-(device.collections)) getOrElse -\/(NineCardsException("Device don't found")) }

}
