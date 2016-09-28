package com.fortysevendeg.ninecardslauncher.commons

import cats.data.EitherT
import cats.{Functor, Monad}
import monix.eval.Task
import cats.syntax.either._

import scala.language.{higherKinds, implicitConversions}

package object services {

  object TaskService {

    implicit val taskFunctor = new Functor[Task] {
      override def map[A, B](fa: Task[A])(f: (A) => B): Task[B] = fa.map(f)
    }

    implicit val taskMonad = new Monad[Task] {
      override def flatMap[A, B](fa: Task[A])(f: (A) => Task[B]): Task[B] = fa.flatMap(f)
      override def pure[A](x: A): Task[A] = Task(x)
      override def tailRecM[A, B](a: A)(f: (A) => Task[Either[A, B]]): Task[B] = defaultTailRecM(a)(f)
    }

    trait NineCardException extends RuntimeException {
      def message: String
      def cause: Option[Throwable]
    }

    type TaskService[A] = EitherT[Task, NineCardException, A]

    def apply[A](f: Task[NineCardException Either A]): TaskService[A] = {
      EitherT[Task, NineCardException, A](f)
    }

    def empty: TaskService[Unit] = EitherT(Task(Either.right((): Unit)))

    def left[A](ex: NineCardException): TaskService[A] = EitherT(Task(Either.left(ex)))

    def right[A](value: A): TaskService[A] = EitherT(Task(Either.right(value)))

  }

}
