package com.fortysevendeg.repository

import org.specs2.execute.{Result, AsResult}
import org.specs2.specification.{Scope, Around}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Await, Future}
import scalaz.concurrent.Task

trait BaseTestSupport extends Around with Scope {

  def await[T](f: => Future[T]) = Await.result(f, Duration.Inf)
  def runTask[T](t: => Task[T]) = t.run
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  override def around[T: AsResult](t: => T): Result = AsResult.effectively(t)


}
