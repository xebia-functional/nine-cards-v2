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

sealed trait WizardInlineType

case object LauncherWizardInline extends WizardInlineType

case object AppDrawerWizardInline extends WizardInlineType

case object ProfileWizardInline extends WizardInlineType

case object CollectionsWizardInline extends WizardInlineType

object WizardInlineType {

  def apply(name: String): WizardInlineType = name match {
    case n if n == LauncherWizardInline.toString    => LauncherWizardInline
    case n if n == AppDrawerWizardInline.toString   => AppDrawerWizardInline
    case n if n == CollectionsWizardInline.toString => CollectionsWizardInline
    case _                                          => ProfileWizardInline
  }

}
