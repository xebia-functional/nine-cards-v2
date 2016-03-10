package com.fortysevendeg.ninecardslauncher.app.ui.commons

import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import macroid.Ui
import rapture.core.{Answer, Errata, Result, Unforeseen}

import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

object TasksOps {

  implicit class TaskResultUI[A, E <: Exception](t: Task[Result[A, E]]) {

    val tag = "9cards"

    def resolveAsync[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => (),
      onPreTask: () => Unit = () => ()
      ): Unit = {
      onPreTask()
      t.runAsync {
        case -\/(ex) =>
          printErrorTaskMessage("=> EXCEPTION Disjunction <=", Seq(ex))
          onException(ex)
        case \/-(Answer(response)) => onResult(response)
        case \/-(e@Errata(_)) =>
          printErrorTaskMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach onException
        case \/-(Unforeseen(ex)) =>
          printErrorTaskMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          onException(ex)
      }
    }

    def resolveAsyncUi[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop,
      onPreTask: () => Ui[_] = () => Ui.nop): Unit = {
      onPreTask().run
      t.runAsync {
        case -\/(ex) =>
          printErrorTaskMessage("=> EXCEPTION Disjunction <=", Seq(ex))
          onException(ex).run
        case \/-(Answer(response)) => onResult(response).run
        case \/-(e@Errata(_)) =>
          printErrorTaskMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach (ex => onException(ex).run)
        case \/-(Unforeseen(ex)) =>
          printErrorTaskMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          onException(ex).run
      }
    }

    def resolve[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => ()): Unit = {
      t.map {
        case Answer(response) => onResult(response)
        case e@Errata(_) =>
          printErrorTaskMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach onException
        case Unforeseen(ex) =>
          printErrorTaskMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          onException(ex)
      }.attemptRun
    }

    def resolveUi[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop): Unit = {
      t.map {
        case Answer(response) => onResult(response).run
        case e@Errata(_) =>
          printErrorTaskMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach (ex => onException(ex).run)
        case Unforeseen(ex) =>
          printErrorTaskMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          onException(ex).run
      }.attemptRun
    }

  }

}
