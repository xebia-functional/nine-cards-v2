package com.fortysevendeg.ninecardslauncher.commons.test

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import monix.eval.Task

import scala.concurrent.Await
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global

object TaskServiceTestOps {

  implicit class TaskServiceTestAwait[A](t: Task[NineCardException Either A]) {

    def run: NineCardException Either A = Await.result(t.runAsync, 10 seconds)

  }

}
