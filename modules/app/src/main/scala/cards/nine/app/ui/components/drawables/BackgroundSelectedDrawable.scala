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
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{CardBackgroundColor, PrimaryColor}
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class BackgroundSelectedDrawable(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
    extends Drawable {

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin).toFloat

  val paintStroke: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setDither(true)
    paint
  }

  val paintBack: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setDither(true)
    paint
  }

  selected(s = false)

  def selected(s: Boolean) = {
    if (s) {
      paintStroke.setColor(theme.get(PrimaryColor))
      paintBack.setColor(theme.get(PrimaryColor))
    } else {
      paintStroke.setColor(theme.get(PrimaryColor))
      paintBack.setColor(theme.get(CardBackgroundColor))
    }
    invalidateSelf()
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds  = getBounds
    val middleX = bounds.width() / 2
    val middleY = bounds.height() / 2
    val radius  = if (middleX < middleY) middleX else middleY
    canvas.drawCircle(middleX, middleY, radius, paintStroke)
    canvas.drawCircle(middleX, middleY, radius - stroke, paintBack)
  }

  override def setColorFilter(cf: ColorFilter): Unit =
    paintBack.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paintBack.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

}
