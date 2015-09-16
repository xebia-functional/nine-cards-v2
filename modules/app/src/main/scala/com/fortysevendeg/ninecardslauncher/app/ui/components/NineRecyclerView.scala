package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.view.animation.AnimationUtils
import android.view.animation.GridLayoutAnimationController.AnimationParameters
import android.view.{View, MotionEvent}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Tweak, ContextWrapper}

class NineRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends RecyclerView(context, attr, defStyleAttr) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  var disableScroll = false

  var enableAnimation = false

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

}

object NineRecyclerViewTweaks {
  type W = NineRecyclerView

  def nrvDisableScroll(disable: Boolean) = Tweak[W](_.disableScroll = disable)

  def nrvEnableAnimation(res: Int)(implicit contextWrapper: ContextWrapper) = Tweak[W]{ view =>
    view.enableAnimation = true
    view.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(contextWrapper.application, res))
  }

  def nrvScheduleLayoutAnimation = Tweak[W](_.scheduleLayoutAnimation())

}