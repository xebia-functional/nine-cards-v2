package com.fortysevendeg.ninecardslauncher.app.ui.commons.ops

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import macroid.Ui

import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

object TasksOps {

  implicit class TaskResultUI[A, E <: Exception](t: Task[Xor[E,A]]) {

    val tag = AppLog.tag

    def resolveAsync[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => (),
      onPreTask: () => Unit = () => ()
      ): Unit = {
      onPreTask()
      t.runAsync {
        case -\/(ex) =>
          printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
          onException(ex)
        case \/-(Xor.Right(response)) => onResult(response)
        case \/-(Xor.Left(e)) =>
          printErrorTaskMessage(s"=> EXCEPTION Xor ", e)
          onException(e)
      }
    }

    def resolveAsyncUi[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop,
      onPreTask: () => Ui[_] = () => Ui.nop): Unit = {
      onPreTask().run
      t.runAsync {
        case -\/(ex) =>
          printErrorTaskMessage("=> EXCEPTION Disjunction <=", ex)
          onException(ex).run
        case \/-(Xor.Right(response)) => onResult(response).run
        case \/-(Xor.Left(e)) =>
          printErrorTaskMessage(s"=> EXCEPTION Xor <=", e)
          onException(e).run
      }
    }

    def resolve[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => ()): Unit = {
      t.map {
        case Xor.Right(response) => onResult(response)
        case Xor.Left(e) =>
          printErrorTaskMessage(s"=> EXCEPTION Xor <=", e)
          onException(e)
      }.attemptRun
    }

    def resolveUi[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop): Unit = {
      t.map {
        case Xor.Right(response) => onResult(response).run
        case Xor.Left(e) =>
          printErrorTaskMessage(s"=> EXCEPTION Xor <=", e)
          onException(e).run
      }.attemptRun
    }

  }

}
