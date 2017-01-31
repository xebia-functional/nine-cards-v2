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

package cards.nine.app.ui.commons.dialogs.recommendations

import cards.nine.app.ui.commons.dialogs.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.NotCategorizedPackage
import cards.nine.models.types.theme.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.UIActionsExtras._
import macroid.extras.ViewTweaks._

trait RecommendationsUiActions extends Styles {

  self: BaseActionFragment with RecommendationsDOM with RecommendationsUiListener =>

  def loadBackgroundColor = theme.get(CardLayoutBackgroundColor)

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.recommendations) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)).toService()

  def showLoading(): TaskService[Unit] =
    ((loading <~ vVisible) ~ (recycler <~ vGone)).toService()

  def showErrorLoadingRecommendationInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingRecommendations, error = true, loadRecommendations())
      .toService()

  def loadRecommendations(recommendations: Seq[NotCategorizedPackage]): TaskService[Unit] = {
    val adapter = RecommendationsAdapter(recommendations, installApp)
    ((recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)).toService()
  }

  def recommendationAdded(app: NotCategorizedPackage): TaskService[Unit] =
    TaskService.right(addApp(app))

  def close(): TaskService[Unit] = unreveal().toService()

  def showContactUsError(): TaskService[Unit] =
    uiShortToast(R.string.contactUsError).toService()

}
