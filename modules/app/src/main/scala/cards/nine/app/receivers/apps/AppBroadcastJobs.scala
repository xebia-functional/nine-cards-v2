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

package cards.nine.app.receivers.apps

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.action_filters.{
  AppInstalledActionFilter,
  AppUninstalledActionFilter,
  AppUpdatedActionFilter
}
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Application.ApplicationDataOps
import cards.nine.models.{ApplicationData, Collection}
import cats.implicits._
import macroid.ContextWrapper
import monix.eval.Task

class AppBroadcastJobs(implicit contextWrapper: ContextWrapper) extends Jobs with Conversions {

  def addApp(packageName: String): TaskService[Unit] = {

    def insertAppInCollectionIfExist(maybeCollection: Option[Collection], app: ApplicationData) =
      maybeCollection match {
        case Some(collection) =>
          di.collectionProcess.addCards(collection.id, Seq(app.toCardData))
        case _ => TaskService(Task(Either.right((): Unit)))
      }

    for {
      app        <- di.deviceProcess.saveApp(packageName)
      collection <- di.collectionProcess.getCollectionByCategory(app.category)
      _          <- insertAppInCollectionIfExist(collection, app)
      _          <- di.collectionProcess.updateNoInstalledCardsInCollections(packageName)
      _          <- sendBroadCastTask(BroadAction(AppInstalledActionFilter.action))
    } yield (): Unit
  }

  def deleteApp(packageName: String): TaskService[Unit] =
    for {
      _ <- di.deviceProcess.deleteApp(packageName)
      _ <- di.collectionProcess.deleteAllCardsByPackageName(packageName)
      _ <- sendBroadCastTask(BroadAction(AppUninstalledActionFilter.action))
    } yield (): Unit

  def updateApp(packageName: String): TaskService[Unit] =
    for {
      _ <- di.deviceProcess.updateApp(packageName)
      _ <- sendBroadCastTask(BroadAction(AppUpdatedActionFilter.action))
    } yield (): Unit

}
