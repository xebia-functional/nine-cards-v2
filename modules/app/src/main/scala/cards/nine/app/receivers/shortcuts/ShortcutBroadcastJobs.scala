package cards.nine.app.receivers.shortcuts

import android.content.{Context, Intent}
import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.action_filters.AppInstalledActionFilter
import cards.nine.app.ui.commons.{BroadAction, JobException, ShortcutJobs}
import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Card
import cats.implicits._
import macroid.ContextWrapper

class ShortcutBroadcastJobs(implicit contextWrapper: ContextWrapper)
  extends ShortcutJobs
  with Conversions {

  import ShortcutBroadcastReceiver._

  lazy val preferences = contextSupport.context.getSharedPreferences(shortcutBroadcastPreferences, Context.MODE_PRIVATE)

  def addShortcut(intent: Intent): TaskService[Unit] = {

    def readCollectionId: TaskService[Option[Int]] = TaskService[Option[Int]] {
      CatchAll[JobException] {
        preferences.getInt(collectionIdKey, 0) match {
          case n if n > 0 => Option(n)
          case _ => None
        }
      }
    }

    def shortcutAdded(collectionId: Int, card: Card): TaskService[Unit] =
      for {
        _ <- di.trackEventProcess.addShortcutFromReceiver(card.term)
        _ <- sendBroadCastTask(BroadAction(AppInstalledActionFilter.action)).resolveLeftTo((): Unit)
      } yield ()

    def addShortcut(collectionId: Int): TaskService[Unit] =
      for {
        maybeCard <- addNewShortcut(collectionId, intent)
        _ <- maybeCard match {
          case Some(card) => shortcutAdded(collectionId, card)
          case _ => TaskService.empty
        }
      } yield ()

    for {
      maybeId <- readCollectionId
      _ <- maybeId match {
        case Some(id) => addShortcut(id)
        case _ => TaskService.empty
      }
    } yield ()
  }

}
