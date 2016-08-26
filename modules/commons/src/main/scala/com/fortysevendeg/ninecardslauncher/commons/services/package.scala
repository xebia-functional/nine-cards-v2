package com.fortysevendeg.ninecardslauncher.commons

import cats.data.{Xor, XorT}
import cats.{Functor, Monad}

import scala.language.{higherKinds, implicitConversions}
import scalaz.concurrent.Task

package object services {

    object TaskService {

    implicit val taskFunctor = new Functor[Task] {
      override def map[A, B](fa: Task[A])(f: (A) => B): Task[B] = fa.map(f)
    }

    implicit val taskMonad = new Monad[Task] {
      override def flatMap[A, B](fa: Task[A])(f: (A) => Task[B]): Task[B] = fa.flatMap(f)
      override def pure[A](x: A): Task[A] = Task(x)
    }

    trait NineCardException extends RuntimeException {
      def message: String
      def cause: Option[Throwable]
    }

    type TaskService[A] = XorT[Task, NineCardException, A]

    def apply[A](f: Task[NineCardException Xor A]) : TaskService[A] = {
      XorT[Task, NineCardException, A](f)
    }

  }

}
