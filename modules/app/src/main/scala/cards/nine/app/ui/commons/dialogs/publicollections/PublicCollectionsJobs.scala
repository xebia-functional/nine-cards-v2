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

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.collections.tasks.CollectionJobs
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.dialogs.publicollections.PublicCollectionsFragment._
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.{Collection, SharedCollection}
import cards.nine.models.types.{NineCardsCategory, TypeSharedCollection}
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

import cards.nine.commons.NineCardExtensions._
import cats.implicits._

class PublicCollectionsJobs(actions: PublicCollectionsUiActions)(
    implicit contextWrapper: ActivityContextWrapper)
    extends Jobs
    with Conversions
    with CollectionJobs {

  def initialize(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.openPublicCollections()
      _ <- actions.initialize()
      _ <- loadPublicCollections()
    } yield ()

  def loadPublicCollections(): TaskService[Unit] = {

    def getSharedCollections(
        category: NineCardsCategory,
        typeSharedCollection: TypeSharedCollection): TaskService[Seq[SharedCollection]] =
      di.sharedCollectionsProcess.getSharedCollectionsByCategory(category, typeSharedCollection)

    for {
      _                 <- actions.showLoading()
      sharedCollections <- getSharedCollections(statuses.category, statuses.typeSharedCollection)
      _ <- if (sharedCollections.isEmpty) {
        actions.showEmptyMessageInScreen()
      } else {
        actions.loadPublicCollections(sharedCollections)
      }
    } yield ()
  }

  def loadPublicCollectionsByCategory(category: NineCardsCategory): TaskService[Unit] = {
    statuses = statuses.copy(category = category)
    for {
      _ <- actions.updateCategory(category)
      _ <- loadPublicCollections()
    } yield ()
  }

  def loadPublicCollectionsByTypeSharedCollection(
      typeSharedCollection: TypeSharedCollection): TaskService[Unit] = {
    statuses = statuses.copy(typeSharedCollection = typeSharedCollection)
    for {
      _ <- actions.updateTypeCollection(typeSharedCollection)
      _ <- loadPublicCollections()
    } yield ()
  }

  def saveSharedCollection(sharedCollection: SharedCollection): TaskService[Collection] = {

    def addCollection() =
      for {
        collection <- addSharedCollection(sharedCollection)
        _          <- actions.close()
      } yield collection

    di.sharedCollectionsProcess
      .updateViewSharedCollection(sharedCollection.id)
      .resolveLeftTo(()) *>
      di.trackEventProcess
        .createNewCollectionFromPublicCollection(sharedCollection.name)
        .resolveLeftTo(()) *>
      addCollection()

  }

  def shareCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    di.launcherExecutorProcess.launchShare(
      getString(R.string.shared_collection_url, sharedCollection.id))

  protected def getString(res: Int, formatArgs: scala.AnyRef*): String =
    resGetString(res, formatArgs)

}
