package com.fortysevendeg.ninecardslauncher.app.ui.commons

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import macroid.Ui
import macroid.FullDsl._

import scalaz.{\/-, -\/, \/}
import scalaz.concurrent.Task

object TasksOps {

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
