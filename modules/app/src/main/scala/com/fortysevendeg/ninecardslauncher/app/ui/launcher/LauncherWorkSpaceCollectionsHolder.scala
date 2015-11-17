package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable._
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.GridLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.components.Dimen
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.CollectionItemTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Tweak, Ui}

class LauncherWorkSpaceCollectionsHolder(parentDimen: Dimen)(implicit activityContext: ActivityContextWrapper)
  extends LauncherWorkSpaceHolder
  with CollectionsGroupStyle {

  var grid = slot[GridLayout]

  val views = 0 until numSpaces map (new CollectionItem(_))

  addView(getUi(l[GridLayout]() <~ wire(grid) <~ collectionGridStyle))

  runUi(grid <~ glAddViews(
    views = views,
    columns = numInLine,
    rows = numInLine,
    width = parentDimen.width / numInLine,
    height = parentDimen.height / numInLine))

  def populate(collections: Seq[Collection]): Ui[_] = {
    val uiSeq = for {
      row <- 0 until numInLine
      column <- 0 until numInLine
    } yield {
        val position = (row * numInLine) + column
        val view = grid map (_.getChildAt(position) match {
          case item: CollectionItem => item
        })
        collections.lift(position) map {
          collection =>
            view <~ vVisible <~ ciPopulate(collection)
        } getOrElse view <~ vGone
      }
    Ui.sequence(uiSeq: _*)
  }

}

class CollectionItem(positionInGrid: Int)(implicit activityContext: ActivityContextWrapper)
  extends FrameLayout(activityContext.application)
  with CollectionItemStyle {

  var collection: Option[Collection] = None

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var icon = slot[ImageView]

  var name = slot[TextView]

  setTag(positionInGrid)

  addView(
    getUi(
      l[LinearLayout](
        w[ImageView] <~ wire(icon) <~ iconStyle,
        w[TextView] <~ wire(name) <~ nameStyle
      ) <~ collectionItemStyle <~ On.click {
        collection map {
          c =>
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
              activityContext.getOriginal,
              new Pair[View, String](icon.get, getContentTransitionName(c.position)))
            val intent = createIntent[CollectionsDetailsActivity]
            intent.putExtra(startPosition, c.position)
            intent.putExtra(indexColorToolbar, c.themedColorIndex)
            intent.putExtra(iconToolbar, c.icon)
            uiStartIntentWithOptions(intent, options)
        } getOrElse Ui.nop
      } <~ vTag(R.id.use_layer_hardware, "")
    )
  )

  def populate(collection: Collection) = {
    this.collection = Some(collection)
    runUi(populateIcon(collection, iconCollectionWorkspace(collection.icon)))
  }

  private def populateIcon(collection: Collection, resIcon: Int): Ui[_] =
    (icon <~ ivSrc(resIcon) <~ vBackground(createBackground(collection.themedColorIndex))) ~
      (name <~ tvText(collection.name))

  private def createBackground(indexColor: Int): Drawable = {
    val color = resGetColor(getIndexColor(indexColor))

    Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(getColorDark(color, 0.2f))),
        getDrawable(color),
        null)
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), getDrawable(getColorDark(color)))
      states.addState(Array.emptyIntArray, getDrawable(color))
      states
    }
  }

  private def getDrawable(color: Int): Drawable = {
    val drawableColor = createShapeDrawable(color)
    Lollipop ifSupportedThen {
      drawableColor
    } getOrElse {
      val padding = resGetDimensionPixelSize(R.dimen.elevation_default)
      val drawableShadow = createShapeDrawable(resGetColor(R.color.shadow_default))
      val layer = new LayerDrawable(Array(drawableShadow, drawableColor))
      layer.setLayerInset(0, padding, padding, padding, 0)
      layer.setLayerInset(1, padding, 0, padding, padding)
      layer
    }
  }

  private def createShapeDrawable(color: Int) = {
    val drawableColor = new ShapeDrawable(new OvalShape())
    drawableColor.getPaint.setColor(color)
    drawableColor.getPaint.setStyle(Paint.Style.FILL)
    drawableColor.getPaint.setAntiAlias(true)
    drawableColor
  }

}

object CollectionItemTweaks {
  type W = CollectionItem

  def ciPopulate(collection: Collection) = Tweak[W](_.populate(collection))
}