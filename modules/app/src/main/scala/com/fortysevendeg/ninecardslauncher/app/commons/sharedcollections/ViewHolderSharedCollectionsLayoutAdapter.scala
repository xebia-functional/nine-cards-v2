package com.fortysevendeg.ninecardslauncher.app.commons.sharedcollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.collections.CollectionCardsStyles
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.SharedCollectionOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.CharDrawable
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class ViewHolderSharedCollectionsLayoutAdapter(
  content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], presenter: SharedCollectionsPresenter, val theme: NineCardsTheme)
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

  lazy val description = findView(TR.public_collections_item_description)

  lazy val appsIcons = findView(TR.public_collections_item_apps)

  lazy val addCollection = findView(TR.public_collections_item_add_collection)

  lazy val shareCollection = findView(TR.public_collections_item_share_collection)

  ((root <~ cardRootStyle) ~
    (name <~ textStyle) ~
    (author <~ textStyle) ~
    (downloads <~ leftDrawableTextStyle(R.drawable.icon_dialog_collection_downloaded)) ~
    (description <~ textStyle) ~
    (addCollection <~ buttonStyle) ~
    (shareCollection <~ ivSrc(tintDrawable(R.drawable.icon_dialog_collection_share)))).run

  def bind(collection: SharedCollection, position: Int): Ui[Any] = {
    val background = new ShapeDrawable(new OvalShape)
    background.getPaint.setColor(resGetColor(getRandomIndexColor))
    val appsCount = appsByRow - 1
    val apps = collection.resolvedPackages slice(0, appsCount)
    val plus = collection.resolvedPackages.length - appsCount
    (iconContent <~ vBackground(background)) ~
      (icon <~ ivSrc(collection.getIconCollectionDetail)) ~
      (appsIcons <~
        vgRemoveAllViews <~
        automaticAlignment(apps, plus)) ~
      (name <~ tvText(resGetString(collection.category.getStringResource) getOrElse collection.category.getStringResource)) ~
      (author <~ tvText(collection.author)) ~
      (description <~ (if (collection.description.isEmpty) vGone else vVisible + tvText(collection.description))) ~
      (downloads <~ tvText(s"${collection.views}")) ~
      (content <~ vTag(position)) ~
      (addCollection <~ On.click(Ui(presenter.saveSharedCollection(collection)))) ~
      (shareCollection <~ On.click(Ui(presenter.launchShareCollection(collection.sharedCollectionId))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def automaticAlignment(packages: Seq[SharedCollectionPackage], plus: Int): Tweak[LinearLayout] = {
    val width = appsIcons.getWidth
    if (width > 0) {
      vgAddViews(getViewsByCards(packages, width, plus))
    } else {
      vGlobalLayoutListener { v => {
        appsIcons <~ vgAddViews(getViewsByCards(packages, v.getWidth, plus))
      }}
    }
  }

  private[this] def getViewsByCards(packages: Seq[SharedCollectionPackage], width: Int, plus: Int) = {
    val size = resGetDimensionPixelSize(R.dimen.size_icon_item_collections_content)
    val padding = (width - (size * appsByRow)) / (appsByRow - 1)
    val appsViews = packages map { pkg =>
      (w[ImageView] <~
        lp[ViewGroup](size, size) <~
        llLayoutMargin(marginRight = padding) <~
        ivUri(pkg.icon)).get
    }
    if (plus > 0) {
      appsViews :+ getCounter(plus, width)
    } else {
      appsViews
    }
  }

  private[this] def getCounter(plus: Int, width: Int) = {
    val size = resGetDimensionPixelSize(R.dimen.size_icon_item_collections_content)
    val color = resGetColor(R.color.background_count_public_collection_dialog)
    (w[ImageView] <~
      lp[ViewGroup](size, size) <~
      ivSrc(CharDrawable(s"+$plus", circle = true, Some(color)))).get
  }
}