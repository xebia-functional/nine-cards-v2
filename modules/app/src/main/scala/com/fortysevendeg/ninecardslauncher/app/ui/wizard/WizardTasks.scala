package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserInfo

trait WizardTasks {

  def signInUser(username: String, device: Device)
    (implicit context: ContextSupport, di: Injector): ServiceDef2[UserInfo, UserException with UserConfigException] =
    for {
      response <- di.userProcess.signIn(username, device)
      userInfo <- di.userConfigProcess.getUserInfo
    } yield userInfo

}
