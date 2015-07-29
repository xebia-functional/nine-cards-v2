package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.util.Log
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
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
        case -\/(ex) => {
          val message = Option(ex.getMessage) getOrElse ex.getClass.toString
          Log.d(tag, "=> EXCEPTION Disjunction <=")
          Log.d(tag, message)
          ex.printStackTrace()
          onException(ex)
        }
        case \/-(Answer(response)) => onResult(response)
        case \/-(e @ Errata(_)) => {
          Log.d(tag, s"=> EXCEPTION Errata (${e.exceptions.length}) <=")
          e.exceptions foreach {
            ex =>
              val message = Option(ex.getMessage) getOrElse ex.getClass.toString
              Log.d(tag, message)
              onException(ex)
          }
        }
        case \/-(Unforeseen(ex)) => {
          val message = Option(ex.getMessage) getOrElse ex.getClass.toString
          Log.d(tag, "=> EXCEPTION Unforeseen <=")
          Log.d(tag, message)
          onException(ex)
        }
      }
    }

  }

  implicit class TaskUI[E <: NineCardsException, A](t: Task[E \/ A]) {

    def resolveAsync[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => (),
      onPreTask: () => Unit = () => ()
      ): Unit = {
      onPreTask()
      t.runAsync {
        case -\/(ex) => onException(ex)
        case \/-(\/-(response)) => onResult(response)
        case \/-(-\/(ex)) => onException(ex)
      }
    }

    def resolveAsyncUi[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop,
      onPreTask: () => Ui[_] = () => Ui.nop
      ): Unit = {
      runUi(onPreTask())
      t.runAsync {
        case -\/(ex) => runUi(onException(ex))
        case \/-(\/-(response)) => runUi(onResult(response))
        case \/-(-\/(ex)) => runUi(onException(ex))
      }
    }

    def resolve[E >: Throwable](
      onResult: A => Unit = a => (),
      onException: E => Unit = (e: Throwable) => ()
      ): Unit = {
      t.map {
        case \/-(response) => onResult(response)
        case -\/(ex) => onException(ex)
      }.attemptRun
    }

    def resolveUi[E >: Throwable](
      onResult: (A) => Ui[_] = a => Ui.nop,
      onException: (E) => Ui[_] = (e: Throwable) => Ui.nop
      ): Unit = {
      t.map {
        case \/-(response) => runUi(onResult(response))
        case -\/(ex) => runUi(onException(ex))
      }.attemptRun
    }

  }

}
