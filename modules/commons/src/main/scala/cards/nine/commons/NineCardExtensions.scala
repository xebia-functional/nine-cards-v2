package cards.nine.commons

import cats.data.EitherT
import cards.nine.commons.services.TaskService._
import monix.eval.Task
import cats.syntax.either._

import scala.language.implicitConversions

object NineCardExtensions {


  implicit class EitherTExtensions[A](r: EitherT[Task, NineCardException, A]) {

    def resolve[E <: NineCardException](implicit converter: Throwable => E): EitherT[Task, NineCardException, A] =
      resolveLeft(e => Left(converter(e)))

    def resolveIf[E <: NineCardException](condition: Boolean, ifNot: A): EitherT[Task, NineCardException, A] =
      if (condition) {
        r
      } else {
        EitherT(Task(Either.right(ifNot)))
      }

    def resolveLeftTo(result: A): EitherT[Task, NineCardException, A] =
      resolveLeft((_) => Right(result))

    def resolveLeft(mapLeft: NineCardException => Either[NineCardException, A]): EitherT[Task, NineCardException, A] =
      resolveSides((r) => Right(r), mapLeft)

    def resolveRight[B](mapRight: (A) => Either[NineCardException, B]): EitherT[Task, NineCardException, B] =
      resolveSides(mapRight, (e) => Left(e))

    def resolveAsOption: EitherT[Task, NineCardException, Option[A]] = r.map(result => Option(result)).resolveLeftTo(None)

    def resolveSides[B](
      mapRight: (A) => Either[NineCardException, B],
      mapLeft: NineCardException => Either[NineCardException, B] = (e: NineCardException) => Left(e)): EitherT[Task, NineCardException, B] = {
      val task: Task[Either[NineCardException, A]] = r.value
      val innerResult: Task[NineCardException Either B] = task.map {
        case Right(v) => mapRight(v)
        case Left(e) => mapLeft(e)
      }
      EitherT(innerResult)
    }

  }

  implicit class EitherTOptionExtensions[A](r : EitherT[Task, NineCardException, Option[A]]) {

    case class EmptyException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)
        with NineCardException {
      cause map initCause
    }

    def resolveOption(message: String): EitherT[Task, NineCardException, A] = resolveOption(Option(message))

    def resolveOption(message: Option[String] = None): EitherT[Task, NineCardException, A] =
      r.resolveRight {
        case Some(v) => Right(v)
        case None => Left(EmptyException(message getOrElse "Value not found"))
      }

  }

}
