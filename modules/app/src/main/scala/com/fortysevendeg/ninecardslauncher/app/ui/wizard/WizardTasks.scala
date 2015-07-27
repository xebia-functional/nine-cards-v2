package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserInfo

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

trait WizardTasks {

  def signInUser(username: String, device: Device)
    (implicit context: ContextSupport, di: Injector): Task[NineCardsException \/ UserInfo] =
    for {
      response <- di.userProcess.signIn(username, device) ▹ eitherT
      userInfo <- di.userConfigProcess.getUserInfo ▹ eitherT
    } yield userInfo

}
