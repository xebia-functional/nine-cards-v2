package com.fortysevendeg.ninecardslauncher.process.userconfig

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollection, UserInfo}

trait UserConfigProcess {
  def getUserInfo(implicit context: ContextSupport): ServiceDef2[UserInfo, UserConfigException]
  def getUserCollection(deviceId: String)(implicit context: ContextSupport): ServiceDef2[Seq[UserCollection], UserConfigException]
}
