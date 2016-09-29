package cards.nine.process.userv1

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.userv1.models.UserV1Info

trait UserV1Process {
  def getUserInfo(deviceName: String, oauthScopes: Seq[String])(implicit context: ContextSupport): TaskService[UserV1Info]
}
