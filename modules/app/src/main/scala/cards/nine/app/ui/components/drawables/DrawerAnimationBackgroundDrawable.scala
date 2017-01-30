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
import macroid.extras.SnailsUtils

class DrawerAnimationBackgroundDrawable(backgroundColor: Int, circleColor: Int) extends Drawable {

  private[this] var statuses = BackgroundDrawerAnimationStatuses()

  lazy val circlePaint = {
    val paint = new Paint
    paint.setColor(circleColor)
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val radius =
      SnailsUtils.calculateRadius(statuses.x, bounds.centerY(), bounds.width(), bounds.height())
    canvas.drawColor(backgroundColor)
    canvas.drawCircle(statuses.x, bounds.centerY(), radius * statuses.percentage, circlePaint)
  }

  override def setColorFilter(cf: ColorFilter): Unit =
    circlePaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = circlePaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  def setData(p: Float, x: Int) = {
    statuses = statuses.copy(percentage = p, x = x)
    invalidateSelf()
  }

}

case class BackgroundDrawerAnimationStatuses(percentage: Float = 0, x: Int = 0)
