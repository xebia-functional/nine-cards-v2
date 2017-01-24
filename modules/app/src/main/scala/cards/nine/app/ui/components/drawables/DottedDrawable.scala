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
import macroid.extras.ResourcesExtras._
import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class DottedDrawable(horizontal: Boolean = true)(implicit contextWrapper: ContextWrapper)
    extends Drawable {

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default).toFloat

  val paint: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setDither(true)
    paint.setColor(resGetColor(R.color.stroke_rules_moment))
    paint.setStrokeWidth(resGetDimensionPixelSize(R.dimen.stroke_thin))
    paint.setStyle(Paint.Style.STROKE)
    paint.setPathEffect(new DashPathEffect(Array(paddingDefault, paddingDefault), 0))
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val path   = new Path()
    path.moveTo(0, 0)
    if (horizontal) path.lineTo(bounds.width(), 0)
    else path.lineTo(0, bounds.height())
    canvas.drawPath(path, paint)
  }

  override def setColorFilter(cf: ColorFilter): Unit = paint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

}
