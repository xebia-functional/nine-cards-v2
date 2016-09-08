package com.fortysevendeg.ninecardslauncher.commons

import cats.data.{Xor, XorT}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._

import scala.language.implicitConversions
import scalaz.concurrent.Task

object NineCardExtensions {


  implicit class XorTExtensions[A](r: XorT[Task, NineCardException, A]) {

    def resolve[E <: NineCardException](implicit converter: Throwable => E): XorT[Task, NineCardException, A] =
      resolveLeft(e => Xor.left(converter(e)))

    def resolveLeftTo(result: A): XorT[Task, NineCardException, A] =
      resolveLeft((_) => Xor.right(result))

    def resolveLeft(mapLeft: NineCardException => Xor[NineCardException, A]): XorT[Task, NineCardException, A] =
      resolveSides((r) => Xor.right(r), mapLeft)

    def resolveRight[B](mapRight: (A) => Xor[NineCardException, B]): XorT[Task, NineCardException, B] =
      resolveSides(mapRight, (e) => Xor.left(e))

    def resolveSides[B](
      mapRight: (A) => Xor[NineCardException, B],
      mapLeft: NineCardException => Xor[NineCardException, B] = (e: NineCardException) => Xor.left(e)): XorT[Task, NineCardException, B] = {
      val task: Task[Xor[NineCardException, A]] = r.value
      val innerResult: Task[NineCardException Xor B] = task.map {
        case Xor.Right(v) => mapRight(v)
        case Xor.Left(e) => mapLeft(e)
      }
      XorT(innerResult)
    }

  }

  implicit class XorTOptionExtensions[A](r : XorT[Task, NineCardException, Option[A]]) {

    case class EmptyException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)
        with NineCardException {
      cause map initCause
    }

    def resolveOption() =
      r.resolveRight {
        case Some(v) => Xor.right(v)
        case None => Xor.left(EmptyException("Value not found"))
      }

  }

}
