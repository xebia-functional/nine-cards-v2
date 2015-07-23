package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.text.TextUtils.TruncateAt
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, ViewGroup}
import android.widget.ImageView.ScaleType
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.modules.persistent.PersistentServicesComponent
import com.fortysevendeg.ninecardslauncher.app.ui.components.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

trait Styles {

  self: PersistentServicesComponent =>

  def searchContentStyle(implicit context: ContextWrapper): Tweak[LinearLayout] =
    vBackgroundColorFilter(persistentServices.getSearchBackgroundColor)

  def burgerButtonStyle(implicit context: ContextWrapper): Tweak[TintableImageView] =
    tivDefaultColor(persistentServices.getSearchIconsColor) +
      tivPressedColor(persistentServices.getSearchPressedColor)

  def googleButtonStyle(implicit context: ContextWrapper): Tweak[TintableImageView] =
    tivDefaultColor(persistentServices.getSearchGoogleColor) +
      tivPressedColor(persistentServices.getSearchPressedColor)

  def micButtonStyle(implicit context: ContextWrapper): Tweak[TintableImageView] =
    tivDefaultColor(persistentServices.getSearchIconsColor) +
      tivPressedColor(persistentServices.getSearchPressedColor)

  def drawerAppStyle(implicit context: ContextWrapper): Tweak[TintableImageView] = {
    val elevation = resGetDimensionPixelSize(R.dimen.elevation_pressed)
    Lollipop ifSupportedThen {
      vStateListAnimator(R.anim.elevation_transition) +
        vPaddings(elevation) +
        vCircleOutlineProvider(elevation)
    } getOrElse tivPressedColor(persistentServices.getAppDrawerPressedColor)
  }

  def drawerItemStyle(implicit context: ContextWrapper): Tweak[TintableImageView] =
    tivPressedColor(persistentServices.getAppDrawerPressedColor) +
      vTag(R.id.`type`, AppDrawer.app)

  def paginationItemStyle(implicit context: ContextWrapper) = {
    val margin = resGetDimensionPixelSize(R.dimen.margin_pager_collection)
    vWrapContent +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.workspaces_pager)
  }

}

trait CollectionsGroupStyle {

  val collectionGridStyle: Tweak[GridLayout] =
    vMatchParent

}

trait CollectionItemStyle {

  val collectionItemStyle: Tweak[LinearLayout] =
    vWrapContent +
      llVertical +
      llGravity(Gravity.CENTER) +
      flLayoutGravity(Gravity.CENTER)

  def iconStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_group_collection)
    lp[ViewGroup](size, size) +
      (Lollipop ifSupportedThen vElevation(resGetDimensionPixelSize(R.dimen.elevation_default)) getOrElse Tweak.blank)
  }

  def nameStyle(implicit context: ContextWrapper): Tweak[TextView] = {
    val displacement = resGetDimensionPixelSize(R.dimen.shadow_displacement_default)
    val radius = resGetDimensionPixelSize(R.dimen.shadow_radius_default)
    vWrapContent +
      vPadding(paddingTop = resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvColorResource(R.color.collection_group_name) +
      tvSizeResource(R.dimen.text_default) +
      tvLines(2) +
      tvEllipsize(TruncateAt.END) +
      tvGravity(Gravity.CENTER_HORIZONTAL) +
      tvShadowLayer(radius, displacement, displacement, resGetColor(R.color.shadow_default))
  }

}

object AppDrawer {
  val app = "app"
}