package com.fortysevendeg.ninecardslauncher.commons

import cats.data.Xor

object XorCatchAll {

  def apply[E] = new CatchingAll[E]()

  class CatchingAll[E] {
    def apply[V](f: => V)(implicit converter: Throwable => E): Xor[E, V] =
      Xor.catchNonFatal(f) leftMap converter
  }

}
