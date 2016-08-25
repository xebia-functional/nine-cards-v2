package com.fortysevendeg.ninecardslauncher.commons

import cats.data.{Xor, XorT}
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.{NineCardException, _}

import scala.language.implicitConversions
import scalaz.concurrent.Task

object NineCardExtensions {


  implicit class XorTExtensions[A](r: XorT[Task, NineCardException, A]) {

    def resolve[E <: NineCardException](implicit converter: Throwable => E): XorT[Task, NineCardException, A] =
      r leftMap converter

    def resolveTo(result: A): XorT[Task, NineCardException, A] = {
      val task: Task[Xor[NineCardException, A]] = r.value
      val innerResult: Task[NineCardException Xor A] = task.map {
        case r @ Xor.Right(_) => r
        case Xor.Left(_) => Xor.right(result)
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

    def resolveOption() = {
      val task: Task[NineCardException Xor Option[A]] = r.value

      val innerResult: Task[NineCardException Xor A] = task.map {
        case error @ Xor.Left(_) => error
        case Xor.Right(result) => result match {
          case Some(a) => Xor.Right(a)
          case _ => Xor.left(EmptyException("Value not found"))
        }
      }
      XorT(innerResult)
    }

  }

}
