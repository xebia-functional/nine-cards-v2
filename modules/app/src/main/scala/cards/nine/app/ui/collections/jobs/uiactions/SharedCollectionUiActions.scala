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

package cards.nine.app.ui.collections.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, UiContext, UiException}
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Collection
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

class SharedCollectionUiActions(
    val dom: GroupCollectionsDOM,
    listener: GroupCollectionsUiListener)(
    implicit activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
    extends ImplicitsUiExceptions {

  def reloadSharedCollectionId(sharedCollectionId: Option[String]): TaskService[Unit] =
    Ui {
      for {
        adapter         <- dom.getAdapter
        currentPosition <- adapter.getCurrentFragmentPosition
        _ = adapter.updateShareCollectionIdFromCollection(currentPosition, sharedCollectionId)
      } yield dom.invalidateOptionMenu
    }.toService()

  def showPublishCollectionWizardDialog(collection: Collection): TaskService[Unit] =
    Ui(listener.showPublicCollectionDialog(collection)).toService()

  def showMessagePublishContactsCollectionError: TaskService[Unit] =
    showError(R.string.publishCollectionError).toService()

  def showMessageNotPublishedCollectionError: TaskService[Unit] =
    showError(R.string.notPublishedCollectionError).toService()

  def getCurrentCollection: TaskService[Option[Collection]] = TaskService {
    CatchAll[UiException](dom.getCurrentCollection)
  }

  private[this] def showError(error: Int = R.string.contactUsError): Ui[Any] =
    dom.root <~ vSnackbarShort(error)

}
