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

sealed trait GetAppOrder {
  val ascending: Boolean
  val name: String
}

case class GetByName(ascending: Boolean) extends GetAppOrder {
  override val name: String = "GET BY NAME"
}

object GetByName extends GetByName(true)

case class GetByInstallDate(ascending: Boolean) extends GetAppOrder {
  override val name: String = "GET BY INSTALL DATE"
}

object GetByInstallDate extends GetByInstallDate(false)

case class GetByCategory(ascending: Boolean) extends GetAppOrder {
  override val name: String = "GET BY CATEGORY"
}

object GetByCategory extends GetByCategory(true)
