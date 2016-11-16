package cards.nine.app.ui.preferences

import android.content.Intent
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.dialogs.wizard.WizardInlinePreferences
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ActivityContextWrapper

class PreferencesJobs(ui: PreferencesUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with ImplicitsUiExceptions {

  var statuses = PreferencesJobsStatuses()

  lazy val wizardInlinePreferences = new WizardInlinePreferences()

  def initialize(): TaskService[Unit] = ui.initialize()

  def initializeActionBarTitle(): TaskService[Unit] = ui.setActionBarTitle()

  def preferenceChanged(preferenceName: String): TaskService[Unit] = {
    statuses = statuses.copy(changedPreferences = statuses.changedPreferences + preferenceName)
    val data = new Intent()
    data.putExtra(ResultData.preferencesResultData, statuses.changedPreferences.toArray)
    ui.setActivityResult(ResultCodes.preferencesChanged, data)
  }

  def cleanWizardInlinePreferences(): TaskService[Unit] =
    for {
      _ <- TaskService.right(wizardInlinePreferences.clean())
      _ <- ui.showWizardInlineCleaned()
    } yield ()

}

case class PreferencesJobsStatuses(changedPreferences: Set[String] = Set.empty)
