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

package cards.nine.app.ui.components.widgets

import android.content.Context
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.view.animation.GridLayoutAnimationController.AnimationParameters
import android.view.{MotionEvent, View}
import cards.nine.commons._
import macroid.Contexts

class CollectionRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends RecyclerView(context, attr, defStyleAttr)
    with Contexts[View] {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  var statuses = CollectionRecyclerStatuses()

  override def dispatchTouchEvent(ev: MotionEvent): Boolean =
    if (statuses.disableScroll) {
      true
    } else {
      super.dispatchTouchEvent(ev)
    }

  override def attachLayoutAnimationParameters(
      child: View,
      params: LayoutParams,
      index: Int,
      count: Int): Unit =
    (statuses.enableAnimation, Option(getLayoutManager)) match {
      case (true, Some(layoutManager: GridLayoutManager)) =>
        val animationParams = Option(params.layoutAnimationParameters) match {
          case Some(animParams: AnimationParameters) => animParams
          case _ =>
            val animParams = new AnimationParameters()
            params.layoutAnimationParameters = animParams
            animParams
        }
        val columns = layoutManager.getSpanCount
        animationParams.count = count
        animationParams.index = index
        animationParams.columnsCount = columns
        animationParams.rowsCount = count / columns
        val invertedIndex = count - 1 - index
        animationParams.column = columns - 1 - (invertedIndex % columns)
        animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns
      case _ =>
        super.attachLayoutAnimationParameters(child, params, index, count)
    }

}

case class CollectionRecyclerStatuses(
    disableScroll: Boolean = false,
    enableAnimation: Boolean = false)
