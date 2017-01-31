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

package cards.nine.app.ui.commons.dialogs.privatecollections

import cards.nine.app.ui.commons.dialogs.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.commons.PaddingItemDecoration
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.CollectionData
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ViewTweaks._

trait PrivateCollectionsUiActions extends Styles {

  self: BaseActionFragment with PrivateCollectionsDOM with PrivateCollectionsListener =>

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.myCollections) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)).toService()

  def addPrivateCollections(privateCollections: Seq[CollectionData]): TaskService[Unit] = {
    val adapter = PrivateCollectionsAdapter(privateCollections, saveCollection)
    ((recycler <~
      vVisible <~
      rvAddItemDecoration(new PaddingItemDecoration) <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)).toService()
  }

  def showLoading(): TaskService[Unit] =
    ((loading <~ vVisible) ~ (recycler <~ vGone)).toService()

  def showEmptyMessageInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.emptyPrivateCollections, error = false, loadPrivateCollections())
      .toService()

  def showErrorLoadingCollectionInScreen(): TaskService[Unit] =
    showMessageInScreen(
      R.string.errorLoadingPrivateCollections,
      error = true,
      loadPrivateCollections()).toService()

  def showErrorSavingCollectionInScreen(): TaskService[Unit] =
    showMessageInScreen(
      R.string.errorSavingPrivateCollections,
      error = true,
      loadPrivateCollections()).toService()

  def close(): TaskService[Unit] = unreveal().toService()

}
