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

package cards.nine.repository.provider

object NineCardsUri {

  val authorityPart = "com.fortysevendeg.ninecardslauncher"

  val contentPrefix = "content://"

  val baseUriString = s"$contentPrefix$authorityPart"

  val appUriString = s"$baseUriString/${AppEntity.table}"

  val cardUriString = s"$baseUriString/${CardEntity.table}"

  val collectionUriString = s"$baseUriString/${CollectionEntity.table}"

  val dockAppUriString = s"$baseUriString/${DockAppEntity.table}"

  val momentUriString = s"$baseUriString/${MomentEntity.table}"

  val userUriString = s"$baseUriString/${UserEntity.table}"

  val widgetUriString = s"$baseUriString/${WidgetEntity.table}"

}
