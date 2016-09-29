package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.commons.BroadcastDispatcher._
import com.fortysevendeg.ninecardslauncher.app.commons._
import com.fortysevendeg.ninecardslauncher.app.di.{Injector, InjectorImpl}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.{NineCardsPreferencesValue, Theme}
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.theme.models.NineCardsTheme
import macroid.ContextWrapper
import monix.eval.Task


class Jobs(implicit contextWrapper: ContextWrapper)
  extends ContextSupportProvider
  with ImplicitsUiExceptions {

  implicit lazy val di: Injector = new InjectorImpl

  lazy val preferenceValues = new NineCardsPreferencesValue

  @deprecated
  def getTheme: NineCardsTheme =
    di.themeProcess.getTheme(Theme.getThemeFile(preferenceValues)).resolveNow match {
      case Right(t) => t
      case Left(e) =>
        AppLog.printErrorMessage(e)
        getDefaultTheme
    }

  @deprecated
  def sendBroadCast(broadAction: BroadAction) = {
    val intent = new Intent(broadAction.action)
    intent.putExtra(keyType, commandType)
    broadAction.command foreach (d => intent.putExtra(keyCommand, d))
    contextWrapper.bestAvailable.sendBroadcast(intent)
  }

  def sendBroadCastTask(broadAction: BroadAction): TaskService[Unit] = TaskService {
      CatchAll[UiException](sendBroadCast(broadAction))
  }

  def withActivity(f: (AppCompatActivity => TaskService[Unit])) =
    contextWrapper.original.get match {
      case Some(activity: AppCompatActivity) => f(activity)
      case _ => TaskService(Task(Right((): Unit)))
    }

}
