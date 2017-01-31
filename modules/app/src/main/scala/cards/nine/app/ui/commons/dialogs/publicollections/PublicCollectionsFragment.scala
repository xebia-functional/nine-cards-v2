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

package cards.nine.app.ui.commons.dialogs.publicollections

import android.app.Dialog
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.AppLog
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.jobs.LauncherJobs
import cards.nine.commons.services.TaskService._
import cards.nine.models.SharedCollection
import cards.nine.models.types.theme.CardLayoutBackgroundColor
import cards.nine.models.types.{
  Communication,
  NineCardsCategory,
  TopSharedCollection,
  TypeSharedCollection
}
import cards.nine.process.sharedcollections.SharedCollectionsConfigurationException
import com.fortysevendeg.ninecardslauncher.R
import monix.execution.cancelables.SerialCancelable
import PublicCollectionsFragment._
import cards.nine.app.ui.commons.dialogs.BaseActionFragment

class PublicCollectionsFragment(implicit launcherJobs: LauncherJobs)
    extends BaseActionFragment
    with PublicCollectionsUiActions
    with PublicCollectionsDOM
    with PublicCollectionsListener
    with AppNineCardsIntentConversions { self =>

  lazy val collectionJobs = new PublicCollectionsJobs(self)

  lazy val packages =
    getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int =
    theme.get(CardLayoutBackgroundColor)

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    statuses = statuses.reset
    collectionJobs.initialize().resolveServiceOr(onError)
  }

  override def loadPublicCollectionsByTypeSharedCollection(
      typeSharedCollection: TypeSharedCollection): Unit =
    serialCancelableTaskRef := collectionJobs
      .loadPublicCollectionsByTypeSharedCollection(typeSharedCollection)
      .resolveAutoCancelableAsyncServiceOr(onError)

  override def loadPublicCollectionsByCategory(category: NineCardsCategory): Unit =
    serialCancelableTaskRef := collectionJobs
      .loadPublicCollectionsByCategory(category)
      .resolveAutoCancelableAsyncServiceOr(onError)

  override def loadPublicCollections(): Unit =
    serialCancelableTaskRef := collectionJobs
      .loadPublicCollections()
      .resolveAutoCancelableAsyncServiceOr(onError)

  override def onAddCollection(sharedCollection: SharedCollection): Unit =
    (for {
      collection <- collectionJobs.saveSharedCollection(sharedCollection)
      _          <- launcherJobs.addCollection(collection)
    } yield ()).resolveServiceOr(_ => showErrorSavingCollectionInScreen())

  override def onShareCollection(sharedCollection: SharedCollection): Unit =
    collectionJobs.shareCollection(sharedCollection).resolveServiceOr(_ => showContactUsError())

  private[this] def onError(e: Throwable) = e match {
    case e: SharedCollectionsConfigurationException =>
      AppLog.invalidConfigurationV2
      showErrorLoadingCollectionInScreen()
    case _ => showErrorLoadingCollectionInScreen()
  }
}

object PublicCollectionsFragment {

  var statuses = PublicCollectionStatuses()

  val serialCancelableTaskRef = SerialCancelable()
}

case class PublicCollectionStatuses(
    category: NineCardsCategory = Communication,
    typeSharedCollection: TypeSharedCollection = TopSharedCollection) {

  def reset: PublicCollectionStatuses =
    copy(category = Communication, typeSharedCollection = TopSharedCollection)

}
