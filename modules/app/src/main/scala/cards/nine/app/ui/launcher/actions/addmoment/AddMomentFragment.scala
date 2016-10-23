package cards.nine.app.ui.launcher.actions.addmoment

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.types.NineCardsMoment
import cards.nine.process.theme.models.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher.R

class AddMomentFragment
  extends BaseActionFragment
  with AddMomentDOM
  with AddMomentUiActions
  with AddMomentListener
  with AppNineCardsIntentConversions { self =>

  lazy val momentJobs = new AddMomentJobs(self)

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = theme.get(CardLayoutBackgroundColor)

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    momentJobs.initialize().resolveAsync()
  }

  override def loadMoments(): Unit =
    momentJobs.loadMoments().resolveServiceOr(_ => showErrorLoadingCollectionInScreen())

  override def addMoment(moment: NineCardsMoment): Unit =
    momentJobs.addMoment(moment).resolveServiceOr(_ => showErrorSavingCollectionInScreen())
}



