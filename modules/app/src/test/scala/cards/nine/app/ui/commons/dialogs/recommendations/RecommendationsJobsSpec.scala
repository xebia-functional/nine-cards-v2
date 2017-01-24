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

import cards.nine.app.di.Injector
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.ApiTestData
import cards.nine.models.types.{AllAppsCategory, Photography}
import cards.nine.process.intents.{
  LauncherExecutorProcess,
  LauncherExecutorProcessPermissionException
}
import cards.nine.process.recommendations.{RecommendationsProcess, RecommendedAppsException}
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait RecommendationsJobsSpecification extends TaskServiceSpecification with Mockito {

  trait RecommendationsJobsScope extends Scope with ApiTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector: Injector = mock[Injector]

    val mockRecommendationsUiActions = mock[RecommendationsUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockRecommendationsProcess = mock[RecommendationsProcess]

    mockInjector.recommendationsProcess returns mockRecommendationsProcess

    val recommendationsJobs =
      new RecommendationsJobs(AllAppsCategory, Seq.empty, mockRecommendationsUiActions)(
        contextWrapper) {

        override lazy val di: Injector = mockInjector

      }
  }

}

class RecommendationsJobsSpec extends RecommendationsJobsSpecification {

  "initialize" should {
    "returns a valid response when the service returns a right response" in new RecommendationsJobsScope {

      mockRecommendationsUiActions.initialize() returns serviceRight(Unit)
      mockRecommendationsUiActions.showLoading() returns serviceRight(Unit)
      mockRecommendationsProcess.getRecommendedAppsByPackages(any, any)(any) returns serviceRight(
        seqNotCategorizedPackage)
      mockRecommendationsUiActions.loadRecommendations(any) returns serviceRight(Unit)

      recommendationsJobs.initialize().mustRightUnit

      there was one(mockRecommendationsUiActions).initialize()
      there was one(mockRecommendationsUiActions).showLoading()
      there was one(mockRecommendationsProcess)
        .getRecommendedAppsByPackages(===(Seq.empty), ===(Seq.empty))(any)
      there was one(mockRecommendationsUiActions).loadRecommendations(seqNotCategorizedPackage)
    }
  }

  "installNow" should {
    "returns a valid response when the service returns a right response" in new RecommendationsJobsScope {

      mockTrackEventProcess.addRecommendationByFab(any) returns serviceRight(Unit)
      mockLauncherExecutorProcess.launchGooglePlay(any)(any) returns serviceRight(Unit)
      mockRecommendationsUiActions.recommendationAdded(any) returns serviceRight(Unit)

      recommendationsJobs.installNow(notCategorizedPackage).mustRightUnit

      there was one(mockTrackEventProcess).addRecommendationByFab(
        notCategorizedPackage.packageName)
      there was one(mockLauncherExecutorProcess).launchGooglePlay(
        ===(notCategorizedPackage.packageName))(any)
      there was one(mockRecommendationsUiActions).recommendationAdded(notCategorizedPackage)
    }

    "returns a LauncherExecutorProcessPermissionException when the service returns an exception" in new RecommendationsJobsScope {

      mockTrackEventProcess.addRecommendationByFab(any) returns serviceRight(Unit)
      mockLauncherExecutorProcess.launchGooglePlay(any)(any) returns serviceLeft(
        LauncherExecutorProcessPermissionException(""))

      recommendationsJobs
        .installNow(notCategorizedPackage)
        .mustLeft[LauncherExecutorProcessPermissionException]

      there was one(mockTrackEventProcess).addRecommendationByFab(
        notCategorizedPackage.packageName)
      there was one(mockLauncherExecutorProcess).launchGooglePlay(
        ===(notCategorizedPackage.packageName))(any)
      there was no(mockRecommendationsUiActions).recommendationAdded(notCategorizedPackage)
    }
  }

  "loadRecommendations" should {
    "returns a valid response when the service returns a right response" in new RecommendationsJobsScope {

      mockRecommendationsUiActions.showLoading() returns serviceRight(Unit)
      mockRecommendationsProcess.getRecommendedAppsByPackages(any, any)(any) returns serviceRight(
        seqNotCategorizedPackage)
      mockRecommendationsUiActions.loadRecommendations(any) returns serviceRight(Unit)

      recommendationsJobs.loadRecommendations().mustRightUnit

      there was one(mockRecommendationsUiActions).showLoading()
      there was one(mockRecommendationsProcess)
        .getRecommendedAppsByPackages(===(Seq.empty), ===(Seq.empty))(any)
      there was one(mockRecommendationsUiActions).loadRecommendations(seqNotCategorizedPackage)
    }

    "returns a RecommendedAppsException when the service returns an exception" in new RecommendationsJobsScope {

      mockRecommendationsUiActions.showLoading() returns serviceRight(Unit)
      mockRecommendationsProcess.getRecommendedAppsByPackages(any, any)(any) returns serviceLeft(
        RecommendedAppsException(""))

      recommendationsJobs.loadRecommendations().mustLeft[RecommendedAppsException]

      there was one(mockRecommendationsUiActions).showLoading()
      there was one(mockRecommendationsProcess)
        .getRecommendedAppsByPackages(===(Seq.empty), ===(Seq.empty))(any)
    }

    "returns a valid response when the service returns a right response" in new RecommendationsJobsScope {

      override val recommendationsJobs =
        new RecommendationsJobs(Photography, Seq.empty, mockRecommendationsUiActions)(
          contextWrapper) {

          override lazy val di: Injector = mockInjector

        }

      mockRecommendationsUiActions.showLoading() returns serviceRight(Unit)
      mockRecommendationsProcess.getRecommendedAppsByCategory(any, any)(any) returns serviceRight(
        seqNotCategorizedPackage)
      mockRecommendationsUiActions.loadRecommendations(any) returns serviceRight(Unit)

      recommendationsJobs.loadRecommendations().mustRightUnit

      there was one(mockRecommendationsUiActions).showLoading()
      there was one(mockRecommendationsProcess)
        .getRecommendedAppsByCategory(===(Photography), ===(Seq.empty))(any)
      there was one(mockRecommendationsUiActions).loadRecommendations(seqNotCategorizedPackage)
    }
  }

  "showErrorLoadingRecommendation" should {
    "call to showErrorLoadingRecommendationInScreen " in new RecommendationsJobsScope {

      mockRecommendationsUiActions.showErrorLoadingRecommendationInScreen() returns serviceRight(
        Unit)
      recommendationsJobs.showErrorLoadingRecommendation().mustRightUnit
      there was one(mockRecommendationsUiActions).showErrorLoadingRecommendationInScreen()
    }
  }
  "showError" should {
    "call to showContactUsError" in new RecommendationsJobsScope {

      mockRecommendationsUiActions.showContactUsError() returns serviceRight(Unit)
      recommendationsJobs.showError().mustRightUnit
      there was one(mockRecommendationsUiActions).showContactUsError()
    }
  }
  "close" should {
    "call to close" in new RecommendationsJobsScope {

      mockRecommendationsUiActions.close() returns serviceRight(Unit)
      recommendationsJobs.close().mustRightUnit
      there was one(mockRecommendationsUiActions).close()
    }
  }
}
