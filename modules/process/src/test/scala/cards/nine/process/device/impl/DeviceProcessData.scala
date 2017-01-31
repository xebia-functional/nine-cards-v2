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

import cards.nine.commons._
import cards.nine.commons.test.data.{ApplicationTestData, DeviceTestData}
import cards.nine.models._
import cards.nine.repository.model.{App => RepositoryApp}
import cards.nine.services.persistence.conversions.AppConversions

trait DeviceProcessData
    extends ApplicationTestData
    with DeviceTestData
    with AppConversions
    with NineCardsIntentConversions {

  val iterableCursorContact = new IterableCursor[Contact] {
    override def count(): Int = seqContact.length

    override def moveToPosition(pos: Int): Contact = seqContact(pos)

    override def close(): Unit = ()
  }

  val iterableContact = new IterableContacts(iterableCursorContact)

  val mockIterableCursor = new IterableCursor[RepositoryApp] {
    override def count(): Int = 0

    override def moveToPosition(pos: Int): RepositoryApp = javaNull

    override def close(): Unit = ()
  }

  val iterableCursorApps = new IterableAppCursor(mockIterableCursor, toApp) {
    override def count(): Int = seqApplication.length

    override def moveToPosition(pos: Int): ApplicationData = seqApplicationData(pos)

    override def close(): Unit = ()
  }

}
