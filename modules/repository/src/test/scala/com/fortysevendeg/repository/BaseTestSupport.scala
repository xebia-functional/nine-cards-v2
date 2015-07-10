package com.fortysevendeg.repository

import scalaz.concurrent.Task

trait BaseTestSupport {

  def runTask[T](t: => Task[T]) = t.run
}
