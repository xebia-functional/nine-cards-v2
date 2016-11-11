package cards.nine.app.ui.preferences

import android.content.Intent
import macroid.extras.UIActionsExtras._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ActivityContextWrapper, Ui}

class PreferencesUiActions(dom: PreferencesDOM)(implicit contextWrapper: ActivityContextWrapper) {

  def initialize(): TaskService[Unit] = Ui {
    dom.actionBar foreach { ab =>
      ab.setDisplayHomeAsUpEnabled(true)
      ab.setDisplayShowHomeEnabled(false)
      ab.setDisplayShowTitleEnabled(true)
      ab.setDisplayUseLogoEnabled(false)
    }
  }.toService

  def setActionBarTitle(): TaskService[Unit] =
    Ui(dom.actionBar foreach (_.setTitle(R.string.nineCardsSettingsTitle))).toService

  def setActivityResult(resultCode: Int, data: Intent): TaskService[Unit] =
    Ui(contextWrapper.original.get foreach (_.setResult(resultCode, data))).toService

  def showContactUsError(): TaskService[Unit] =
    uiShortToast(R.string.contactUsError).toService

}
