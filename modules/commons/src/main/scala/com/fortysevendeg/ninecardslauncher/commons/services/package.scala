package com.fortysevendeg.ninecardslauncher.commons

import cats.{Functor, Monad}
import cats.data.{Xor, XorT}
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

  object CatsService {

    implicit val taskFunctor = new Functor[Task] {
      override def map[A, B](fa: Task[A])(f: (A) => B): Task[B] = fa.map(f)
    }

    implicit val taskMonad = new Monad[Task] {
      override def flatMap[A, B](fa: Task[A])(f: (A) => Task[B]): Task[B] = fa.flatMap(f)
      override def pure[A](x: A): Task[A] = Task(x)
    }

    type CatsService[Ex <: Throwable, Val] = XorT[Task, Ex, Val]

    def apply[Ex <: Throwable : ClassTag, Val](f: Task[Ex Xor Val]) : CatsService[Ex, Val] = {
      XorT[Task, Ex, Val](f)
    }

  }

}
