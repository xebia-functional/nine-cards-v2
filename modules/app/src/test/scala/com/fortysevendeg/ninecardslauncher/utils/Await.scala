package com.fortysevendeg.ninecardslauncher.utils

import scala.concurrent.duration.Duration

object Await {

  def result(method: => Unit, duration: Duration): Unit = {
    method
    Thread.sleep(duration.toMillis)
  }

}
