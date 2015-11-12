package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{UiContext, LauncherExecutor, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.process.collection.{PrivateCard, PrivateCollection}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

trait PrivateCollectionsComposer
  extends Styles
  with LauncherExecutor
  with NineCardIntentConversions {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  def initUi: Ui[_] =
    (toolbar <~
      tbTitle(R.string.myCollections) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def addPrivateCollections(
    privateCollections: Seq[PrivateCollection],
    clickListener: (PrivateCollection) => Unit)(implicit uiContext: UiContext[_]): Ui[_] = {
    val adapter = new PrivateCollectionsAdapter(privateCollections, clickListener)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

}

case class ViewHolderPrivateCollectionsLayoutAdapter(
  content: ViewGroup,
  clickListener: (PrivateCollection) => Unit)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  val appsByRow = 5

  lazy val icon = Option(findView(TR.private_collections_item_icon))

  lazy val name = Option(findView(TR.private_collections_item_name))

  lazy val appsRow1 = Option(findView(TR.private_collections_item_row1))

  lazy val appsRow2 = Option(findView(TR.private_collections_item_row2))

  lazy val addCollection = Option(findView(TR.private_collections_item_add_collection))

  def bind(privateCollection: PrivateCollection, position: Int): Ui[_] = {
    val d = new ShapeDrawable(new OvalShape)
    d.getPaint.setColor(resGetColor(getIndexColor(privateCollection.themedColorIndex)))
    val cardsRow1 = privateCollection.cards slice(0, appsByRow)
    val cardsRow2 = privateCollection.cards slice(appsByRow, appsByRow * 2)
    val uisRow1 = getViewsByCards(cardsRow1)
    val uisRow2 = getViewsByCards(cardsRow2)
    (icon <~
      ivSrc(iconCollectionDetail(privateCollection.icon)) <~
      vBackground(d)) ~
      (appsRow1 <~ vgRemoveAllViews <~ vgAddViews(uisRow1)) ~
      (appsRow2 <~ vgRemoveAllViews <~ vgAddViews(uisRow2)) ~
      (name <~ tvText(privateCollection.name)) ~
      (content <~ vTag2(position)) ~
      (addCollection <~ On.click(Ui(clickListener(privateCollection))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def getViewsByCards(cards: Seq[PrivateCard]) = {
    val size = resGetDimensionPixelSize(R.dimen.size_app_private_collections)
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    cards map { card =>
      getUi(
        w[ImageView] <~
          lp[ViewGroup](size, size) <~
          vPaddings(padding) <~
          ivUri(card.imagePath))
    }
  }
}