package com.fortysevendeg.ninecardslauncher.process.recognition

import cards.nine.commons.services.TaskService.NineCardException

case class RecognitionProcessException(  message: String,  cause: Option[Throwable] = None,  recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

trait ImplicitsRecognitionProcessExceptions {
  implicit def recognitionProcessExceptionConverter = (t: Throwable) => RecognitionProcessException(t.getMessage, Option(t))
}