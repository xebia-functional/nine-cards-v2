package cards.nine.app.ui.launcher.actions.publicollections

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardIntentConversions
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher2.R

class PublicCollectionsFragment(implicit lPresenter: LauncherPresenter)
  extends BaseActionFragment
  with PublicCollectionsActionsImpl
  with AppNineCardIntentConversions { self =>

  override lazy val collectionPresenter: PublicCollectionsPresenter = new PublicCollectionsPresenter(self)

  lazy val launcherPresenter = lPresenter

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = loadBackgroundColor

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    collectionPresenter.initialize()
  }
}




