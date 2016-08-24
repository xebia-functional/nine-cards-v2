package com.fortysevendeg.ninecardslauncher.process.user

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.process.user.models.User

trait UserProcess {

  def signIn(email: String, androidMarketToken: String, emailTokenId: String)(implicit context: ContextSupport): CatsService[Unit]

  def register(implicit context: ContextSupport): CatsService[Unit]

  def unregister(implicit context: ContextSupport): CatsService[Unit]

  def getUser(implicit context: ContextSupport): CatsService[User]

  def updateUserDevice(deviceName: String, deviceCloudId: String, deviceToken: Option[String] = None)(implicit context: ContextSupport): CatsService[Unit]

  def updateDeviceToken(deviceToken: String)(implicit context: ContextSupport): CatsService[Unit]
}
