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

package cards.nine.app.ui.components.layouts

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.{FrameLayout, ImageView}
import macroid.extras.DeviceVersion.Lollipop
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._

class WorkspaceItemMenu(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends FrameLayout(context, attr, defStyleAttr)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.workspace_item_menu, this)

  private[this] val title = Option(findView(TR.workspace_title))

  val icon = Option(findView(TR.workspace_icon))

  (icon <~ fabStyle).run

  def populate(backgroundColor: Int, res: Int, text: Int): Ui[Any] =
    (title <~ tvText(text)) ~
      (icon <~
        ivSrc(res) <~
        (Lollipop ifSupportedThen {
          vBackgroundColor(backgroundColor)
        } getOrElse {
          val drawable = new ShapeDrawable(new OvalShape)
          drawable.getPaint.setColor(backgroundColor)
          vBackground(drawable)
        }))

  private[this] def fabStyle: Tweak[ImageView] =
    Lollipop ifSupportedThen {
      vElevation(resGetDimension(R.dimen.elevation_fab_button)) + vCircleOutlineProvider()
    } getOrElse Tweak.blank

}
