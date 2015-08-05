package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.util.Log
import macroid.Ui
import macroid.FullDsl._
import rapture.core.{Unforeseen, Errata, Answer, Result}

import scalaz.{\/-, -\/, \/}
import scalaz.concurrent.Task

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
          printMessage("=> EXCEPTION Disjunction <=", Seq(ex))
          onException(ex)
        case \/-(Answer(response)) => onResult(response)
        case \/-(e@Errata(_)) =>
          printMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach onException
        case \/-(Unforeseen(ex)) =>
          printMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
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
          printMessage("=> EXCEPTION Disjunction <=", Seq(ex))
          runUi(onException(ex))
        case \/-(Answer(response)) => runUi(onResult(response))
        case \/-(e@Errata(_)) =>
          printMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach (ex => runUi(onException(ex)))
        case \/-(Unforeseen(ex)) =>
          printMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          runUi(onException(ex))
      }
    }

    def resolve[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => ()): Unit = {
      t.map {
        case Answer(response) => onResult(response)
        case e@Errata(_) =>
          printMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach onException
        case Unforeseen(ex) =>
          printMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          onException(ex)
      }.attemptRun
    }

    def resolveUi[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop): Unit = {
      t.map {
        case Answer(response) => runUi(onResult(response))
        case e@Errata(_) =>
          printMessage(s"=> EXCEPTION Errata (${e.exceptions.length}) <=", e.exceptions)
          e.exceptions foreach (ex => runUi(onException(ex)))
        case Unforeseen(ex) =>
          printMessage("=> EXCEPTION Unforeseen <=", Seq(ex))
          runUi(onException(ex))
      }.attemptRun
    }

    private[this] def printMessage(header: String, exs: Seq[Throwable]) = {
      Log.d(tag, header)
      exs foreach (ex => Log.d(tag, Option(ex.getMessage) getOrElse ex.getClass.toString))
    }

  }

}
