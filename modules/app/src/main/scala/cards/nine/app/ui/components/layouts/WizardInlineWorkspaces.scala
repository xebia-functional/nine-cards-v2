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
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.TextViewTweaks._

class WizardInlineWorkspaces(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends AnimatedWorkSpaces[WizardInlineWidgetsHolder, WizardInlineData](
      context,
      attr,
      defStyleAttr)
    with Contexts[View] {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  override def createEmptyView(): WizardInlineWidgetsHolder =
    new WizardInlineWidgetsHolder

  override def createView(viewType: Int): WizardInlineWidgetsHolder =
    new WizardInlineWidgetsHolder

  override def populateView(
      view: Option[WizardInlineWidgetsHolder],
      data: WizardInlineData,
      viewType: Int,
      position: Int): Ui[_] =
    view match {
      case Some(v: WizardInlineWidgetsHolder) => v.bind(data)
      case _                                  => Ui.nop
    }

}

case class WizardInlineData(image: Int, title: String, message: String)

class WizardInlineWidgetsHolder(implicit contextWrapper: ContextWrapper)
    extends LinearLayout(contextWrapper.application)
    with TypedFindView {

  lazy val image = findView(TR.wizard_inline_item_image)

  lazy val title = findView(TR.wizard_inline_item_title)

  lazy val message = findView(TR.wizard_inline_item_message)

  LayoutInflater.from(contextWrapper.application).inflate(R.layout.wizard_inline_step, this)

  def bind(data: WizardInlineData): Ui[_] =
    (image <~ ivSrc(data.image)) ~
      (title <~ tvText(data.title)) ~
      (message <~ tvText(data.message))

}
