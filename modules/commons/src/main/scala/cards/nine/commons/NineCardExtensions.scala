/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.commons

import cats.data.EitherT
import cards.nine.commons.services.TaskService._
import monix.eval.Task
import cats.syntax.either._

import scala.language.implicitConversions

object NineCardExtensions {

  implicit class EitherTExtensions[A](r: EitherT[Task, NineCardException, A]) {

    def resolve[E <: NineCardException](
        implicit converter: Throwable => E): EitherT[Task, NineCardException, A] =
      resolveLeft(e => Left(converter(e)))

    def resolveIf[E <: NineCardException](
        condition: Boolean,
        ifNot: A): EitherT[Task, NineCardException, A] =
      if (condition) {
        r
      } else {
        EitherT(Task(Either.right(ifNot)))
      }

    def resolveLeftTo(result: A): EitherT[Task, NineCardException, A] =
      resolveLeft((_) => Right(result))

    def resolveLeft(mapLeft: NineCardException => Either[NineCardException, A]): EitherT[
      Task,
      NineCardException,
      A] =
      resolveSides((r) => Right(r), mapLeft)

    def resolveRight[B](
        mapRight: (A) => Either[NineCardException, B]): EitherT[Task, NineCardException, B] =
      resolveSides(mapRight, (e) => Left(e))

    def resolveAsOption: EitherT[Task, NineCardException, Option[A]] =
      r.map(result => Option(result)).resolveLeftTo(None)

    def resolveSides[B](
        mapRight: (A) => Either[NineCardException, B],
        mapLeft: NineCardException => Either[NineCardException, B] = (e: NineCardException) =>
          Left(e)): EitherT[Task, NineCardException, B] = {
      val task: Task[Either[NineCardException, A]] = r.value
      val innerResult: Task[NineCardException Either B] = task.map {
        case Right(v) => mapRight(v)
        case Left(e)  => mapLeft(e)
      }
      EitherT(innerResult)
    }

  }

  implicit class EitherTOptionExtensions[A](r: EitherT[Task, NineCardException, Option[A]]) {

    case class EmptyException(message: String, cause: Option[Throwable] = None)
        extends RuntimeException(message)
        with NineCardException {
      cause map initCause
    }

    def resolveOption(message: String): EitherT[Task, NineCardException, A] =
      resolveOption(Option(message))

    def resolveOption(message: Option[String] = None): EitherT[Task, NineCardException, A] =
      r.resolveRight {
        case Some(v) => Right(v)
        case None    => Left(EmptyException(message getOrElse "Value not found"))
      }

  }

}
