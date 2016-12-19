package cards.nine.process.user

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.User

trait UserProcess {

  def signIn(email: String, androidMarketToken: String, emailTokenId: String)(
      implicit context: ContextSupport): TaskService[Unit]

  def register(implicit context: ContextSupport): TaskService[Unit]

  def unregister(implicit context: ContextSupport): TaskService[Unit]

  def getUser(implicit context: ContextSupport): TaskService[User]

  def updateUserDevice(
      deviceName: String,
      deviceCloudId: String,
      deviceToken: Option[String] = None)(implicit context: ContextSupport): TaskService[Unit]

  def updateDeviceToken(deviceToken: String)(implicit context: ContextSupport): TaskService[Unit]
}
