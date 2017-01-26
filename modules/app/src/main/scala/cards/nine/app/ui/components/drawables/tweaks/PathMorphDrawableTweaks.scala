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

package cards.nine.app.ui.components.drawables.tweaks

import android.widget.ImageView
import cards.nine.app.ui.components.drawables.PathMorphDrawable
import macroid.Tweak

import scala.util.Try

object PathMorphDrawableTweaks {
  type W = ImageView

  def pmdAnimIcon(icon: Int) = Tweak[W] { view =>
    view.getDrawable.asInstanceOf[PathMorphDrawable].setToTypeIcon(icon)
    view.getDrawable.asInstanceOf[PathMorphDrawable].start()
  }

  def pmdChangeIcon(icon: Int) =
    Tweak[W](view => Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setTypeIcon(icon)))

  def pmdColor(color: Int) =
    Tweak[W](view => Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setColor(color)))

  def pmdColorResource(color: Int) =
    Tweak[W](view => Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setColorResource(color)))

  def pmdStroke(stroke: Float) =
    Tweak[W](view => Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setStroke(stroke)))
}
