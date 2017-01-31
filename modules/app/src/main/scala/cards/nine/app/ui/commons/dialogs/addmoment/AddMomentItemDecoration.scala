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

package cards.nine.app.ui.commons.dialogs.addmoment

import android.graphics.{Canvas, Paint}
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import cards.nine.models.NineCardsTheme
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class AddMomentItemDecoration(implicit theme: NineCardsTheme, contextWrapper: ContextWrapper)
    extends RecyclerView.ItemDecoration {

  val paint: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setColor(theme.getLineColor)
    paint.setStrokeWidth(resGetDimensionPixelSize(R.dimen.stroke_thin))
    paint.setStyle(Paint.Style.STROKE)
    paint
  }

  override def onDraw(c: Canvas, recyclerView: RecyclerView, state: State): Unit = {
    super.onDraw(c, recyclerView, state)
    (0 to recyclerView.getChildCount flatMap (i => Option(recyclerView.getChildAt(i)))) foreach {
      view =>
        c.drawLine(view.getLeft, view.getBottom, view.getRight, view.getBottom, paint)
    }
  }

}
