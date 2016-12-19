package cards.nine.commons

import cats.data.EitherT
import cats.syntax.either._
import monix.cats.MonixToCatsConversions
import monix.eval.Task

import scala.language.{higherKinds, implicitConversions}

package object services {

  object TaskService extends MonixToCatsConversions {

    trait NineCardException extends RuntimeException {
      def message: String
      def cause: Option[Throwable]
    }

    type TaskService[A] = EitherT[Task, NineCardException, A]

    def apply[A](f: Task[NineCardException Either A]): TaskService[A] =
      EitherT[Task, NineCardException, A](f)

    def empty: TaskService[Unit] = EitherT(Task(Either.right((): Unit)))

    def left[A](ex: NineCardException): TaskService[A] = EitherT(Task(Either.left(ex)))

    def right[A](value: A): TaskService[A] = EitherT(Task(Either.right(value)))

  }

}
