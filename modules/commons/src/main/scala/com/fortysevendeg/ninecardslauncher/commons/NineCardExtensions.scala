package com.fortysevendeg.ninecardslauncher.commons

import cats.data.{Xor, XorT}
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.{NineCardException, _}
import rapture.core._
import rapture.core.scalazInterop.ResultT

import scala.language.implicitConversions
import scala.reflect.ClassTag
import scalaz._
import scalaz.concurrent.Task

object NineCardExtensions {

  object CatchAll {

    def apply[E <: Exception] = new CatchingAll[E]()

    class CatchingAll[E <: Exception]() {
      def apply[A](blk: => A)(implicit classTag: ClassTag[E], cv: Throwable => E): Result[A, E] =
        \/.fromTryCatchNonFatal(blk) match {
          case \/-(x) => Result.answer[A, E](x)
          case -\/(e) => Errata(Seq((implicitly[ClassTag[E]], (e.getMessage, cv(e)))))
        }
    }

  }

  implicit class ResultTExtensions[A, B <: Exception : ClassTag](r : ResultT[Task, A, B]) {

    def resolve[E <: Exception : ClassTag](implicit cv: Exception => E) = {
      val task: Task[Result[A, B]] = r.run
      val innerResult: Task[Result[A, E]] = task.map {
        case e@Errata(_) =>
          val exs = e.exceptions map (ie => (implicitly[ClassTag[E]], (ie.getMessage, cv(ie))))
          Errata[A, E](exs)
        case Unforeseen(e) => Unforeseen[A, E](e)
        case Answer(s) => Answer[A, E](s)
      }
      ResultT(innerResult)
    }

    def resolveTo(result: A) = {
      val task: Task[Result[A, B]] = r.run
      val innerResult: Task[Result[A, B]] = task.map {
        case Errata(_) => Answer(result)
        case Unforeseen(e) => Answer(result)
        case answer@Answer(_) => answer
      }
      ResultT(innerResult)
    }

  }

  implicit class ResultTOptionExtensions[A, B <: Exception : ClassTag](r : ResultT[Task, Option[A], B]) {

    def resolveOption() = {
      val task: Task[Result[Option[A], B]] = r.run

      val innerResult: Task[Result[A, B]] = task.map {
        case Errata(errors) => Errata(errors)
        case Unforeseen(u) => Unforeseen(u)
        case Answer(result) => result match {
          case Some(a) => Answer(a)
          case _ => Errata(Seq.empty)
        }
      }
      ResultT(innerResult)
    }

  }

  // Cats extensions

  implicit class XorTExtensions[A](r: XorT[Task, NineCardException, A]) {

    def resolve[E <: NineCardException](implicit converter: Throwable => E): XorT[Task, NineCardException, A] =
      r leftMap converter

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
          case _ => Xor.left(EmptyException(""))
        }
      }
      XorT(innerResult)
    }

  }

}
