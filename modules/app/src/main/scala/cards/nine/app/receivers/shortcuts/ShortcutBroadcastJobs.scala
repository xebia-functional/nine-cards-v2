package cards.nine.app.receivers.shortcuts

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cats.implicits._
import macroid.ContextWrapper
import monix.eval.Task

class ShortcutBroadcastJobs(implicit contextWrapper: ContextWrapper)
  extends Jobs
  with Conversions {



}
