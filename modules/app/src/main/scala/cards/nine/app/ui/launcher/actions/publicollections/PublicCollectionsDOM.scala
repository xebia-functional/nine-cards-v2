package cards.nine.app.ui.launcher.actions.publicollections

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.{LinearLayout, TextView}
import cards.nine.models.{Collection, SharedCollection}
import cards.nine.models.types.{TypeSharedCollection, NineCardsCategory}
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.Contexts
import macroid.FullDsl._

trait PublicCollectionsDOM {

  self: TypedFindView with Contexts[Fragment] =>

  lazy val recycler = findView(TR.actions_recycler)

  var typeFilter = slot[TextView]

  var categoryFilter = slot[TextView]

  lazy val tabsMenu = l[LinearLayout](
    w[TextView] <~
      wire(typeFilter) <~
      tabButtonStyle(R.string.top),
    w[TextView] <~
      wire(categoryFilter) <~
      tabButtonStyle(R.string.communication)).get

  def tabButtonStyle(text: Int) = {
    val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)
    val paddingLarge = resGetDimensionPixelSize(R.dimen.padding_large)
    val paddingSmall = resGetDimensionPixelSize(R.dimen.padding_small)
    vWrapContent +
      tvText(text) +
      tvNormalMedium +
      tvSizeResource(R.dimen.text_large) +
      tvGravity(Gravity.CENTER_VERTICAL) +
      tvColorResource(R.color.tab_public_collection_dialog) +
      vPadding(paddingTop = paddingDefault, paddingBottom = paddingDefault, paddingRight = paddingLarge) +
      tvDrawablePadding(paddingSmall) +
      tvCompoundDrawablesWithIntrinsicBoundsResources(right = R.drawable.tab_menu_indicator)
  }

}

trait PublicCollectionsListener {

  def loadPublicCollectionsByTypeSharedCollection(typeSharedCollection: TypeSharedCollection): Unit

  def loadPublicCollectionsByCategory(category: NineCardsCategory): Unit

  def loadPublicCollections(): Unit

  def onAddCollection(sharedCollection: SharedCollection): Unit

  def onShareCollection(sharedCollection: SharedCollection): Unit

}