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

package cards.nine.app.ui.commons.dialogs.wizard

import android.view.ViewGroup
import com.fortysevendeg.ninecardslauncher.TR

class WizardInlineDOM(viewGroup: ViewGroup) {

  import cards.nine.app.ui.commons.ViewGroupFindViews._

  lazy val wizardInlineWorkspace =
    findView(TR.wizard_inline_workspace).run(viewGroup)

  lazy val wizardInlinePagination =
    findView(TR.wizard_inline_pagination_panel).run(viewGroup)

  lazy val wizardInlineSkip = findView(TR.wizard_inline_skip).run(viewGroup)

  lazy val wizardInlineGotIt = findView(TR.wizard_inline_got_it).run(viewGroup)

}

trait WizardListener {

  def dismissWizard(): Unit

}
