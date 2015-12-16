package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, NineCardIntentConversions, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.CharDrawable
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Tweak, Ui}

trait PublicCollectionsComposer
  extends Styles
  with LauncherExecutor
  with NineCardIntentConversions {

  self: TypedFindView with BaseActionFragment with PublicCollectionsListener =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  def initUi: Ui[_] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.publicCollections) <~
      dtbExtended <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def addPublicCollections(
    sharedCollections: Seq[SharedCollection])(implicit uiContext: UiContext[_]): Ui[_] = {
    val adapter = new PublicCollectionsAdapter(sharedCollections, saveSharedCollection)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

}

case class ViewHolderPublicCollectionsLayoutAdapter(
  content: ViewGroup,
  clickListener: (SharedCollection) => Unit)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with TypedFindView
  with LauncherExecutor {

  val appsByRow = 5

  lazy val iconContent = Option(findView(TR.public_collections_item_content))

  lazy val icon = Option(findView(TR.public_collections_item_icon))

  lazy val name = Option(findView(TR.public_collections_item_name))

  lazy val author = Option(findView(TR.public_collections_item_author))

  lazy val downloads = Option(findView(TR.public_collections_item_downloads))

  lazy val description = Option(findView(TR.public_collections_item_description))

  lazy val appsIcons = Option(findView(TR.public_collections_item_apps))

  lazy val addCollection = Option(findView(TR.public_collections_item_add_collection))

  lazy val shareCollection = Option(findView(TR.public_collections_item_share_collection))

  def bind(collection: SharedCollection, position: Int): Ui[_] = {
    val background = new ShapeDrawable(new OvalShape)
    background.getPaint.setColor(resGetColor(getRandomIndexColor))
    val appsCount = appsByRow - 1
    val apps = collection.resolvedPackages slice(0, appsCount)
    val plus = collection.resolvedPackages.length - appsCount
    (iconContent <~ vBackground(background)) ~
      (icon <~ ivSrc(iconCollectionDetail(collection.icon))) ~
      (appsIcons <~
        vgRemoveAllViews <~
        automaticAlignment(apps, plus)) ~
      (name <~ tvText(resGetString(collection.category.getStringResource) getOrElse collection.category.getStringResource)) ~
      (author <~ tvText(collection.author)) ~
      (description <~ (if (collection.description.isEmpty) vGone else vVisible + tvText(collection.description))) ~
      (downloads <~ tvText(s"${collection.views}")) ~
      (content <~ vTag2(position)) ~
      (addCollection <~ On.click(Ui(clickListener(collection)))) ~
      (shareCollection <~ On.click(Ui(launchShare(collection.shareLink))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def automaticAlignment(packages: Seq[SharedCollectionPackage], plus: Int): Tweak[LinearLayout] = {
    val width = appsIcons.map(_.getWidth) getOrElse 0
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
      getUi(
        w[ImageView] <~
          lp[ViewGroup](size, size) <~
          llLayoutMargin(marginRight = padding) <~
          ivUri(pkg.icon))
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
    getUi(
      w[ImageView] <~
        lp[ViewGroup](size, size) <~
        ivSrc(new CharDrawable(s"+$plus", circle = true, Some(color))))
  }
}