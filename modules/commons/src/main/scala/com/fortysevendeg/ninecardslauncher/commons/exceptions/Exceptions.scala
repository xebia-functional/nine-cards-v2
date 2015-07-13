package com.fortysevendeg.ninecardslauncher.commons.exceptions

import scala.util.control.NonFatal
import scalaz.{-\/, \/-, \/}

object Exceptions {

  case class NineCardsException(msg: String, cause: Option[Throwable] = None) extends RuntimeException(msg) {
    cause map initCause
  }

  def fromTryCatchNineCardsException[T](a: => T): NineCardsException \/ T = try {
    \/-(a)
  } catch {
    case NonFatal(t) => -\/(NineCardsException(t.getMessage, Some(t)))
  }

}
