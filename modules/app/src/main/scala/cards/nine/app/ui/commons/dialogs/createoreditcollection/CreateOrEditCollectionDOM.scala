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

package cards.nine.app.ui.commons.dialogs.createoreditcollection

import cards.nine.models.Collection
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait CreateOrEditCollectionDOM { self: TypedFindView =>

  lazy val name = findView(TR.new_collection_name)

  lazy val collectionName = findView(TR.new_collection_name)

  lazy val colorContent = findView(TR.new_collection_select_color_content)

  lazy val colorImage = findView(TR.new_collection_select_color_image)

  lazy val colorText = findView(TR.new_collection_select_color_text)

  lazy val iconContent = findView(TR.new_collection_select_icon_content)

  lazy val iconImage = findView(TR.new_collection_select_icon_image)

  lazy val iconText = findView(TR.new_collection_select_icon_text)

}

trait CreateOrEditCollectionListener {

  def changeColor(maybeColor: Option[Int])

  def changeIcon(maybeIcon: Option[String])

  def saveCollection(
      maybeName: Option[String],
      maybeIcon: Option[String],
      maybeIndex: Option[Int]): Unit

  def editCollection(
      collection: Collection,
      maybeName: Option[String],
      maybeIcon: Option[String],
      maybeIndex: Option[Int]): Unit

}
