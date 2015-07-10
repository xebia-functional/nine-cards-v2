package com.fortysevendeg.ninecardslauncher.services

import org.specs2.mock.Mockito

import scalaz.concurrent.Task

trait BaseTestSupport
  extends Mockito {

  def runTask[T](t: => Task[T]) = t.run

}
