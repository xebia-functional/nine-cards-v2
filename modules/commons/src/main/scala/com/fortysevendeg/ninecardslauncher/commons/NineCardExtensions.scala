package com.fortysevendeg.ninecardslauncher.commons

import cats.data.XorT
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
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
  implicit class XorTExtensions[Val](r: XorT[Task, NineCardException, Val]) {

    def resolve[E <: NineCardException](implicit converter: Throwable => E): XorT[Task, NineCardException, Val] =
      r leftMap converter

  }

}
