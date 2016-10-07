package cards.nine.app.ui.commons

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.commons.BroadcastDispatcher._
import cards.nine.app.di.{Injector, InjectorImpl}
import cards.nine.app.ui.commons.AppUtils._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.preferences.commons.{NineCardsPreferencesValue, Theme}
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.theme.models.NineCardsTheme
import macroid.ContextWrapper


class Jobs(implicit contextWrapper: ContextWrapper)
  extends ContextSupportProvider
  with ImplicitsJobExceptions {

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
  def sendBroadCast(broadAction: BroadAction): Unit = sendBroadCast(commandType, broadAction)

  def sendBroadCastTask(broadAction: BroadAction): TaskService[Unit] =
    TaskService(CatchAll[JobException](sendBroadCast(commandType, broadAction)))

  def askBroadCastTask(broadAction: BroadAction): TaskService[Unit] =
    TaskService(CatchAll[JobException](sendBroadCast(questionType, broadAction)))

  private[this] def sendBroadCast(
    broadCastKeyType: String,
    broadAction: BroadAction): Unit = {
    val intent = new Intent(broadAction.action)
    intent.putExtra(keyType, broadCastKeyType)
    broadAction.command foreach (d => intent.putExtra(keyCommand, d))
    contextWrapper.bestAvailable.sendBroadcast(intent)
  }

  def withActivity(f: (AppCompatActivity => Unit)): TaskService[Unit] =
    contextWrapper.original.get match {
      case Some(activity: AppCompatActivity) => TaskService(CatchAll[JobException](f(activity)))
      case _ => TaskService.empty
    }

}

case class BroadAction(action: String, command: Option[String] = None)
