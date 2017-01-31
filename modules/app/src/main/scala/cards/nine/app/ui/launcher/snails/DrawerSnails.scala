/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.launcher.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import android.annotation.TargetApi
import android.os.Build
import android.view.animation.DecelerateInterpolator
import android.view.{View, ViewAnimationUtils}
import macroid.extras.SnailsUtils._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.preferences.commons.{
  AppDrawerAnimationCircle,
  AppDrawerAnimationValue,
  SpeedAnimations
}
import cards.nine.commons._
import macroid.extras.SnailsUtils
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object DrawerSnails {

  def openAppDrawer(animation: AppDrawerAnimationValue, source: View)(
      implicit context: ContextWrapper): Snail[View] = Snail[View] { view =>
    view.clearAnimation()
    view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
    val animPromise = Promise[Unit]()

    animation match {
      case anim @ AppDrawerAnimationCircle if anim.isSupported =>
        reveal(source, view)(animPromise.trySuccess(()))
      case _ => fadeIn(view)(animPromise.trySuccess(()))
    }

    animPromise.future
  }

  def closeAppDrawer(animation: AppDrawerAnimationValue, source: View)(
      implicit context: ContextWrapper): Snail[View] = Snail[View] { view =>
    view.clearAnimation()
    view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
    val animPromise = Promise[Unit]()

    animation match {
      case anim @ AppDrawerAnimationCircle if anim.isSupported =>
        reveal(source, view, in = false)(animPromise.trySuccess(()))
      case _ => fadeOut(view)(animPromise.trySuccess(()))
    }

    animPromise.future
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private[this] def reveal(source: View, view: View, in: Boolean = true)(
      animationEnd: => Unit = ())(implicit context: ContextWrapper): Unit = {
    val (cx, cy)   = source.calculateAnchorViewPosition
    val fromRadius = source.getWidth / 2
    val toRadius   = SnailsUtils.calculateRadius(width = cx + fromRadius, height = cy + fromRadius)

    val (startRadius, endRadius): (Float, Float) =
      if (in) (fromRadius, toRadius) else (toRadius, fromRadius)

    val reveal: Animator = ViewAnimationUtils
      .createCircularReveal(view, cx + fromRadius, cy + fromRadius, startRadius, endRadius)
    reveal.setDuration(SpeedAnimations.getDuration)
    reveal.addListener(new AnimatorListenerAdapter {
      override def onAnimationStart(animation: Animator): Unit = {
        super.onAnimationStart(animation)
        if (in) {
          view.setAlpha(1)
          view.setVisibility(View.VISIBLE)
        }
      }

      override def onAnimationEnd(animation: Animator) = {
        super.onAnimationEnd(animation)
        if (!in) view.setVisibility(View.GONE)
        view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
        animationEnd
      }
    })
    reveal.setInterpolator(new DecelerateInterpolator)
    reveal.start()
  }

  private[this] def fadeIn(view: View)(animationEnd: => Unit = ())(
      implicit context: ContextWrapper): Unit = {
    view.setAlpha(0f)
    view
      .animate()
      .setDuration(SpeedAnimations.getDuration)
      .setInterpolator(new DecelerateInterpolator)
      .alpha(1f)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationStart(animation: Animator): Unit = {
          super.onAnimationStart(animation)
          view.setVisibility(View.VISIBLE)
        }

        override def onAnimationEnd(animation: Animator) = {
          super.onAnimationEnd(animation)
          view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
          animationEnd
        }
      })
      .start()
  }

  private[this] def fadeOut(view: View)(animationEnd: => Unit = ())(
      implicit context: ContextWrapper): Unit = {
    view.setAlpha(1f)
    view
      .animate()
      .setDuration(SpeedAnimations.getDuration)
      .setInterpolator(new DecelerateInterpolator)
      .alpha(0f)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setVisibility(View.GONE)
          view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
          animationEnd
        }
      })
      .start()
  }

}
