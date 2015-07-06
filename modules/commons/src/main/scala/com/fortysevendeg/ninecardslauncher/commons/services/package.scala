package com.fortysevendeg.ninecardslauncher.commons

import scala.language.{higherKinds, implicitConversions}
import scalaz._
import Scalaz._
import scalaz.concurrent.Task

package object services {

  type ServiceF[A, E <: Throwable, B] = A => Task[E \/ B]

  object Service {

    def apply[A, E <: Throwable, B](f: ServiceF[A, E, B]): ServiceF[A, E, B] = f

    implicit def toDisjunctionTask[E <: Throwable, A](f: EitherT[Task, E, A]): Task[E \/ A] = f.run

    implicit def toDisjunctionT[E <: Throwable, A](f: Task[E \/ A]): EitherT[Task, E, A] = EitherT.eitherT(f)

  }

}
