package cards.nine.app.ui.launcher.actions.publicollections

import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.adapters.sharedcollections.SharedCollectionsAdapter
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Collection
import cards.nine.models.types.NineCardsCategory
import cards.nine.process.sharedcollections.models.SharedCollection
import cards.nine.process.sharedcollections.{LatestSharedCollection, TopSharedCollection, TypeSharedCollection}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

trait PublicCollectionsUiActions
  extends Styles
    with AppNineCardsIntentConversions {

  self: BaseActionFragment with PublicCollectionsDOM with PublicCollectionsListener =>

  lazy val (categoryNamesMenu, categories) = {
    val categoriesSorted = NineCardsCategory.appsCategories map { category =>
      (resGetString(category.getStringResource) getOrElse category.name, category)
    } sortBy (_._1)
    (categoriesSorted map (_._1), categoriesSorted map (_._2))
  }

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.publicCollections) <~
      dtbExtended <~
      dtbAddExtendedView(tabsMenu) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (typeFilter <~
        On.click {
          val values = Seq(resGetString(R.string.top), resGetString(R.string.latest))
          typeFilter <~ vListThemedPopupWindowShow(
            values = values,
            onItemClickListener = {
              case 0 => loadPublicCollectionsByTypeSharedCollection(TopSharedCollection)
              case _ => loadPublicCollectionsByTypeSharedCollection(LatestSharedCollection)
            })
        }) ~
      (categoryFilter <~
        On.click {
          categoryFilter <~ vListThemedPopupWindowShow(
            values = categoryNamesMenu,
            onItemClickListener = (position: Int) => {
              categories.lift(position) foreach loadPublicCollectionsByCategory
            },
            width = Some(resGetDimensionPixelSize(R.dimen.width_list_popup_menu)),
            height = Some(resGetDimensionPixelSize(R.dimen.height_list_popup_menu)))
        }) ~
      (recycler <~ recyclerStyle)).toService

  def showErrorLoadingCollectionInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingPublicCollections, error = true, action = loadPublicCollections()).toService

  def showErrorSavingCollectionInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorSavingPublicCollections, error = true, action = loadPublicCollections()).toService

  def showEmptyMessageInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.emptyPublicCollections, error = false, loadPublicCollections()).toService

  def showContactUsError(): TaskService[Unit] = uiShortToast2(R.string.contactUsError).toService

  def loadPublicCollections(
    sharedCollections: Seq[SharedCollection]): TaskService[Unit] = {
    val adapter = SharedCollectionsAdapter(sharedCollections, onAddCollection, onShareCollection)
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

  def updateCategory(category: NineCardsCategory): TaskService[Unit] =
    (categoryFilter <~ tvText(resGetString(category.getStringResource) getOrElse category.name)).toService

  def updateTypeCollection(typeSharedCollection: TypeSharedCollection): TaskService[Unit] = (typeSharedCollection match {
    case TopSharedCollection => typeFilter <~ tvText(R.string.top)
    case LatestSharedCollection => typeFilter <~ tvText(R.string.latest)
  }).toService

  def close(): TaskService[Unit] = unreveal().toService

}
