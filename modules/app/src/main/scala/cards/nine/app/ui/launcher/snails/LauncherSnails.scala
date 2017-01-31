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
import android.view.View
import macroid.extras.ResourcesExtras._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object LauncherSnails {

  def pagerAppear(implicit context: ContextWrapper): Snail[View] =
    Snail[View] { view =>
      val duration = resGetInteger(R.integer.anim_duration_pager_appear)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.setScaleX(.7f)
      view.setScaleY(.7f)
      view.setAlpha(.7f)
      view.animate
        .alpha(1)
        .scaleX(1)
        .scaleY(1)
        .setDuration(duration)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        })
        .start()
      animPromise.future
    }

  def fade(out: Boolean = false)(implicit context: ContextWrapper): Snail[View] = {
    val duration = resGetInteger(R.integer.anim_duration_pager_appear)
    if (out) {
      applyFadeOut(Some(duration))
    } else {
      applyFadeIn(Some(duration))
    }
  }

}
