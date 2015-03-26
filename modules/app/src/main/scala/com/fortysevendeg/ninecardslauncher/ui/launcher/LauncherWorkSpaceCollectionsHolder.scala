package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.graphics.Paint
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.{LayerDrawable, ShapeDrawable, Drawable}
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.widget._
import com.fortysevendeg.macroid.extras.GridLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.modules.repository.Collection
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.ui.components.Dimen
import com.fortysevendeg.ninecardslauncher.ui.launcher.CollectionItemTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext, Tweak}

class LauncherWorkSpaceCollectionsHolder(parentDimen: Dimen)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LauncherWorkSpaceHolder
  with CollectionsGroupStyle {

  var grid = slot[GridLayout]

  val views = 0 to (numSpaces - 1) map (new CollectionItem(_))

  addView(getUi(l[GridLayout]() <~ wire(grid) <~ collectionGridStyle))

  runUi(grid <~ glAddViews(
    views = views,
    columns = numInLine,
    rows = numInLine,
    width = parentDimen.width / numInLine,
    height = parentDimen.height / numInLine))

  def populate(data: LauncherData) = for {
    row <- 0 to (numInLine - 1)
    column <- 0 to (numInLine - 1)
  } yield {
    val position = (row * numInLine) + column
    val view = grid map (_.getChildAt(position).asInstanceOf[CollectionItem])
    data.collections.lift(position) map {
      collection =>
        runUi(view <~ vVisible <~ ciPopulate(collection))
    } getOrElse runUi(view <~ vGone)
  }

}

class CollectionItem(position: Int)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends FrameLayout(activityContext.get)
  with CollectionItemStyle {

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var icon = slot[ImageView]

  var name = slot[TextView]

  setTag(position)

  addView(
    getUi(
      l[LinearLayout](
        w[ImageView] <~ wire(icon) <~ iconStyle,
        w[TextView] <~ wire(name) <~ nameStyle
      ) <~ collectionItemStyle
    )
  )

  def populate(collection: Collection) = resGetDrawableIdentifier(collection.icon) map {
    resIcon =>
      runUi(
        (icon <~ ivSrc(resIcon) <~ vBackground(createBackground(collection.themedColorIndex))) ~
          (name <~ tvText(collection.name))
      )
  }

  def createBackground(indexColor: Int): Drawable = {
    val color = indexColor match {
      case 0 => R.color.collection_group_1
      case 1 => R.color.collection_group_2
      case 2 => R.color.collection_group_3
      case 3 => R.color.collection_group_4
      case 4 => R.color.collection_group_5
      case 5 => R.color.collection_group_6
      case 6 => R.color.collection_group_7
      case 7 => R.color.collection_group_8
      case _ => R.color.collection_group_9
    }
    val drawableColor = new ShapeDrawable(new OvalShape())
    drawableColor.getPaint.setColor(resGetColor(color))
    drawableColor.getPaint.setStyle(Paint.Style.FILL)
    drawableColor.getPaint.setAntiAlias(true)

    Lollipop ifSupportedThen {
      drawableColor
    } getOrElse {
      val padding = resGetDimensionPixelSize(R.dimen.elevation_default)
      val drawableShadow = new ShapeDrawable(new OvalShape())
      drawableShadow.getPaint.setColor(resGetColor(R.color.shadow_default))
      drawableShadow.getPaint.setStyle(Paint.Style.FILL)
      drawableShadow.getPaint.setAntiAlias(true)
      val layer = new LayerDrawable(Array(drawableShadow, drawableColor))
      layer.setLayerInset(0, padding, padding, padding, 0)
      layer.setLayerInset(1, padding, 0, padding, padding)
      layer
    }
  }

}

object CollectionItemTweaks {
  type W = CollectionItem

  def ciPopulate(collection: Collection) = Tweak[W](_.populate(collection))
}