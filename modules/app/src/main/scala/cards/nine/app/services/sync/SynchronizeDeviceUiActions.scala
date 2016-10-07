package cards.nine.app.services.sync

import android.app.Service
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, UiException}
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import macroid.Contexts

trait SynchronizeDeviceUiActions
  extends ImplicitsUiExceptions {

  self: SynchronizeDeviceListener with Contexts[Service] =>

  def endProcess: TaskService[Unit] = TaskService(CatchAll[UiException](processFinished()))

}



trait SynchronizeDeviceListener {

  def processFinished(): Unit

}
