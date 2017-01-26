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

import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.NotCategorizedPackage
import cards.nine.models.types.NineCardsCategory
import macroid.ActivityContextWrapper

class RecommendationsJobs(
    category: NineCardsCategory,
    packages: Seq[String],
    actions: RecommendationsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
    extends Jobs
    with AppNineCardsIntentConversions {

  def initialize(): TaskService[Unit] =
    for {
      _ <- actions.initialize()
      _ <- loadRecommendations()
    } yield ()

  def installNow(app: NotCategorizedPackage): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.addRecommendationByFab(app.packageName)
      _ <- di.launcherExecutorProcess.launchGooglePlay(app.packageName)
      _ <- actions.recommendationAdded(app)
    } yield ()

  def loadRecommendations(): TaskService[Unit] = {
    for {
      _ <- actions.showLoading()
      recommendations <- if (category.isAppCategory) {
        di.recommendationsProcess.getRecommendedAppsByCategory(category, packages)
      } else {
        di.recommendationsProcess.getRecommendedAppsByPackages(packages, packages)
      }
      _ <- actions.loadRecommendations(recommendations)
    } yield ()
  }

  def showErrorLoadingRecommendation(): TaskService[Unit] =
    actions.showErrorLoadingRecommendationInScreen()

  def showError(): TaskService[Unit] = actions.showContactUsError()

  def close(): TaskService[Unit] = actions.close()

}
