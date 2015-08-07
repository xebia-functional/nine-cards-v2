package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.animation.{Animator, AnimatorListenerAdapter}
import android.annotation.TargetApi
import android.os.Build
import android.support.v7.widget.Toolbar
import android.view.{ViewAnimationUtils, View}
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Ui, ContextWrapper, Snail}
import macroid.FullDsl._

import scala.concurrent.Promise

object Snails {

  def changeIcon(resDrawable: Int, fromLeft: Boolean)(implicit context: ContextWrapper): Snail[ImageView] = Snail[ImageView] {
    view =>
      val distance = if (fromLeft) -resGetDimensionPixelSize(R.dimen.padding_default) else resGetDimensionPixelSize(R.dimen.padding_default)
      val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.animate.translationX(-distance).alpha(0f).scaleX(0.7f).scaleY(0.7f).setDuration(duration).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setTranslationX(distance)
          view.setImageResource(resDrawable)
          view.animate.translationX(0).alpha(1).scaleX(1).scaleY(1).setDuration(duration).setListener(new AnimatorListenerAdapter {
            override def onAnimationEnd(animation: Animator) {
              super.onAnimationEnd(animation)
              view.setRotation(0)
              view.setLayerType(View.LAYER_TYPE_NONE, null)
              animPromise.success()
            }
          }).start()
        }
      }).start()
      animPromise.future
  }

  def fadeToolBar(end: () => Ui[_])(implicit context: ContextWrapper): Snail[Toolbar] = Snail[Toolbar] {
    view =>
      val duration = resGetInteger(R.integer.anim_duration_appear_toolbar)
      view.setAlpha(0)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.animate.alpha(1f).setDuration(duration).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          runUi(end())
        }
      }).start()
      animPromise.future
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  def ripple(x: Int, y: Int) = Snail[Toolbar] {
    view =>
      val animPromise = Promise[Unit]()
      val anim = ViewAnimationUtils.createCircularReveal(view, x, y, 0, view.getWidth)
      anim.addListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.success()
        }
      })
      anim.start()
      animPromise.future
  }

}
