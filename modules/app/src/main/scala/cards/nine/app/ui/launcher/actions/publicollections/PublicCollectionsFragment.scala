package cards.nine.app.ui.launcher.actions.publicollections

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardIntentConversions
import cards.nine.app.ui.commons.AppLog
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.launcher.{LauncherActivity, LauncherPresenter}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.types.{Communication, NineCardCategory}
import cards.nine.process.commons.models.Collection
import cards.nine.process.sharedcollections.models.SharedCollection
import cards.nine.process.sharedcollections.{SharedCollectionsConfigurationException, TopSharedCollection, TypeSharedCollection}
import cards.nine.process.theme.models.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

class PublicCollectionsFragment
  extends BaseActionFragment
  with PublicCollectionsUiActions
  with PublicCollectionsDOM
  with PublicCollectionsListener
  with AppNineCardIntentConversions { self =>

  // TODO First implementation in order to remove LauncherPresenter
  def launcherPresenter: LauncherPresenter = getActivity match {
    case activity: LauncherActivity => activity.presenter
    case _ => throw new RuntimeException("LauncherPresenter not found")
  }

  lazy val collectionJobs = new PublicCollectionsJobs(self)

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = theme.get(CardLayoutBackgroundColor)

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    collectionJobs.initialize().resolveServiceOr(onError)
  }

  override def loadPublicCollectionsByTypeSharedCollection(typeSharedCollection: TypeSharedCollection): Unit =
    collectionJobs.loadPublicCollectionsByTypeSharedCollection(typeSharedCollection).resolveServiceOr(onError)

  override def loadPublicCollectionsByCategory(category: NineCardCategory): Unit =
    collectionJobs.loadPublicCollectionsByCategory(category).resolveServiceOr(onError)

  override def loadPublicCollections(): Unit =
    collectionJobs.loadPublicCollections().resolveServiceOr(onError)

  override def addLauncherCollection(collection: Collection): Unit =
    launcherPresenter.addCollection(collection)

  override def onAddCollection(sharedCollection: SharedCollection): Unit =
    collectionJobs.saveSharedCollection(sharedCollection).resolveServiceOr(_ => showErrorSavingCollectionInScreen())

  override def onShareCollection(sharedCollection: SharedCollection): Unit =
    collectionJobs.shareCollection(sharedCollection).resolveServiceOr(_ => showContactUsError())

  private[this] def onError(e: Throwable) = e match {
    case e: SharedCollectionsConfigurationException =>
      AppLog.invalidConfigurationV2
      showErrorLoadingCollectionInScreen()
    case _ => showErrorLoadingCollectionInScreen()
  }
}

object PublicCollectionsFragment {
  var statuses = PublicCollectionStatuses()
}

case class PublicCollectionStatuses(
  category: NineCardCategory = Communication,
  typeSharedCollection: TypeSharedCollection = TopSharedCollection)



