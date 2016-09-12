package com.fortysevendeg.ninecardslauncher.app.ui.applinks

import android.net.Uri
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.ui.collections.tasks.CollectionJobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ImplicitsUiExceptions, Jobs}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
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
    addSharedCollection(sharedCollection).map(_ => ()).recoverWith {
      case e => actions.showUnexpectedErrorMessage()
    }

  def shareCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    di.launcherExecutorProcess.launchShare(resGetString(R.string.shared_collection_url, sharedCollection.id))
      .map(_ => ())
      .recoverWith {
        case e => actions.showUnexpectedErrorMessage()
      }

}
