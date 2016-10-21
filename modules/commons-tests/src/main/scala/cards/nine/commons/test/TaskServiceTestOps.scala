package cards.nine.commons.test

import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{NineCardException, _}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.reflect.ClassTag

object TaskServiceTestOps {

  implicit class TaskServiceTestAwait[A](t: Task[NineCardException Either A]) {

    def run: NineCardException Either A = Await.result(t.runAsync, 10.seconds)

  }

}

trait TaskServiceSpecification extends Specification {

  def serviceRight[A <: NineCardException, B](v: B) = TaskService(Task(Right[A, B](v)))

  def serviceLeft[A <: NineCardException, B](e: A) = TaskService(Task(Left[A, B](e)))

  implicit class TaskServiceTestAwait[B](service: TaskService[B]) {

    def run: NineCardException Either B = Await.result(service.value.runAsync, Duration.Inf)

    def mustLeft[A <: NineCardException](implicit classTag: ClassTag[A]): Unit =
      service.run must beLike {
        case Left(e) => e must beAnInstanceOf[A]
      }

    def mustRight(f: (B) => MatchResult[_]): Unit =
      service.run must beLike {
        case Right(v) => f(v)
      }

    def mustRightUnit: Unit = mustRight (_ shouldEqual ((): Unit))

    def mustRightNone: Unit = mustRight (_ shouldEqual None)
  }

}
