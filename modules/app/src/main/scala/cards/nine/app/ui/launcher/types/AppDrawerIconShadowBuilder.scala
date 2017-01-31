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

package cards.nine.app.ui.launcher.types

import android.graphics.{Canvas, Point}
import android.view.View
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class AppDrawerIconShadowBuilder(view: View)(implicit contextWrapper: ContextWrapper)
    extends View.DragShadowBuilder(view) {

  val size: Int =
    (resGetDimensionPixelSize(R.dimen.size_icon_app_medium) * 1.2f).toInt

  val scale: Float = size.toFloat / view.getWidth.toFloat

  override def onProvideShadowMetrics(shadowSize: Point, shadowTouchPoint: Point): Unit = {
    shadowSize.set(size, size)
    shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2)
  }

  override def onDrawShadow(canvas: Canvas): Unit = {
    Option(getView) foreach { view =>
      canvas.save()
      canvas.scale(scale, scale)
      view.draw(canvas)
      canvas.restore()
    }
  }
}
