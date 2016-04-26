package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.content.ClipData
import android.text.TextUtils.TruncateAt
import android.view.{Gravity, View, ViewGroup}
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.DragObject
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.types.ReorderCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

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

  def startDragStyle(label: String, description: String): Tweak[View] = Tweak[View] { view =>
    val dragData = ClipData.newPlainText(label, description)
    val shadow = new View.DragShadowBuilder(view)
    view.startDrag(dragData, shadow, DragObject(shadow, ReorderCollection), 0)
  }

}

object LauncherTags {
  val app = "app"
}