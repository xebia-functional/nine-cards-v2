package com.fortysevendeg.ninecardslauncher.commons

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException

import scala.util.control.NonFatal
import scalaz._
import scalaz.concurrent.Task
import Scalaz._

object NineCardExtensions {

  def toEnsureAttemptRun[A](f: Task[NineCardsException \/ A]): NineCardsException \/ A = f.attemptRun match {
    case -\/(ex) => -\/(NineCardsException(msg = ex.getMessage, cause = ex.some))
    case \/-(d) => d
  }

  def fromTryCatchNineCardsException[T](a: => T): NineCardsException \/ T = try {
    \/-(a)
  } catch {
    case NonFatal(t) => -\/(NineCardsException(t.getMessage, Some(t)))
  }

}
