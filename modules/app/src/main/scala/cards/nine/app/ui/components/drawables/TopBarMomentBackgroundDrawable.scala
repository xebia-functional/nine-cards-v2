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
import cards.nine.models.types.theme.SearchBackgroundColor
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class TopBarMomentBackgroundDrawable(
    implicit theme: NineCardsTheme,
    contextWrapper: ContextWrapper)
    extends Drawable {

  val color = theme.get(SearchBackgroundColor)

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val radiusIcon = resGetDimensionPixelSize(R.dimen.radius_icon_top_bar_moment)

  val paint: Paint = {
    val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
    paint.setColor(color)
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    canvas.drawRoundRect(
      new RectF(
        bounds.left + radius,
        bounds.top + radius,
        bounds.right - radius,
        bounds.bottom - radius),
      radiusIcon,
      radiusIcon,
      paint)
  }

  override def setColorFilter(cf: ColorFilter): Unit = paint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT
}
