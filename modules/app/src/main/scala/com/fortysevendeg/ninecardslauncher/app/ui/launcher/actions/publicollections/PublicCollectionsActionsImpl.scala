package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import android.support.v4.app.Fragment
import android.widget.{LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.sharedcollections.SharedCollectionsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.{LatestSharedCollection, TopSharedCollection, TypeSharedCollection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait PublicCollectionsActionsImpl
  extends PublicCollectionsUiActions 
  with Styles
  with PublicCollectionsStyle
  with NineCardIntentConversions {

  self: TypedFindView with BaseActionFragment with Contexts[Fragment] =>

  val launcherPresenter: LauncherPresenter

  implicit val collectionPresenter: PublicCollectionsPresenter

  lazy val recycler = Option(findView(TR.actions_recycler))

  def loadBackgroundColor = theme.get(CardLayoutBackgroundColor)

  var typeFilter = slot[TextView]

  var categoryFilter = slot[TextView]

  lazy val (categoryNamesMenu, categories) = {
    val categoriesSorted = NineCardCategory.appsCategories map { category =>
      (resGetString(category.getStringResource) getOrElse category.name, category)
    } sortBy(_._1)
    (categoriesSorted map (_._1), categoriesSorted map (_._2))
  }

  override def initialize(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.publicCollections) <~
      dtbExtended <~
      dtbAddExtendedView(
        l[LinearLayout](
          w[TextView] <~
            wire(typeFilter) <~
            tabButtonStyle(R.string.top),
          w[TextView] <~
            wire(categoryFilter) <~
            tabButtonStyle(R.string.communication)).get) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (typeFilter <~
        On.click {
          val values = Seq(resGetString(R.string.top), resGetString(R.string.latest))
          typeFilter <~ vListThemedPopupWindowShow(
            values = values,
            onItemClickListener = {
              case 0 => collectionPresenter.loadPublicCollectionsByTypeSharedCollection(TopSharedCollection)
              case _ => collectionPresenter.loadPublicCollectionsByTypeSharedCollection(LatestSharedCollection)
            })
        }) ~
      (categoryFilter <~
        On.click {
          categoryFilter <~ vListThemedPopupWindowShow(
            values = categoryNamesMenu,
            onItemClickListener = (position: Int) => {
              categories.lift(position) foreach collectionPresenter.loadPublicCollectionsByCategory
            },
            width = Some(resGetDimensionPixelSize(R.dimen.width_list_popup_menu)),
            height = Some(resGetDimensionPixelSize(R.dimen.height_list_popup_menu)))
        }) ~
      (recycler <~ recyclerStyle)

  override def showErrorLoadingCollectionInScreen(): Ui[Any] =
    showMessageInScreen(R.string.errorLoadingPublicCollections, error = true, action = collectionPresenter.loadPublicCollections())

  override def showErrorSavingCollectionInScreen(): Ui[Any] =
    showMessageInScreen(R.string.errorSavingPublicCollections, error = true, action = collectionPresenter.loadPublicCollections())

  override def showEmptyMessageInScreen(): Ui[Any] =
    showMessageInScreen(R.string.emptyPublicCollections, error = false, collectionPresenter.loadPublicCollections())

  override def loadPublicCollections(
    sharedCollections: Seq[SharedCollection],
    onAddCollection: (SharedCollection) => Unit,
    onShareCollection: (SharedCollection) => Unit): Ui[Any] = {
    val adapter = SharedCollectionsAdapter(sharedCollections, onAddCollection, onShareCollection)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

  override def addCollection(collection: Collection): Ui[Any] = Ui {
    launcherPresenter.addCollection(collection)
  }

  override def showLoading(): Ui[Any] = (loading <~ vVisible) ~ (recycler <~ vGone)

  override def updateCategory(category: NineCardCategory): Ui[Any] =
    categoryFilter <~ tvText(resGetString(category.getStringResource) getOrElse category.name)

  override def updateTypeCollection(typeSharedCollection: TypeSharedCollection): Ui[Any] = typeSharedCollection match {
    case TopSharedCollection => typeFilter <~ tvText(R.string.top)
    case LatestSharedCollection => typeFilter <~ tvText(R.string.latest)
  }

  override def close(): Ui[Any] = unreveal()

}