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

package cards.nine.app.ui.commons.ops

import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{NineCardException, TaskService}
import cards.nine.app.ui.commons.AppLog._
import monix.eval.Task
import cats.syntax.either._
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Either, Failure, Success}

object TaskServiceOps {

  implicit class TaskServiceUi[A](t: TaskService[A]) {

    // TODO - Do not use, it's only used in the `getTheme`. Remove as part of #808
    def resolveNow: Either[NineCardException, A] =
      Await.result(t.value.runAsync, 10.seconds)

    def resolveAsync[E >: Throwable](
        onResult: A => Unit = a => (),
        onException: E => Unit = (e: Throwable) => ()
    ): Cancelable = {
      Task.fork(t.value).runAsync { result =>
        result match {
          case Failure(ex) =>
            printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
            onException(ex)
          case Success(Right(value)) => onResult(value)
          case Success(Left(ex)) =>
            printErrorTaskMessage(s"=> EXCEPTION Left) <=", ex)
            onException(ex)
        }
      }
    }

    def resolveAsyncDelayed[E >: Throwable](
        finiteDuration: FiniteDuration,
        onResult: A => Unit = a => (),
        onException: E => Unit = (e: Throwable) => ()
    ): Cancelable = {
      Task.fork(t.value.delayExecution(finiteDuration)).runAsync { result =>
        result match {
          case Failure(ex) =>
            printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
            onException(ex)
          case Success(Right(value)) => onResult(value)
          case Success(Left(ex)) =>
            printErrorTaskMessage(s"=> EXCEPTION Left) <=", ex)
            onException(ex)
        }
      }
    }

    def resolveAutoCancelableAsyncDelayed[E >: Throwable](
        finiteDuration: FiniteDuration,
        onResult: A => Unit = a => (),
        onException: E => Unit = (e: Throwable) => ()
    ): Cancelable = {
      Task
        .defer(t.value)
        .executeWithOptions(_.enableAutoCancelableRunLoops)
        .delayExecution(finiteDuration)
        .runAsync { result =>
          result match {
            case Failure(ex) =>
              printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
              onException(ex)
            case Success(Right(value)) => onResult(value)
            case Success(Left(ex)) =>
              printErrorTaskMessage(s"=> EXCEPTION Left) <=", ex)
              onException(ex)
          }
        }
    }

    def resolveAsyncService[E >: Throwable](
        onResult: (A) => TaskService[A] = a => TaskService(Task(Either.right(a))),
        onException: (E) => TaskService[A] = (e: NineCardException) =>
          TaskService(Task(Either.left(e)))): Cancelable = {
      Task.fork(t.value).runAsync { result =>
        result match {
          case Failure(ex) =>
            printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
            onException(ex).value.runAsync
          case Success(Right(response)) => onResult(response).value.coeval
          case Success(Left(ex)) =>
            printErrorTaskMessage(s"=> EXCEPTION Left) <=", ex)
            onException(ex).value.runAsync
        }
      }
    }

    def resolveAutoCancelableAsyncService[E >: Throwable](
        onResult: (A) => TaskService[A] = a => TaskService(Task(Either.right(a))),
        onException: (E) => TaskService[A] = (e: NineCardException) =>
          TaskService(Task(Either.left(e)))): Cancelable = {
      Task.defer(t.value).executeWithOptions(_.enableAutoCancelableRunLoops).runAsync { result =>
        result match {
          case Failure(ex) =>
            printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
            onException(ex).value.runAsync
          case Success(Right(response)) => onResult(response).value.coeval
          case Success(Left(ex)) =>
            printErrorTaskMessage(s"=> EXCEPTION Left) <=", ex)
            onException(ex).value.runAsync
        }
      }
    }

    def resolve[E >: Throwable](
        onResult: A => Unit = a => (),
        onException: E => Unit = (e: Throwable) => ()): Unit = {
      t.value.map {
        case Right(response) => onResult(response)
        case Left(ex) =>
          printErrorTaskMessage("=> EXCEPTION Left <=", ex)
          onException(ex)
      }.coeval.runAttempt
    }

    def resolveService[E >: Throwable](
        onResult: (A) => TaskService[A] = a => TaskService(Task(Either.right(a))),
        onException: (E) => TaskService[A] = (e: NineCardException) =>
          TaskService(Task(Either.left(e)))): Unit = {
      Task
        .fork(t.value)
        .map {
          case Right(response) => onResult(response).value.coeval.runAttempt
          case Left(ex) =>
            printErrorTaskMessage("=> EXCEPTION Left <=", ex)
            onException(ex).value.coeval.runAttempt
        }
        .coeval
        .runAttempt
    }

    def resolveServiceOr[E >: Throwable](exception: (E) => TaskService[A]) =
      resolveService(onException = exception)

    def resolveAsyncServiceOr[E >: Throwable](exception: (E) => TaskService[A]): Cancelable =
      resolveAsyncService(onException = exception)

    def resolveAutoCancelableAsyncServiceOr[E >: Throwable](
        exception: (E) => TaskService[A]): Cancelable =
      resolveAutoCancelableAsyncService(onException = exception)

  }

}
