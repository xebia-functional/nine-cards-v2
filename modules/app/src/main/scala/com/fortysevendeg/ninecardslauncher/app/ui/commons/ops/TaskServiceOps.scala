package com.fortysevendeg.ninecardslauncher.app.ui.commons.ops

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.{NineCardException, TaskService}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

object TaskServiceOps {

  implicit class TaskServiceUi[A](t: TaskService[A]) {

    def resolveAsync[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => ()
    ): Unit = {
      Task.fork(t.value).runAsync {
        case -\/(ex) =>
          printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
          onException(ex)
        case \/-(Xor.Right(response)) => onResult(response)
        case \/-(Xor.Left(ex)) =>
          printErrorTaskMessage(s"=> EXCEPTION Xor Left) <=", ex)
          onException(ex)
      }
    }

    def resolveAsyncService[E >: Throwable](
      onResult: (A) => TaskService[A] = a => TaskService(Task(Xor.Right(a))),
      onException: (E) => TaskService[A] = (e: NineCardException) => TaskService(Task(Xor.Left(e)))): Unit = {
      Task.fork(t.value).runAsync {
        case -\/(ex) =>
          printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
          onException(ex).value.run
        case \/-(Xor.Right(response)) => onResult(response).value.run
        case \/-(Xor.Left(ex)) =>
          printErrorTaskMessage(s"=> EXCEPTION Xor Left) <=", ex)
          onException(ex).value.run
      }
    }

    def resolve[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => ()): Unit = {
      Task.fork(t.value).map {
        case Xor.Right(response) => onResult(response)
        case Xor.Left(ex) =>
          printErrorTaskMessage("=> EXCEPTION Xor Left <=", ex)
          onException(ex)
      }.attemptRun
    }

    def resolveService[E >: Throwable](
      onResult: (A) => TaskService[A] = a => TaskService(Task(Xor.Right(a))),
      onException: (E) => TaskService[A] = (e: NineCardException) => TaskService(Task(Xor.Left(e)))): Unit = {
      Task.fork(t.value).map {
        case Xor.Right(response) => onResult(response).value.run
        case Xor.Left(ex) =>
          printErrorTaskMessage("=> EXCEPTION Xor Left <=", ex)
          onException(ex).value.run
      }.attemptRun
    }

    def resolveServiceOr[E >: Throwable](exception: (E) => TaskService[A]) = resolveService(onException = exception)

    def resolveAsyncServiceOr[E >: Throwable](exception: (E) => TaskService[A]) = resolveAsyncService(onException = exception)

  }

}
