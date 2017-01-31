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

package cards.nine.process.device.impl

import cards.nine.services.api.ApiServices
import cards.nine.services.apps.AppsServices
import cards.nine.services.calls.CallsServices
import cards.nine.services.contacts.ContactsServices
import cards.nine.services.image.ImageServices
import cards.nine.services.persistence.PersistenceServices
import cards.nine.services.shortcuts.ShortcutsServices
import cards.nine.services.widgets.WidgetsServices

trait DeviceProcessDependencies {

  val appsServices: AppsServices
  val apiServices: ApiServices
  val persistenceServices: PersistenceServices
  val shortcutsServices: ShortcutsServices
  val contactsServices: ContactsServices
  val imageServices: ImageServices
  val widgetsServices: WidgetsServices
  val callsServices: CallsServices
}
