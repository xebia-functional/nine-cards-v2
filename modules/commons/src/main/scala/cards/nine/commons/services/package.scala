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
