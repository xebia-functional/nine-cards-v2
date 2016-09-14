package com.fortysevendeg.ninecardslauncher.app.ui.commons.ops

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.{NineCardException, TaskService}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import macroid.Ui
import monix.eval.Task
import cats.syntax.either._
import monix.execution.Scheduler.Implicits.global

import scala.util.{Either, Failure, Success}

object TaskServiceOps {

  implicit class TaskServiceUi[A](t: TaskService[A]) {

    // Legacy code. We should remove these 4 methods when we change all presenters to jobs

    def resolveAsync2[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => (),
      onPreTask: () => Unit = () => ()
    ): Unit = {
      onPreTask()
      Task.fork(t.value).runAsync {
        r => r match {
          case Failure(ex) =>
            printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
            onException(ex)
          case Success(Right(response)) => onResult(response)
          case Success(Left(e)) =>
            printErrorTaskMessage(s"=> EXCEPTION Xor ", e)
            onException(e)
        }
      }
    }

    def resolveAsyncUi2[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop,
      onPreTask: () => Ui[_] = () => Ui.nop): Unit = {
      onPreTask().run
      Task.fork(t.value).runAsync {
        r => r match {
          case Failure(ex) =>
            printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
            onException(ex).run
          case Success(Right(response)) => onResult(response).run
          case Success(Left(e)) =>
            printErrorTaskMessage(s"=> EXCEPTION Xor <=", e)
            onException(e).run
        }
      }
    }

    def resolve2[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => ()): Unit = {
      t.value.map {
        case Right(response) => onResult(response)
        case Left(e) =>
          printErrorTaskMessage(s"=> EXCEPTION Xor <=", e)
          onException(e)
      }.coeval.runAttempt
    }

    def resolveUi2[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop): Unit = {
      t.value.map {
        case Right(response) => onResult(response).run
        case Left(e) =>
          printErrorTaskMessage(s"=> EXCEPTION Xor <=", e)
          onException(e).run
      }.coeval.runAttempt
    }


    def resolveAsync[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => ()
    ): Unit = {
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

    def resolveAsyncService[E >: Throwable](
      onResult: (A) => TaskService[A] = a => TaskService(Task(Either.right(a))),
      onException: (E) => TaskService[A] = (e: NineCardException) => TaskService(Task(Either.left(e)))): Unit = {
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
      onException: (E) => TaskService[A] = (e: NineCardException) => TaskService(Task(Either.left(e)))): Unit = {
      Task.fork(t.value).map {
        case Right(response) => onResult(response).value.coeval.runAttempt
        case Left(ex) =>
          printErrorTaskMessage("=> EXCEPTION Left <=", ex)
          onException(ex).value.coeval.runAttempt
      }.coeval.runAttempt
    }

    def resolveServiceOr[E >: Throwable](exception: (E) => TaskService[A]) = resolveService(onException = exception)

    def resolveAsyncServiceOr[E >: Throwable](exception: (E) => TaskService[A]) = resolveAsyncService(onException = exception)

  }

}
