package com.fortysevendeg.ninecardslauncher.process.user

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.user.models.Device

trait UserProcess {
  def signIn(email: String, device: Device)(implicit context: ContextSupport): ServiceDef2[SignInResponse, UserException]
  def register(implicit context: ContextSupport): ServiceDef2[Unit, UserException]
  def unregister(implicit context: ContextSupport): ServiceDef2[Unit, UserException]
}
