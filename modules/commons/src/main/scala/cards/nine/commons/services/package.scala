package cards.nine.commons

import cats.Monad
import cats.data.EitherT
import monix.eval.Task
import cats.syntax.either._

import scala.language.{higherKinds, implicitConversions}

package object services {

  object TaskService {

    implicit val taskMonad: Monad[Task] =
      monix.cats.monixToCatsMonad[Task](monix.eval.Task.nondeterminism)

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
