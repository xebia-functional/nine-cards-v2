package cards.nine.process.userv1

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.UserV1

trait UserV1Process {
  def getUserInfo(deviceName: String, oauthScopes: Seq[String])(
      implicit context: ContextSupport): TaskService[UserV1]
}
