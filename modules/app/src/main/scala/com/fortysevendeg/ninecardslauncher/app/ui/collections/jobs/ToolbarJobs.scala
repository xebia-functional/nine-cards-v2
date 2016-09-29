package com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs

import com.fortysevendeg.ninecardslauncher.app.commons.{Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import macroid.ActivityContextWrapper

class ToolbarJobs(actions: ToolbarUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
    with Conversions
    with NineCardIntentConversions { self =>

  def scrollY(dy: Int): TaskService[Unit] = actions.translationScrollY(dy)

  def scrollIdle(): TaskService[Unit] = actions.scrollIdle()

  def forceScrollType(scrollType: ScrollType): TaskService[Unit] = actions.forceScrollType(scrollType)

  def pullToClose(scroll: Int, scrollType: ScrollType, close: Boolean): TaskService[Unit] =
    actions.pullCloseScrollY(scroll, scrollType, close)

}
