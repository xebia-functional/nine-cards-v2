package cards.nine.app.services

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ContextWrapper

class NineCardsFirebaseJobs(implicit contextWrapper: ContextWrapper)
  extends Jobs {

  def updateDeviceToken(): TaskService[Unit] =
    for {
      token <- di.externalServicesProcess.readFirebaseToken
      _ <- di.userProcess.updateDeviceToken(token)
    } yield ()



}
