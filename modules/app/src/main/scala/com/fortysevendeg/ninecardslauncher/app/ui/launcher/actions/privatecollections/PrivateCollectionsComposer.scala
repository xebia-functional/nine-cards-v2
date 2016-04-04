package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{PrivateCard, PrivateCollection}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait PrivateCollectionsComposer
  extends Styles
  with LauncherExecutor
  with NineCardIntentConversions {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  def initUi: Ui[_] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.myCollections) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  def showLoadingView: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showError(res: Int): Ui[_] = rootContent <~ uiSnackbarShort(res)

  def reloadPrivateCollections(
    privateCollections: Seq[PrivateCollection])(implicit uiContext: UiContext[_], presenter: PrivateCollectionsPresenter): Ui[_] = {
    val adapter = new PrivateCollectionsAdapter(privateCollections)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

}

case class ViewHolderPrivateCollectionsLayoutAdapter(
  content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], presenter: PrivateCollectionsPresenter)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  val appsByRow = 5

  lazy val iconContent = Option(findView(TR.private_collections_item_content))

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
    (iconContent <~ vBackground(d)) ~
      (icon <~ ivSrc(iconCollectionDetail(privateCollection.icon))) ~
      (appsRow1 <~
        vgRemoveAllViews <~
        automaticAlignment(appsRow1, cardsRow1)) ~
      (appsRow2 <~
        vgRemoveAllViews <~
        automaticAlignment(appsRow2, cardsRow2)) ~
      (name <~ tvText(privateCollection.name)) ~
      (content <~ vTag(position)) ~
      (addCollection <~ On.click(Ui(clickListener(privateCollection))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def automaticAlignment(view: Option[LinearLayout], cards: Seq[PrivateCard]): Tweak[LinearLayout] = {
    val width = view.map(_.getWidth) getOrElse 0
    if (width > 0) {
      val uisRow1 = getViewsByCards(cards, width)
      vgAddViews(uisRow1)
    } else {
      vGlobalLayoutListener { v => {
        val uisRow1 = getViewsByCards(cards, v.getWidth)
        appsRow1 <~ vgAddViews(uisRow1)
      }}
    }
  }

  private[this] def getViewsByCards(cards: Seq[PrivateCard], width: Int) = {
    val size = resGetDimensionPixelSize(R.dimen.size_icon_item_collections_content)
    val padding = (width - (size * appsByRow)) / (appsByRow - 1)
    cards.zipWithIndex map {
      case (card, index) =>
        (w[ImageView] <~
          lp[ViewGroup](size, size) <~
          (if (index < appsByRow - 1) llLayoutMargin(0, 0, padding, 0) else Tweak.blank) <~
          ivUri(card.imagePath)).get
    }
  }
}