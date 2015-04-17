package com.fortysevendeg.ninecardslauncher.utils

import java.io.Closeable

import scala.util.control.Exception._

object ResourceUtils {

  def withResource[C <: Closeable, R](closeable: C)(f: C => R) = {
    allCatch.andFinally(closeable.close())(f(closeable))
  }

}
