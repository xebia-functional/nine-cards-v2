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

package cards.nine.models

import cards.nine.commons.javaNull
import cards.nine.models.types.Misc

class IterableAppCursor[T](cursor: IterableCursor[T], f: T => Application)
    extends IterableApplicationData {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): ApplicationData = f(cursor.moveToPosition(pos)).toData

  override def close(): Unit = cursor.close()

}

class EmptyIterableApps() extends IterableAppCursor(javaNull, javaNull) {
  val emptyApp                                           = ApplicationData("", "", "", Misc, 0, 0, "", installedFromGooglePlay = false)
  override def count(): Int                              = 0
  override def moveToPosition(pos: Int): ApplicationData = emptyApp
  override def close(): Unit                             = {}
}

class IterableContacts(cursor: IterableCursor[Contact]) extends IterableCursor[Contact] {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Contact = cursor.moveToPosition(pos)

  override def close(): Unit = cursor.close()

}
