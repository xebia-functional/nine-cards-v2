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

package cards.nine.models.types

sealed trait CollectionType {
  val name: String
}

case object AppsCollectionType extends CollectionType {
  override val name: String = "APPS"
}

case object ContactsCollectionType extends CollectionType {
  override val name: String = "CONTACTS"
}

case object MomentCollectionType extends CollectionType {
  override val name: String = "MOMENT"
}

case object FreeCollectionType extends CollectionType {
  override val name: String = "FREE"
}

object CollectionType {

  val collectionTypes =
    Seq(AppsCollectionType, ContactsCollectionType, MomentCollectionType, FreeCollectionType)

  def apply(name: String): CollectionType =
    collectionTypes find (_.name == name) getOrElse FreeCollectionType

}
