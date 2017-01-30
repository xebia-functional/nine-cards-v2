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

package cards.nine.services.shortcuts.impl

import android.content.Intent
import cards.nine.models.Shortcut

trait ShortcutsServicesImplData {

  val name        = "Name"
  val packageName = "com.fortysevendeg.ninecardslauncher.test.sampleapp"

  val intent = new Intent(Intent.ACTION_CREATE_SHORTCUT)

  val sampleShortcut1 = Shortcut(title = "B - Sample Name 1", icon = None, intent = intent)

  val sampleShortcut2 = Shortcut(title = "A - Sample Name 2", icon = None, intent = intent)

  val shotcutsList = Seq(sampleShortcut1, sampleShortcut2)

}
