package cards.nine.app.ui.commons.dialogs.addmoment

import android.app.Dialog
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.types.theme.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher.R

class AddMomentFragment
  extends BaseActionFragment
  with AddMomentDOM
  with AddMomentUiActions
  with AddMomentListener
  with AppNineCardsIntentConversions { self =>

  lazy val momentJobs = new AddMomentJobs(self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = theme.get(CardLayoutBackgroundColor)

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    momentJobs.initialize().resolveAsync()
  }

  override def loadMoments(): Unit =
    momentJobs.loadMoments().resolveServiceOr(_ => showErrorLoadingCollectionInScreen())

  override def addMoment(moment: NineCardsMoment): Unit =
    momentJobs.addMoment(moment).resolveServiceOr(_ => showErrorSavingCollectionInScreen())
}



