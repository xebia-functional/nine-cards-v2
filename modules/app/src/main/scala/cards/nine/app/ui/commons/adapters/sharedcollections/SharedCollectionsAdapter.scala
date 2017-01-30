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

package cards.nine.app.ui.commons.adapters.sharedcollections

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import cards.nine.app.ui.commons.UiContext
import cards.nine.models.SharedCollection
import cards.nine.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.TR
import com.fortysevendeg.ninecardslauncher.TypedResource._
import macroid.ActivityContextWrapper

case class SharedCollectionsAdapter(
    sharedCollections: Seq[SharedCollection],
    onAddCollection: (SharedCollection) => Unit,
    onShareCollection: (SharedCollection) => Unit)(
    implicit activityContext: ActivityContextWrapper,
    uiContext: UiContext[_],
    theme: NineCardsTheme)
    extends RecyclerView.Adapter[ViewHolderSharedCollectionsLayoutAdapter] {

  override def onCreateViewHolder(
      parent: ViewGroup,
      viewType: Int): ViewHolderSharedCollectionsLayoutAdapter = {
    val view = LayoutInflater
      .from(parent.getContext)
      .inflate(TR.layout.public_collections_item, parent, false)
    ViewHolderSharedCollectionsLayoutAdapter(view)
  }

  override def getItemCount: Int = sharedCollections.size

  override def onBindViewHolder(
      viewHolder: ViewHolderSharedCollectionsLayoutAdapter,
      position: Int): Unit = {
    val publicCollection = sharedCollections(position)
    viewHolder
      .bind(
        publicCollection,
        onAddCollection(publicCollection),
        onShareCollection(publicCollection))
      .run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
