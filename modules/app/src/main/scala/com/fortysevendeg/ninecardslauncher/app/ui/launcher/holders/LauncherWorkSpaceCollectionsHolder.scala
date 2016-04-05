package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable._
import android.graphics.drawable.shapes.OvalShape
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.GridLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder}
import CollectionItemTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{CollectionItemStyle, CollectionsGroupStyle, LauncherPresenter}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceCollectionsHolder(presenter: LauncherPresenter, parentDimen: Dimen)(implicit contextWrapper: ContextWrapper)
  extends LauncherWorkSpaceHolder
  with CollectionsGroupStyle {

  var grid = slot[GridLayout]

  val views = 0 until numSpaces map (position => new CollectionItem(presenter, position))

  addView((l[GridLayout]() <~ wire(grid) <~ collectionGridStyle).get)

  (grid <~ glAddViews(
    views = views,
    columns = numInLine,
    rows = numInLine,
    width = parentDimen.width / numInLine,
    height = parentDimen.height / numInLine)).run

  def populate(collections: Seq[Collection]): Ui[_] = {
    val uiSeq = for {
      row <- 0 until numInLine
      column <- 0 until numInLine
    } yield {
      val position = (row * numInLine) + column
      val view = grid flatMap (_.getChildAt(position) match {
        case item: CollectionItem => Some(item)
        case _ => None
      })
      collections.lift(position) map { collection =>
        view <~ vVisible <~ ciPopulate(collection)
      } getOrElse view <~ vGone
    }
    Ui.sequence(uiSeq: _*)
  }

}

class CollectionItem(presenter: LauncherPresenter, positionInGrid: Int)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.application)
  with CollectionItemStyle {

  var collection: Option[Collection] = None

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var icon = slot[ImageView]

  var name = slot[TextView]

  setTag(positionInGrid)

  addView(
    (l[LinearLayout](
      w[ImageView] <~ wire(icon) <~ iconStyle,
      w[TextView] <~ wire(name) <~ nameStyle
    ) <~ collectionItemStyle <~ On.click {
      Ui(presenter.goToCollection(icon, collection))
    } <~ On.longClick {
      presenter.removeCollection(collection)
      Ui(true)
    } <~ vUseLayerHardware).get)

  def populate(collection: Collection) = {
    this.collection = Some(collection)
    val resIcon = iconCollectionWorkspace(collection.icon)
    ((icon <~ ivSrc(resIcon) <~ vBackground(createBackground(collection.themedColorIndex))) ~
      (name <~ tvText(collection.name))).run
  }

  private[this] def createBackground(indexColor: Int): Drawable = {
    val color = resGetColor(getIndexColor(indexColor))

    Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(getColorDark(color, 0.2f))),
        getDrawable(color),
        javaNull)
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), getDrawable(getColorDark(color)))
      states.addState(Array.emptyIntArray, getDrawable(color))
      states
    }
  }

  private[this] def getDrawable(color: Int): Drawable = {
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

  private[this] def createShapeDrawable(color: Int) = {
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