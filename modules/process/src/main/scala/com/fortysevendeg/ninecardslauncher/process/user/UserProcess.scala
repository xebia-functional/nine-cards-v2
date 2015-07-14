package com.fortysevendeg.ninecardslauncher.process.user

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.api.models.GoogleDevice

import scalaz.\/
import scalaz.concurrent.Task

trait UserProcess {
  def signIn(email: String, device: GoogleDevice)(implicit context: ContextSupport): Task[NineCardsException \/ SignInResponse]
  def register(implicit context: ContextSupport): Task[NineCardsException \/ Unit]
  def unregister(implicit context: ContextSupport): Task[NineCardsException \/ Unit]
}
