/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain
 *  a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.fortysevendeg.ninecardslauncher.ui.collections

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.di.DependencyInjector
import com.fortysevendeg.ninecardslauncher.modules.repository.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.ui.commons.AsyncImageFragmentTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid.FullDsl._
import macroid.{Ui, ActivityContext, AppContext}
import CollectionAdapter._

class CollectionAdapter(
    collection: Collection,
    heightCard: Int,
    listener: CollectionListener)
    (implicit context: ActivityContext, appContext: AppContext, fragment: Fragment, di: DependencyInjector)
    extends RecyclerView.Adapter[ViewHolderCollectionAdapter] {

  override def onCreateViewHolder(parentViewGroup: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val adapter = new CollectionLayoutAdapter(heightCard)
    adapter.content.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = Option(v.getTag) map {
        tag =>
          runUi(listener(collection.cards(tag.toString.toInt)))
      }
    })
    new ViewHolderCollectionAdapter(adapter)
  }

  override def getItemCount: Int = collection.cards.size

  override def onBindViewHolder(viewHolder: ViewHolderCollectionAdapter, position: Int): Unit = {
    val card = collection.cards(position)
    runUi((viewHolder.icon <~ ivUri(fragment, card.imagePath)) ~
      (viewHolder.name <~ tvText(card.term)) ~
      (viewHolder.content <~ vTag(position.toString)))
  }


}

object CollectionAdapter {
  type CollectionListener = Card => Ui[_]
}

