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

    def apply[X, A, B <: Exception : ClassTag](f: X => Task[Result[A, B]]) : ServiceDef[X, A, B] = {
      ResultT[({type λ[α] = ReaderT[Task, X, α]})#λ, A, B](Kleisli.kleisli(f))
    }

    @deprecated("migrate to Result type")
    implicit def toDisjunctionTask[E <: Throwable, A](f: EitherT[Task, E, A]): Task[E \/ A] = f.run

    @deprecated("migrate to Result type")
    implicit def toDisjunctionT[E <: Throwable, A](f: Task[E \/ A]): EitherT[Task, E, A] = EitherT.eitherT(f)

  }

}
