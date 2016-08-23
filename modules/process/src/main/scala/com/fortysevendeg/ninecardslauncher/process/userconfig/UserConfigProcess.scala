package com.fortysevendeg.ninecardslauncher.process.userconfig

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollection, UserInfo}

trait UserConfigProcess {
  def getUserInfo(implicit context: ContextSupport): CatsService[UserInfo]
  def getUserCollection(deviceId: String)(implicit context: ContextSupport): CatsService[Seq[UserCollection]]
}
