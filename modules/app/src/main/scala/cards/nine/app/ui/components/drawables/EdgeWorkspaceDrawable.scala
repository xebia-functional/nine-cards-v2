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

package cards.nine.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import cards.nine.commons.ops.ColorOps._
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class EdgeWorkspaceDrawable(left: Boolean)(implicit contextWrapper: ContextWrapper)
    extends Drawable {

  val color = resGetColor(R.color.collection_workspace_feedback_drop)

  lazy val fillPaint = {
    val paint = new Paint
    paint.setColor(color)
    paint.setAntiAlias(true)
    paint
  }

  lazy val strokePaint = {
    val paint = new Paint
    paint.setStrokeWidth(resGetDimensionPixelSize(R.dimen.stroke_default))
    paint.setColor(color.alpha(1f))
    paint.setAntiAlias(true)
    paint
  }

  override def setColorFilter(cf: ColorFilter): Unit =
    fillPaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = fillPaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    canvas.drawRect(bounds, fillPaint)
    // top line
    canvas.drawLine(0, 0, bounds.width(), 0, strokePaint)
    // bottom line
    canvas.drawLine(0, bounds.height(), bounds.width(), bounds.height(), strokePaint)
    // left or right line
    if (left) {
      canvas.drawLine(bounds.width(), 0, bounds.width(), bounds.height(), strokePaint)
    } else {
      canvas.drawLine(0, 0, 0, bounds.height(), strokePaint)
    }
  }
}
