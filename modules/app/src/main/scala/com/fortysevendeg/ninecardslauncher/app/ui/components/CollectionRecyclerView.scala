package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.ViewGroup.{OnHierarchyChangeListener, LayoutParams}
import android.view.animation.AnimationUtils
import android.view.animation.GridLayoutAnimationController.AnimationParameters
import android.view.{View, MotionEvent}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import macroid.FullDsl._
import macroid.{Tweak, ContextWrapper}

class CollectionRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends RecyclerView(context, attr, defStyleAttr) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  var disableScroll = false

  var enableAnimation = false

  def createScrollListener(
    scrolled: (Int, Int, Int) => Int,
    scrollStateChanged: (Int, RecyclerView, Int) => Unit) = {
    val sl = new NineOnScrollListener {
      var scrollY = scrollListener map (_.scrollY) getOrElse 0
      override def onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int): Unit = {
        super.onScrolled(recyclerView, dx, dy)
        scrollY = scrolled(scrollY, dx, dy)
      }
      override def onScrollStateChanged(recyclerView: RecyclerView, newState: Int): Unit = {
        super.onScrollStateChanged(recyclerView, newState)
        scrollStateChanged(scrollY, recyclerView, newState)
      }
    }
    clearOnScrollListeners()
    addOnScrollListener(sl)
    scrollListener = Option(sl)
  }

  var scrollListener: Option[NineOnScrollListener] = None

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = if(disableScroll) {
    true
  } else {
    super.dispatchTouchEvent(ev)
  }

  override def attachLayoutAnimationParameters(child: View, params: LayoutParams, index: Int, count: Int): Unit =
    (enableAnimation, Option(getAdapter), Option(getLayoutManager)) match {
      case (true, Some(_), Some(layoutManager: GridLayoutManager)) =>
        val animationParams = Option(params.layoutAnimationParameters) match {
          case Some(animParams: AnimationParameters) => animParams
          case _ =>
            val animParams = new AnimationParameters()
            params.layoutAnimationParameters = animParams
            animParams
        }
        val columns = layoutManager.getSpanCount
        animationParams.count = count
        animationParams.index = index
        animationParams.columnsCount = columns
        animationParams.rowsCount = count / columns
        val invertedIndex = count - 1 - index
        animationParams.column = columns - 1 - (invertedIndex % columns)
        animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns
      case _ => super.attachLayoutAnimationParameters(child, params, index, count)
    }

  abstract class NineOnScrollListener
    extends OnScrollListener {
    var scrollY: Int
  }

}

object CollectionRecyclerViewTweaks {
  type W = CollectionRecyclerView

  def nrvDisableScroll(disable: Boolean) = Tweak[W](_.disableScroll = disable)

  def nrvEnableAnimation(res: Int)(implicit contextWrapper: ContextWrapper) = Tweak[W]{ view =>
    view.enableAnimation = true
    view.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(contextWrapper.application, res))
  }

  def nrvScheduleLayoutAnimation = Tweak[W](_.scheduleLayoutAnimation())

  def nrvCollectionScrollListener(
    scrolled: (Int, Int, Int) => Int,
    scrollStateChanged: (Int, RecyclerView, Int) => Unit
  )(implicit context: ContextWrapper): Tweak[W] = Tweak[W](_.createScrollListener(scrolled, scrollStateChanged))

  def nrvResetPositions(implicit context: ContextWrapper): Tweak[W] = Tweak[W] { recyclerView =>
    recyclerView.setOnHierarchyChangeListener(new OnHierarchyChangeListener {
      override def onChildViewAdded(parent: View, child: View): Unit = reset
      override def onChildViewRemoved(parent: View, child: View): Unit = reset
      private[this] def reset = recyclerView.getLayoutManager match {
        case layoutManager: GridLayoutManager =>
          val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
          0 until recyclerView.getChildCount foreach { position =>
            val newPosition = position + firstVisiblePosition
            val v = recyclerView.getChildAt(position)
            runUi(v <~ vTag2(newPosition))
          }
      }

    })
  }

}