package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.sharedcollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup.LayoutParams._
import android.view.{View, ViewGroup}
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.collections.CollectionCardsStyles
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.SharedCollectionOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.CharDrawable
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.google.android.flexbox.FlexboxLayout
import macroid.FullDsl._
import macroid._

case class ViewHolderSharedCollectionsLayoutAdapter(
  content: ViewGroup,
  onAddCollection: (SharedCollection) => Unit,
  onShareCollection: (SharedCollection) => Unit,
  mySharedCollectionIds: Seq[String] = Seq.empty)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], val theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
    with TypedFindView
    with CollectionCardsStyles {

  val appsByRow = 5

  lazy val root = findView(TR.public_collections_item_layout)

  lazy val iconContent = findView(TR.public_collections_item_content)

  lazy val icon = findView(TR.public_collections_item_icon)

  lazy val name = findView(TR.public_collections_item_name)

  lazy val author = findView(TR.public_collections_item_author)

  lazy val downloads = findView(TR.public_collections_item_downloads)

  lazy val subscriptions = findView(TR.public_collections_item_subscriptions)

  lazy val appsIcons = findView(TR.public_collections_item_apps)

  lazy val addCollection = findView(TR.public_collections_item_add_collection)

  lazy val shareCollection = findView(TR.public_collections_item_share_collection)

  ((root <~ cardRootStyle) ~
    (name <~ textStyle) ~
    (author <~ textStyle) ~
    (downloads <~ leftDrawableTextStyle(R.drawable.icon_collection_downloads)) ~
    (subscriptions <~ leftDrawableTextStyle(R.drawable.icon_collection_subscriptions)) ~
    (addCollection <~ buttonStyle) ~
    (shareCollection <~ ivSrc(tintDrawable(R.drawable.icon_dialog_collection_share)))).run

  def bind(collection: SharedCollection, position: Int): Ui[Any] = {
    val background = new ShapeDrawable(new OvalShape)
    background.getPaint.setColor(resGetColor(getRandomIndexColor))
    val apps = collection.resolvedPackages
    (iconContent <~ vBackground(background)) ~
      (icon <~ ivSrc(collection.getIconCollectionDetail)) ~
      (appsIcons <~
        fblRemoveAllViews <~
        automaticAlignment(apps)) ~
      (name <~ tvText(resGetString(collection.name) getOrElse collection.name)) ~
      (author <~ tvText(collection.author)) ~
      (subscriptions <~
        (if (collection.views < 0) vGone else vVisible + tvText(s"${collection.views}" + " " + resGetString(R.string.subscriptions).toLowerCase))) ~ //TODO Change collection.views for collection.subscriptions: Option[Int] and hide when it's a None
      (downloads <~ tvText(s"${collection.views}")) ~
      (content <~ vTag(position)) ~
      (addCollection <~
        (if(mySharedCollectionIds.contains(collection.sharedCollectionId)) vInvisible else vVisible + On.click(Ui(onAddCollection(collection))))) ~
      (shareCollection <~ On.click(Ui(onShareCollection(collection))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def automaticAlignment(packages: Seq[SharedCollectionPackage]): Tweak[FlexboxLayout] = {
    val width = appsIcons.getWidth
    if (width > 0) {
      vgAddViews(getViewsByCards(packages, width))
    } else {
      vGlobalLayoutListener { v => {
        appsIcons <~ vgAddViews(getViewsByCards(packages, v.getWidth))
      }}
    }
  }

  private[this] def getViewsByCards(packages: Seq[SharedCollectionPackage], width: Int) = {
    val sizeIcon = resGetDimensionPixelSize(R.dimen.size_icon_item_collections_content)
    val sizeView = width / appsByRow
    val padding = (sizeView - sizeIcon) / 2
    val appsViews = packages map { pkg =>
      (w[ImageView] <~
        lp[FlexboxLayout](sizeView, WRAP_CONTENT) <~
        vPadding(padding, 0, padding, 0) <~
        ivUri(pkg.icon)).get
    }
    appsViews
  }

  private[this] def getCounter(plus: Int, width: Int) = {
    val size = resGetDimensionPixelSize(R.dimen.size_icon_item_collections_content)
    val color = resGetColor(R.color.background_count_public_collection_dialog)
    (w[ImageView] <~
      lp[ViewGroup](size, size) <~
      ivSrc(CharDrawable(s"+$plus", circle = true, Some(color)))).get
  }
}