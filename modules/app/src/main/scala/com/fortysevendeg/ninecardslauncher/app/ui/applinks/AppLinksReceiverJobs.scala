package com.fortysevendeg.ninecardslauncher.app.ui.applinks

import android.net.Uri
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ImplicitsUiExceptions, Jobs}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import macroid.ActivityContextWrapper

import scalaz.concurrent.Task

class AppLinksReceiverJobs(actions: AppLinksReceiverUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with ImplicitsUiExceptions {

  def uriReceived(uri: Uri): TaskService[Unit] = {

    def safeExtractPath: Option[String] = Option(uri) flatMap (u => Option(u.getPath))

    val CollectionsPathRegex = "\\/collections\\/id\\/(.+)".r

    (safeExtractPath, Option(uri)) match {
      case (Some(CollectionsPathRegex(id)), _) =>
        // TODO - Load public collections and show it on screen
        TaskService(Task(Xor.right((): Unit)))
      case (_, Some(link)) =>
        // TODO - Open uri on browser by adding a new action to launcher intent process
        TaskService(Task(Xor.right((): Unit)))
        for {
          _ <- actions.showLinkNotSupportedMessage()
          _ <- actions.exit()
        } yield ()
      case (_, None) => actions.exit()
    }

  }


}
