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
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders.LauncherWorkSpaceCollectionsHolder.positionDraggingItem
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.types.{DragLauncherType, ReorderCollection}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{CollectionItemStyle, CollectionsGroupStyle, LauncherPresenter}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceCollectionsHolder(
  presenter: LauncherPresenter,
  parentDimen: Dimen)(implicit contextWrapper: ContextWrapper)
  extends LauncherWorkSpaceHolder
  with CollectionsGroupStyle {

  val handler = new Handler()

  var task: Option[Runnable] = None

  val sizeEdgeBetweenWorkspaces = resGetDimensionPixelSize(R.dimen.size_edge_between_workspaces)

  val widthSpace = parentDimen.width / numInLine

  val heightSpace = parentDimen.height / numInLine

  var positionScreen = 0

  var countCollectionScreens = 0

  var grid: Option[GridLayout] = slot[GridLayout]

  val views: Seq[CollectionItem] = 0 until numSpaces map (_ => new CollectionItem(presenter))

  addView((l[GridLayout]() <~ wire(grid) <~ collectionGridStyle).get)

  (grid <~
    glAddViews(
      views = views,
      columns = numInLine,
      rows = numInLine,
      width = widthSpace,
      height = heightSpace) <~
    vDragListener()).run

  def populate(collections: Seq[Collection], positionScreen: Int, countCollectionScreens: Int): Ui[_] = {
    this.positionScreen = positionScreen
    this.countCollectionScreens = countCollectionScreens
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
        view <~ ciPopulate(collection)
      } getOrElse view <~ ciOff()
    }
    Ui.sequence(uiSeq: _*)
  }

  def prepareItemsScreenInReorder(position: Int): Ui[Any] = {
    val startReorder = presenter.statuses.startPositionReorderMode
    val screenOfCollection = (toPositionCollection(0) <= startReorder) && (toPositionCollection(numSpaces) > startReorder)
    if (screenOfCollection) {
      Ui.sequence(views map { view =>
        if (view.positionInGrid == startReorder) {
          view.convertToDraggingItem() ~ (view <~ vInvisible)
        } else if (view.collection.isEmpty) {
          view <~ vInvisible
        } else {
          view <~ vVisible
        }
      }: _*) ~ reorder(startReorder, position, animation = false)
    } else {
      Ui.sequence(views map { view =>
        if (view.positionInGrid == toPositionCollection(position)) {
          view.convertToDraggingItem() ~ (view <~ vInvisible)
        } else if (view.collection.isEmpty) {
          view <~ vInvisible
        } else {
          view <~ vVisible
        }
      }: _*)
    }
  }

  private[this] def resetAllPositions(): Ui[Any] = Ui.sequence(views map { view =>
    view <~ backToPosition() <~ (view.collection map (_ => vVisible) getOrElse vInvisible)
  }: _*)

  private[this] def vDragListener(): Tweak[View] = Tweak[View] { view =>
    view.setOnDragListener(new OnDragListener {
      override def onDrag(v: View, event: DragEvent): Boolean = {
        (event.getAction, DragLauncherType(event.getLocalState), presenter.statuses.isReordering(), isRunningReorderAnimation) match {
          case (ACTION_DRAG_LOCATION, ReorderCollection, true, false) =>
            val lastCurrentPosition = presenter.statuses.currentPositionReorderMode
            val x = event.getX
            val y = event.getY
            val canMoveToLeft = positionScreen > 0
            val canMoveToRight = positionScreen < countCollectionScreens - 1
            (calculateEdge(x), canMoveToLeft, canMoveToRight) match {
              case (LeftEdge, true, _) =>
                delayedTask(() => {
                  resetAllPositions().run
                  presenter.draggingToPreviousScreen(toPositionCollection(numSpaces - 1) - numSpaces)
                })
              case (RightEdge, _, true) =>
                delayedTask(() => {
                  resetAllPositions().run
                  presenter.draggingToNextScreen(toPositionCollection(0) + numSpaces)
                })
              case (NoEdge, _ , _) =>
                clearTask()
                val space = calculatePosition(x, y)
                val existCollectionInSpace = (views.lift(space) flatMap(_.collection)).isDefined
                val currentPosition = toPositionCollection(space)
                if (existCollectionInSpace && lastCurrentPosition != currentPosition) {
                  reorder(lastCurrentPosition, currentPosition).run
                  presenter.draggingTo(currentPosition)
                }
              case _ =>
            }
          case (ACTION_DROP | ACTION_DRAG_ENDED, ReorderCollection, true, false) =>
            resetPlaces.run
            presenter.drop()
          case (ACTION_DROP | ACTION_DRAG_ENDED, ReorderCollection, true, true) =>
            // we are waiting that the animation is finished in order to reset views
            delayedTask(() => {
              resetPlaces.run
              presenter.drop()
            }, resGetInteger(R.integer.anim_duration_normal))
          case _ =>
        }
        true
      }
    })
  }

  private[this] def reorder(currentPosition: Int, toPosition: Int, animation: Boolean = true): Ui[Any] =
    if (currentPosition < toPosition) {
      val from = currentPosition + 1
      val to = toPosition
      val transforms = from to to map { pos =>
        move(pos, pos - 1, animation)
      }
      val updatePositions = from to to map { pos =>
        getView(pos) map (view => Ui(view.positionInGrid = pos - 1)) getOrElse Ui.nop
      }
      Ui.sequence(transforms ++ updatePositions:_*)
    } else if (currentPosition > toPosition) {
      val from = toPosition
      val to = currentPosition
      val transforms = from until to map { pos =>
        move(pos, pos  + 1, animation)
      }
      val updatePositions = from until to map { pos =>
        getView(pos) map (view => Ui(view.positionInGrid = pos + 1)) getOrElse Ui.nop
      }
      Ui.sequence(transforms ++ updatePositions:_*)
    } else {
      Ui.nop
    }

  private[this] def move(from: Int, to: Int, animation: Boolean): Ui[Any] = {
    val (fromColumn, fromRow) = place(from)
    val (toColumn, toRow) = place(to)
    val displacementHorizontal = (toColumn - fromColumn) * widthSpace
    val displacementVertical = (toRow - fromRow) * heightSpace
    val view = getView(from)
    if (animation) {
      view <~ applyAnimation(
        xBy = Some(displacementHorizontal),
        yBy = Some(displacementVertical))
    } else {
      view <~
        vTranslationX(displacementHorizontal) <~
        vTranslationY(displacementVertical)
    }
  }

  private[this] def resetPlaces: Ui[Any] = {
    val start = toPositionGrid(presenter.statuses.startPositionReorderMode)
    val current = toPositionGrid(presenter.statuses.currentPositionReorderMode)
    val collectionsReordered = (views map (_.collection)).reorder(start, current).zipWithIndex map {
      case (collection, index) =>
        val positionCollection = toPositionCollection(index)
        // if it's the collection that the user is dragging, we put the collection stored.
        // when the user is reordering in other screen the collection isn't the same on the view
        if (positionCollection == presenter.statuses.currentPositionReorderMode) {
          presenter.statuses.collectionReorderMode
        } else {
          collection map (_.copy(position = positionCollection))
        }
    }
    Ui.sequence(views.zip(collectionsReordered) map {
      case (view, Some(collection)) =>
        view  <~
          vClearAnimation <~
          backToPosition() <~
          ciPopulate(collection)
      case _ => Ui.nop
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

  private[this] def calculateEdge(x: Float): Edge = if (x < sizeEdgeBetweenWorkspaces) {
    LeftEdge
  } else if (x > parentDimen.width - sizeEdgeBetweenWorkspaces) {
    RightEdge
  } else {
    NoEdge
  }

  private[this] def toPositionCollection(position: Int) = position + (positionScreen * numSpaces)

  private[this] def toPositionGrid(position: Int) = position - (positionScreen * numSpaces)

  private[this] def isRunningReorderAnimation: Boolean = views exists (_.isRunningAnimation)

  private[this] def delayedTask(runTask: () => Unit, duration: Int = 500): Unit = if (task.isEmpty) {
    val runnable = new Runnable {
      override def run(): Unit = runTask()
    }
    task = Option(runnable)
    handler.postDelayed(runnable, duration)
  }

  private[this] def clearTask(): Unit =  if (task.isDefined) {
    task foreach handler.removeCallbacks
    task = None
  }

  private[this] def backToPosition() = vTranslationX(0) + vTranslationY(0)
}

case class CollectionItem(
  presenter: LauncherPresenter)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.application)
  with CollectionItemStyle {

  var positionInGrid = 0

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
    positionInGrid = collection.position
    val resIcon = iconCollectionWorkspace(collection.icon)
    ((layout <~
      On.click {
        Ui(presenter.goToCollection(icon, this.collection))
      } <~
      On.longClick {
        presenter.startDrag(this.collection, positionInGrid)
        (this.collection map { _ =>
          (this <~ vInvisible) ~ convertToDraggingItem() ~ (layout <~ startDrag())
        } getOrElse Ui.nop).run
        Ui(true)
      }) ~
      (icon <~ ivSrc(resIcon) <~ vBackground(createBackground(collection.themedColorIndex))) ~
      (name <~ tvText(collection.name))).run
  }

  def convertToDraggingItem(): Ui[Any] = Ui(positionInGrid = positionDraggingItem)

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

object LauncherWorkSpaceCollectionsHolder {
  val positionDraggingItem = Int.MaxValue
}

object CollectionItemTweaks {
  type W = CollectionItem

  def ciPopulate(collection: Collection) = vVisible + Tweak[W](_.populate(collection))

  def ciOff() = vInvisible + Tweak[W](_.collection = None)
}

sealed trait Edge

case object LeftEdge extends Edge

case object RightEdge extends Edge

case object NoEdge extends Edge