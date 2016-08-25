package com.fortysevendeg.ninecardslauncher.process.social

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException
import com.fortysevendeg.ninecardslauncher.services.plus.GooglePlusServicesException

import scalaz.Scalaz._

case class SocialProfileProcessException(  message: String,  cause: Option[Throwable] = None,  recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

trait ImplicitsSocialProfileProcessExceptions {

  implicit def googlePlusExceptionConverter = (t: Throwable) => {
    t match {
      case gPlusException: GooglePlusServicesException =>
        SocialProfileProcessException(gPlusException.getMessage, gPlusException.some, gPlusException.recoverable)
      case _ => SocialProfileProcessException(t.getMessage, Option(t))
    }
  }

}