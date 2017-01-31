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

class DrawerBackgroundDrawable(
    color: Int = 0,
    horizontalPadding: Int,
    verticalPadding: Int,
    radius: Int)
    extends Drawable {

  lazy val backgroundPaint = {
    val paint = new Paint
    paint.setColor(color)
    paint
  }

  override def setColorFilter(cf: ColorFilter): Unit =
    backgroundPaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = backgroundPaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    canvas.drawRoundRect(
      new RectF(
        horizontalPadding,
        verticalPadding,
        bounds.width() - horizontalPadding,
        bounds.height() - verticalPadding),
      radius,
      radius,
      backgroundPaint)
  }

}
