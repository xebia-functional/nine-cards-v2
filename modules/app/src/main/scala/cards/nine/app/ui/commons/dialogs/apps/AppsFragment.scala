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

package cards.nine.app.ui.commons.dialogs.apps

import android.app.Dialog
import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.dialogs.apps.AppsFragment._
import cards.nine.app.ui.collections.jobs.{GroupCollectionsJobs, SingleCollectionJobs}
import cards.nine.app.ui.commons.UiExtensions
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{ApplicationData, NotCategorizedPackage}
import com.fortysevendeg.ninecardslauncher.R

class AppsFragment(
    implicit groupCollectionsJobs: GroupCollectionsJobs,
    singleCollectionJobs: Option[SingleCollectionJobs])
    extends BaseActionFragment
    with AppsUiActions
    with AppsDOM
    with AppsUiListener
    with Conversions
    with UiExtensions { self =>

  lazy val appsJobs = AppsJobs(actions = self)

  lazy val packages =
    getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String]).toSet

  override def useFab: Boolean = true

  override def getLayoutId: Int = R.layout.list_action_apps_fragment

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    appStatuses = appStatuses.copy(initialPackages = packages, selectedPackages = packages)
    appsJobs.initialize(appStatuses.selectedPackages).resolveAsync()
  }

  override def onDestroy(): Unit = {
    appsJobs.destroy().resolveAsync()
    super.onDestroy()
  }

  override def loadApps(): Unit = {
    appStatuses = appStatuses.copy(contentView = AppsView)
    appsJobs.loadApps().resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps())
  }

  override def loadFilteredApps(keyword: String): Unit =
    appsJobs.loadAppsByKeyword(keyword).resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps())

  override def loadSearch(query: String): Unit = {
    appStatuses = appStatuses.copy(contentView = GooglePlayView)
    appsJobs.loadSearch(query).resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps())
  }

  override def launchGooglePlay(app: NotCategorizedPackage): Unit =
    (for {
      _     <- appsJobs.launchGooglePlay(app.packageName)
      cards <- groupCollectionsJobs.addCards(Seq(toCardData(app)))
      _ <- singleCollectionJobs match {
        case Some(job) => job.addCards(cards)
        case _         => TaskService.empty
      }
      _ <- appsJobs.close()
    } yield ()).resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps())

  override def updateSelectedApps(app: ApplicationData): Unit = {
    appStatuses = appStatuses.update(app.packageName)
    appsJobs
      .updateSelectedApps(appStatuses.selectedPackages)
      .resolveAsyncServiceOr(_ => appsJobs.showError())
  }

  override def updateCollectionApps(): Unit = {

    def updateCards(): TaskService[Unit] =
      for {
        result <- appsJobs.getAddedAndRemovedApps
        (cardsToAdd, cardsToRemove) = result
        cardsRemoved <- groupCollectionsJobs.removeCardsByPackagesName(
          cardsToRemove flatMap (_.packageName))
        _ <- singleCollectionJobs match {
          case Some(job) => job.removeCards(cardsRemoved)
          case _         => TaskService.empty
        }
        cardsAdded <- groupCollectionsJobs.addCards(cardsToAdd)
        _ <- singleCollectionJobs match {
          case Some(job) => job.addCards(cardsAdded)
          case _         => TaskService.empty
        }
      } yield ()

    (for {
      _ <- if (appStatuses.initialPackages == appStatuses.selectedPackages)
        TaskService.empty
      else updateCards()
      _ <- appsJobs.close()
    } yield ()).resolveAsyncServiceOr(_ => appsJobs.showError())

  }

}

object AppsFragment {

  var appStatuses = AppsStatuses()

  val categoryKey = "category"
}

case class AppsStatuses(
    initialPackages: Set[String] = Set.empty,
    selectedPackages: Set[String] = Set.empty,
    contentView: ContentView = AppsView) {

  def update(packageName: String): AppsStatuses =
    if (selectedPackages.contains(packageName))
      copy(selectedPackages = selectedPackages - packageName)
    else copy(selectedPackages = selectedPackages + packageName)

}

sealed trait ContentView

case object AppsView extends ContentView

case object GooglePlayView extends ContentView
