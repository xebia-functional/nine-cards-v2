package com.fortysevendeg.ninecardslauncher.commons

import rapture.core.Result
import rapture.core.scalazInterop.ResultT

import scala.language.{higherKinds, implicitConversions}
import scala.reflect.ClassTag
import scalaz._
import scalaz.concurrent.Task

package object services {

  object Service {

    type ServiceDef[X, A, E <: Exception] = ResultT[({type λ[α] = ReaderT[Task, X, α]})#λ, A, E]

    type ServiceDef2[A, E <: Exception] = ResultT[Task, A, E]

    def apply[X, A, B <: Exception : ClassTag](f: X => Task[Result[A, B]]) : ServiceDef[X, A, B] = {
      ResultT[({type λ[α] = ReaderT[Task, X, α]})#λ, A, B](Kleisli.kleisli(f))
    }

    def apply[A, B <: Exception : ClassTag](f: Task[Result[A, B]]) : ServiceDef2[A, B] = {
      ResultT[Task, A, B](f)
    }

    def success[A, B <: Exception: ClassTag](a: A) : ServiceDef2[A,B] =
      ResultT[Task, A, B](Task.now(Result.answer(a)))

  }

}
