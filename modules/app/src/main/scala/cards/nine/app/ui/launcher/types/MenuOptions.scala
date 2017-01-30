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

package cards.nine.app.ui.launcher.types

sealed trait AppsMenuOption {
  val name: String
}

case object AppsAlphabetical extends AppsMenuOption {
  override val name: String = "AppsAlphabetical"
}

case object AppsByCategories extends AppsMenuOption {
  override val name: String = "AppsByCategories"
}

case object AppsByLastInstall extends AppsMenuOption {
  override val name: String = "AppsByLastInstall"
}

object AppsMenuOption {
  val list = Seq(AppsAlphabetical, AppsByCategories, AppsByLastInstall)

  def apply(o: AppsMenuOption): Int = list.indexOf(o)

  def apply(status: String): Option[AppsMenuOption] =
    list find (_.name == status)
}

sealed trait ContactsMenuOption {
  val name: String
}

case object ContactsAlphabetical extends ContactsMenuOption {
  override val name: String = "ContactsAlphabetical"
}

case object ContactsFavorites extends ContactsMenuOption {
  override val name: String = "ContactsFavorites"
}

case object ContactsByLastCall extends ContactsMenuOption {
  override val name: String = "ContactsByLastCall"
}

object ContactsMenuOption {
  val list = Seq(ContactsAlphabetical, ContactsFavorites, ContactsByLastCall)

  def apply(o: ContactsMenuOption): Int = list.indexOf(o)

  def apply(status: String): Option[ContactsMenuOption] =
    list find (_.name == status)

}
