package cards.nine.app.ui.commons.dialogs.privatecollections

import android.app.Dialog
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.jobs.LauncherJobs
import cards.nine.commons.services.TaskService._
import cards.nine.models.CollectionData
import cards.nine.models.types.theme.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher.R

class PrivateCollectionsFragment(implicit launcherJobs: LauncherJobs)
    extends BaseActionFragment
    with PrivateCollectionsDOM
    with PrivateCollectionsUiActions
    with PrivateCollectionsListener
    with AppNineCardsIntentConversions { self =>

  lazy val collectionJobs = new PrivateCollectionsJobs(self)

  lazy val packages =
    getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int =
    theme.get(CardLayoutBackgroundColor)

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    collectionJobs.initialize().resolveAsync()
  }

  override def loadPrivateCollections(): Unit =
    collectionJobs
      .loadPrivateCollections()
      .resolveServiceOr(_ => showErrorLoadingCollectionInScreen())

  override def saveCollection(collection: CollectionData): Unit = {
    (for {
      collectionAdded <- collectionJobs.saveCollection(collection)
      _               <- launcherJobs.addCollection(collectionAdded)
    } yield ()).resolveServiceOr(_ => showErrorSavingCollectionInScreen())
  }
}
