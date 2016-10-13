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

  def getThemeTask: TaskService[NineCardsTheme] =
    di.themeProcess.getTheme(Theme.getThemeFile(preferenceValues))

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

  def withActivityTask(f: (AppCompatActivity => Unit)): TaskService[Unit] =
    withActivity(activity => TaskService(CatchAll[JobException](f(activity))))

  def withActivity(f: (AppCompatActivity => TaskService[Unit])): TaskService[Unit] =
    contextWrapper.original.get match {
      case Some(activity: AppCompatActivity) => f(activity)
      case _ => TaskService.empty
    }

  def readIntValue(i: Intent, key: String): Option[Int] =
    if (i.hasExtra(key)) Option(i.getIntExtra(key, 0)) else None

  def readStringValue(i: Intent, key: String): Option[String] =
    if (i.hasExtra(key)) Option(i.getStringExtra(key)) else None

  def readArrayValue(i: Intent, key: String): Option[Array[String]] =
    if (i.hasExtra(key)) Option(i.getStringArrayExtra(key)) else None

}

case class BroadAction(action: String, command: Option[String] = None)
