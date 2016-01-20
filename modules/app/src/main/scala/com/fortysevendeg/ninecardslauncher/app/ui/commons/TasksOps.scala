package com.fortysevendeg.ninecardslauncher.app.ui.commons

import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import macroid.FullDsl._
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
      runUi(onPreTask())
      t.runAsync {
        case -\/(ex) =>
          printErrorTaskMessage("=> EXCEPTION Disjunction <=", Seq(ex))
          runUi(onException(ex))
        case \/-(Answer(response)) => runUi(onResult(response))
        case \/-(e@Errata(_)) =>
          printErrorTaskMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach (ex => runUi(onException(ex)))
        case \/-(Unforeseen(ex)) =>
          printErrorTaskMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          runUi(onException(ex))
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
        case Answer(response) => runUi(onResult(response))
        case e@Errata(_) =>
          printErrorTaskMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach (ex => runUi(onException(ex)))
        case Unforeseen(ex) =>
          printErrorTaskMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          runUi(onException(ex))
      }.attemptRun
    }

  }

}
