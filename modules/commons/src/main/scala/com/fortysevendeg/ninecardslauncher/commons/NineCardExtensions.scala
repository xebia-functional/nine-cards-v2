package com.fortysevendeg.ninecardslauncher.commons

import rapture.core._
import rapture.core.scalazInterop.ResultT

import scala.language.implicitConversions
import scala.reflect.ClassTag
import scalaz._
import scalaz.concurrent.Task
import Scalaz._

object NineCardExtensions {

  implicit def toResult[A, E <: Exception : ClassTag](disj: E \/ A): Result[A, E] = disj match {
    case -\/(e) => Result.errata[A, E](e)
    case \/-(a) => Result.answer[A, E](a)
  }

  implicit def toDisjunction[A, E <: Exception : ClassTag](res: Result[A, E]): E \/ A = res match {
    case Answer(a) => a.right[E]
    case e@Errata(_) => e.errata.head.left[A]
    case Unforeseen(e) => throw e
  }

  implicit def toTaskDisjuntionFromResult[A, E <: Exception : ClassTag](t: Task[Result[A, E]]): Task[E \/ A] =
    t map (r => toDisjunction(r))

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
      val innerResult: Task[Result[A, E]] = task.map(result => result match {
        case e @ Errata(_) =>
          val exs = e.exceptions map (ie => (implicitly[ClassTag[E]], (ie.getMessage, cv(ie))))
          Errata[A, E](exs)
        case Unforeseen(e) => Unforeseen[A, E](e)
        case Answer(s) => Answer[A, E](s)
      })
      ResultT(innerResult)
    }

  }


}
