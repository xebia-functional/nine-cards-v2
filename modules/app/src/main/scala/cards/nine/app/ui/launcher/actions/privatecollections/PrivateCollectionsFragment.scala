package cards.nine.app.ui.launcher.actions.privatecollections

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.launcher.LauncherPresenter
import cards.nine.models.{Collection, CollectionData}
import cards.nine.process.theme.models.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher.R

class PrivateCollectionsFragment(implicit launcherPresenter: LauncherPresenter)
  extends BaseActionFragment
  with PrivateCollectionsDOM
  with PrivateCollectionsUiActions
  with PrivateCollectionsListener
  with AppNineCardsIntentConversions { self =>

  lazy val collectionJobs = new PrivateCollectionsJobs(self)

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = theme.get(CardLayoutBackgroundColor)

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    collectionJobs.initialize().resolveAsync()
  }

  override def loadPrivateCollections(): Unit =
    collectionJobs.loadPrivateCollections().resolveServiceOr(_ => showErrorLoadingCollectionInScreen())

  override def addLauncherCollection(collection: Collection): Unit = launcherPresenter.addCollection(collection)

  override def saveCollection(collection: CollectionData): Unit =
    collectionJobs.saveCollection(collection).resolveServiceOr(_ => showErrorSavingCollectionInScreen)
}



