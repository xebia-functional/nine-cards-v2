package cards.nine.app.ui.applinks

import android.net.Uri
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.app.commons.Conversions
import cards.nine.app.ui.collections.tasks.CollectionJobs
import cards.nine.app.ui.commons.action_filters.CollectionAddedActionFilter
import cards.nine.app.ui.commons.{BroadAction, ImplicitsUiExceptions, Jobs}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.process.sharedcollections.models.SharedCollection
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

class AppLinksReceiverJobs(actions: AppLinksReceiverUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with CollectionJobs
  with ImplicitsUiExceptions {

  implicit lazy val theme: NineCardsTheme = getTheme

  def uriReceived(uri: Uri): TaskService[Unit] = {

    def safeExtractPath: Option[String] = Option(uri) flatMap (u => Option(u.getPath))

    val CollectionsPathRegex = "\\/collections\\/id\\/(.+)".r

    def openInBrowser(uri: Uri): TaskService[Unit] =
      for {
        _ <- actions.showLinkNotSupportedMessage()
        _ <- di.launcherExecutorProcess.launchUrl(uri.toString)
        _ <- actions.exit()
      } yield ()

    (safeExtractPath, Option(uri)) match {
      case (Some(CollectionsPathRegex(id)), _) =>
        for {
          _ <- actions.initializeView()
          sharedCollection <- di.sharedCollectionsProcess.getSharedCollection(id)
          _ <- actions.showCollection(this, sharedCollection)
        } yield ()
      case (_, Some(link)) =>
        openInBrowser(link)
      case (_, None) => actions.exit()
    }

  }

  def addCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    for {
      col <- addSharedCollection(sharedCollection)
      _ <- sendBroadCastTask(BroadAction(CollectionAddedActionFilter.action, Some(col.id.toString))).resolveLeftTo((): Unit)
      _ <- actions.exit()
    } yield ()

  def showError(): TaskService[Unit] =
    for {
      _ <- actions.showUnexpectedErrorMessage()
      _ <- actions.exit()
    } yield ()

  def shareCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    for {
      _ <- di.launcherExecutorProcess.launchShare(resGetString(R.string.shared_collection_url, sharedCollection.id))
      _ <- actions.exit()
    } yield ()

}
