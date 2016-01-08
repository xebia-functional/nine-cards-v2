package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks

import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.View
import android.view.ViewGroup.OnHierarchyChangeListener
import android.view.animation.AnimationUtils
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.SearchBoxAnimatedController
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.snails.RippleBackgroundSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.{DrawerRecyclerView, CollectionRecyclerView, RippleBackgroundView, TintableImageView}
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

import scala.concurrent.ExecutionContext.Implicits.global

object TintableImageViewTweaks {
  type W = TintableImageView

  def tivDefaultColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W] {
    view =>
      view.defaultColor = color
      view.setTint(color)
  }

  def tivPressedColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W](_.pressedColor = color)

}

object RippleBackgroundViewTweaks {

  def rbvColor(color: Int, forceFade: Boolean = false)(implicit contextWrapper: ContextWrapper) = Tweak[RippleBackgroundView] { view =>
    runUi(view <~~ ripple(color, forceFade))
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
        case _ =>
      }

    })
  }

}

object DrawerRecyclerViewTweaks {
  type W = DrawerRecyclerView

  def drvDisableScroll(disable: Boolean) = Tweak[W](view => view.statuses = view.statuses.copy(disableScroll = disable))

  def drvAddController(controller: SearchBoxAnimatedController) = Tweak[W](_.animatedController = Some(controller))

}
