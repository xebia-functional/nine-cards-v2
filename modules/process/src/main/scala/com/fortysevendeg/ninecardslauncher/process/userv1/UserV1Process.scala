package com.fortysevendeg.ninecardslauncher.process.userv1

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.userv1.models.UserV1Info

trait UserV1Process {
  def getUserInfo(deviceName: String, oauthScopes: Seq[String])(implicit context: ContextSupport): TaskService[UserV1Info]
}
