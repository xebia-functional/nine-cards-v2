package com.fortysevendeg.ninecardslauncher.commons

import cats.syntax.either._

object CatchAll {

  def apply[E] = new CatchingAll[E]()

  class CatchingAll[E] {
    def apply[V](f: => V)(implicit converter: Throwable => E): Either[E, V] =
      Either.catchNonFatal(f) leftMap converter
  }

}
