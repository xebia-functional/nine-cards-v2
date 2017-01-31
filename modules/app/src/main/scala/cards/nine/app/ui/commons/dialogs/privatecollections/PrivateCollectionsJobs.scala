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

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Collection, CollectionData}
import cards.nine.models.types.GetByName
import macroid.ActivityContextWrapper

class PrivateCollectionsJobs(actions: PrivateCollectionsUiActions)(
    implicit contextWrapper: ActivityContextWrapper)
    extends Jobs
    with Conversions {

  def initialize(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.openMyCollections()
      _ <- actions.initialize()
      _ <- loadPrivateCollections()
    } yield ()

  def loadPrivateCollections(): TaskService[Unit] =
    for {
      _              <- actions.showLoading()
      collections    <- di.collectionProcess.getCollections
      moments        <- di.momentProcess.getMoments
      apps           <- di.deviceProcess.getSavedApps(GetByName)
      newCollections <- di.collectionProcess.generatePrivateCollections(apps)
      privateCollections = newCollections filterNot { newCollection =>
        newCollection.appsCategory match {
          case Some(category) =>
            (collections flatMap (_.appsCategory)) contains category
          case _ => false
        }
      }
      _ <- if (privateCollections.isEmpty) {
        actions.showEmptyMessageInScreen()
      } else {
        actions.addPrivateCollections(privateCollections)
      }
    } yield ()

  def saveCollection(collection: CollectionData): TaskService[Collection] =
    for {
      _               <- di.trackEventProcess.createNewCollectionFromMyCollection(collection.name)
      collectionAdded <- di.collectionProcess.addCollection(collection)
      _               <- actions.close()
    } yield collectionAdded

}
