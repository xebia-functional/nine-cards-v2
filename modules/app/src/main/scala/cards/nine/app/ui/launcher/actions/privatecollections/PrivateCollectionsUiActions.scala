package cards.nine.app.ui.launcher.actions.privatecollections

import cards.nine.app.commons.AppNineCardIntentConversions
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.process.commons.models.{Collection, PrivateCollection}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

trait PrivateCollectionsUiActions
  extends Styles {

  self: BaseActionFragment with PrivateCollectionsDOM with PrivateCollectionsListener =>

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.myCollections) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)).toService

  def addPrivateCollections(privateCollections: Seq[PrivateCollection]): TaskService[Unit] = {
    val adapter = PrivateCollectionsAdapter(privateCollections, saveCollection)
    ((recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)).toService
  }

  def addCollection(collection: Collection): TaskService[Unit] = Ui {
    addLauncherCollection(collection)
  }.toService

  def showLoading(): TaskService[Unit] = ((loading <~ vVisible) ~ (recycler <~ vGone)).toService

  def showEmptyMessageInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.emptyPrivateCollections, error = false, loadPrivateCollections()).toService

  def showErrorLoadingCollectionInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingPrivateCollections, error = true, loadPrivateCollections()).toService

  def showErrorSavingCollectionInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorSavingPrivateCollections, error = true, loadPrivateCollections()).toService

  def close(): TaskService[Unit] = unreveal().toService

}
