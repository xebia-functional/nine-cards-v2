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

package cards.nine.process.collection.impl

import cards.nine.models.CollectionProcessConfig
import cards.nine.services.api.ApiServices
import cards.nine.services.apps.AppsServices
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.contacts.ContactsServices
import cards.nine.services.persistence.PersistenceServices
import cards.nine.services.widgets.WidgetsServices

trait CollectionProcessDependencies {

  val collectionProcessConfig: CollectionProcessConfig
  val persistenceServices: PersistenceServices
  val contactsServices: ContactsServices
  val appsServices: AppsServices
  val apiServices: ApiServices
  val awarenessServices: AwarenessServices
  val widgetsServices: WidgetsServices

}
