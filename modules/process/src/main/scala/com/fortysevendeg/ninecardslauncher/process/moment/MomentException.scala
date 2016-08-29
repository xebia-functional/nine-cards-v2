package com.fortysevendeg.ninecardslauncher.process.moment

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException


case class MomentException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsMomentException {
  implicit def momentException = (t: Throwable) => MomentException(t.getMessage, Option(t))
}