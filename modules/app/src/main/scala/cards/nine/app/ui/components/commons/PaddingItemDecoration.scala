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

package cards.nine.app.ui.components.commons

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import android.view.View
import cards.nine.app.ui.preferences.commons.CardPadding
import macroid.ContextWrapper

class PaddingItemDecoration(implicit contextWrapper: ContextWrapper)
    extends RecyclerView.ItemDecoration {

  val padding = CardPadding.getPadding

  override def getItemOffsets(
      outRect: Rect,
      view: View,
      parent: RecyclerView,
      state: State): Unit = {
    outRect.top = padding
    outRect.bottom = padding
    outRect.left = padding
    outRect.right = padding
  }

}
