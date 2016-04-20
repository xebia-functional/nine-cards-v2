package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable._
import android.graphics.drawable.shapes.OvalShape
import android.os.Handler
import android.view.DragEvent._
import android.view.View.OnDragListener
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view.{DragEvent, View}
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
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders.CollectionItemTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.types.{DragLauncherType, ReorderCollection}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{CollectionItemStyle, CollectionsGroupStyle, LauncherPresenter}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceCollectionsHolder(presenter: LauncherPresenter, parentDimen: Dimen)(implicit contextWrapper: ContextWrapper)
  extends LauncherWorkSpaceHolder
  with CollectionsGroupStyle {

  val widthSpace = parentDimen.width / numInLine

  val heightSpace = parentDimen.height / numInLine

  var grid: Option[GridLayout] = slot[GridLayout]

  val views: Seq[CollectionItem] = 0 until numSpaces map { position =>
    new CollectionItem(
      presenter = presenter,
      originalPosition = position)
  }

  addView((l[GridLayout]() <~ wire(grid) <~ collectionGridStyle).get)

  (grid <~
    glAddViews(
      views = views,
      columns = numInLine,
      rows = numInLine,
      width = widthSpace,
      height = heightSpace) <~
    vDragListener()).run

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

  private[this] def vDragListener(): Tweak[View] = Tweak[View] { view =>
    view.setOnDragListener(new OnDragListener {
      override def onDrag(v: View, event: DragEvent): Boolean = {
        (event.getAction, DragLauncherType(event.getLocalState), presenter.statuses.isReording(), isRunningReorderAnimation) match {
          case (ACTION_DRAG_LOCATION, ReorderCollection, true, false) =>
            val lastCurrentPosition = presenter.statuses.currentPositionReorderMode
            val currentPosition = calculatePosition(event.getX, event.getY)
            if (lastCurrentPosition != currentPosition) {
              reorder(lastCurrentPosition, currentPosition).run
              presenter.draggingTo(currentPosition)
            }
          case (ACTION_DROP | ACTION_DRAG_ENDED, ReorderCollection, true, false) =>
            resetPlaces.run
            presenter.drop()
          case (ACTION_DROP | ACTION_DRAG_ENDED, ReorderCollection, true, true) =>
            // we are waiting that the animation is finished in order to reset views
            val duration = resGetInteger(R.integer.anim_duration_normal)
            new Handler().postDelayed(new Runnable {
              override def run(): Unit = {
                resetPlaces.run
                presenter.drop()
              }
            }, duration)
          case _ =>
        }
        true
      }
    })
  }

  private[this] def reorder(currentPosition: Int, toPosition: Int): Ui[Any] = {
    if (currentPosition < toPosition) {
      val from = currentPosition + 1
      val to = toPosition
      val transforms = from to to map { pos =>
        move(pos, pos - 1)
      }
      val updatePositions = from to to map { pos =>
        getView(pos) map (view => Ui(view.positionInGrid = pos - 1)) getOrElse Ui.nop
      }
      Ui.sequence(transforms ++ updatePositions:_*)
    } else if (currentPosition > toPosition) {
      val from = toPosition
      val to = currentPosition
      val transforms = from until to map { pos =>
        move(pos, pos  + 1)
      }
      val updatePositions = from until to map { pos =>
        getView(pos) map (view => Ui(view.positionInGrid = pos + 1)) getOrElse Ui.nop
      }
      Ui.sequence(transforms ++ updatePositions:_*)
    } else {
      Ui.nop
    }
  }

  private[this] def move(from: Int, to: Int): Ui[Any] = {
    val (fromColumn, fromRow) = place(from)
    val (toColumn, toRow) = place(to)
    val displacementHorizontal = (toColumn - fromColumn) * widthSpace
    val displacementVertical = (toRow - fromRow) * heightSpace
    val view = getView(from)
    view <~ applyAnimation(
      xBy = Some(displacementHorizontal),
      yBy = Some(displacementVertical))
  }

  private[this] def resetPlaces: Ui[Any] = {
    val start = presenter.statuses.startPositionReorderMode
    val current = presenter.statuses.currentPositionReorderMode
    val collectionsReordered = (views map (_.collection)).reorder(start, current).zipWithIndex map {
      case (collection, index) => collection map(_.copy(position = index))
    }
    Ui.sequence(views.zip(collectionsReordered) map {
      case (view, maybeCollection) =>
        (view <~
          (maybeCollection map ciPopulate getOrElse Tweak.blank) <~
          vClearAnimation <~
          vTranslationX(0) <~
          vTranslationY(0) <~
          vVisible) ~
          view.resetOriginalPosition()
    }:_*)
  }

  private[this] def place(pos: Int): (Int, Int) = {
    val row = pos / numInLine
    val column = pos % numInLine
    (column, row)
  }

  private[this] def getView(position: Int): Option[CollectionItem] = views.find(_.positionInGrid == position)

  private[this] def calculatePosition(x: Float, y: Float): Int = {
    val column = x.toInt / widthSpace
    val row = y.toInt / heightSpace
    (row * numInLine) + column
  }

  private[this] def isRunningReorderAnimation: Boolean = views exists (_.isRunningAnimation)

}

case class CollectionItem(
  presenter: LauncherPresenter,
  originalPosition: Int)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.application)
  with CollectionItemStyle {

  val positionDraggingItem = numSpaces

  var positionInGrid = originalPosition

  var collection: Option[Collection] = None

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var layout = slot[LinearLayout]

  var icon = slot[ImageView]

  var name = slot[TextView]

  addView(
    (l[LinearLayout](
      w[ImageView] <~ wire(icon) <~ iconStyle,
      w[TextView] <~ wire(name) <~ nameStyle
    ) <~
      wire(layout) <~
      collectionItemStyle <~
      vUseLayerHardware).get)

  def populate(collection: Collection): Unit = {
    this.collection = Some(collection)
    android.util.Log.d("9cards", s"${this.collection.get.name} - ${this.collection.get.position}")
    val resIcon = iconCollectionWorkspace(collection.icon)
    ((layout <~
      On.click {
        Ui(presenter.goToCollection(icon, this.collection))
      } <~
      On.longClick {
        presenter.startDrag(this.collection, positionInGrid)
        (this.collection map { _ =>
          (this <~ vGone) ~ Ui(positionInGrid = positionDraggingItem) ~ (layout <~ startDrag())
        } getOrElse Ui.nop).run
        Ui(true)
      }) ~
      (icon <~ ivSrc(resIcon) <~ vBackground(createBackground(collection.themedColorIndex))) ~
      (name <~ tvText(collection.name))).run
  }

  def resetOriginalPosition(): Ui[Any] = Ui(positionInGrid = originalPosition)

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