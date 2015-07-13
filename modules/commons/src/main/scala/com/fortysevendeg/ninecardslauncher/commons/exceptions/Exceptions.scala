package com.fortysevendeg.ninecardslauncher.commons.exceptions

object Exceptions {

  case class NineCardsException(msg: String, cause: Option[Throwable] = None) extends RuntimeException(msg) {
    cause map initCause
  }

}
