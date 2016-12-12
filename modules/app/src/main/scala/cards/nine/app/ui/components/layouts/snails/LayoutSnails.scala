package cards.nine.app.ui.components.layouts.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.DecelerateInterpolator
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewTweaks._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.commons.javaNull
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.concurrent.Promise

object TabsSnails {

  def showTabs(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val maxHeight = resGetDimensionPixelSize(R.dimen.pulltotabs_max_height)
      val height = resGetDimensionPixelSize(R.dimen.pulltotabs_height)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      view.setY(-maxHeight)
      view.setVisibility(View.VISIBLE)

      view.animate()
        .setInterpolator(new DecelerateInterpolator)
        .y(height-maxHeight)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()

      animPromise.future
  }

  def hideTabs(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val maxHeight = resGetDimensionPixelSize(R.dimen.pulltotabs_max_height)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      view.animate()
        .setInterpolator(new DecelerateInterpolator)
        .y(-maxHeight)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            view.setVisibility(View.GONE)
            animPromise.trySuccess(())
          }
        }).start()

      animPromise.future
  }

  def hideList(implicit context: ContextWrapper): Snail[RecyclerView] = Snail[RecyclerView] {
    view =>
      val height = resGetDimensionPixelSize(R.dimen.pulltotabs_height)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      view.animate()
        .setInterpolator(new DecelerateInterpolator)
        .y(height)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()

      animPromise.future
  }

  def showList(implicit context: ContextWrapper): Snail[RecyclerView] = Snail[RecyclerView] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      view.animate()
        .setInterpolator(new DecelerateInterpolator)
        .y(0)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()

      animPromise.future
  }

}
