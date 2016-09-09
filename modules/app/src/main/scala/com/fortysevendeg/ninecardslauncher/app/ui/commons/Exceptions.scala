package com.fortysevendeg.ninecardslauncher.app.ui.commons

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class UiException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

trait ImplicitsUiExceptions {
  implicit def uiExceptionConverter = (t: Throwable) => UiException(t.getMessage, Option(t))
}