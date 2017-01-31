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

package cards.nine.app.ui.commons.dialogs.createoreditcollection

import cards.nine.app.ui.commons.{JobException, Jobs}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.{Collection, CollectionData}
import cards.nine.models.types.FreeCollectionType
import macroid.ActivityContextWrapper

class CreateOrEditCollectionJobs(actions: CreateOrEditCollectionUiActions)(
    implicit contextWrapper: ActivityContextWrapper)
    extends Jobs {

  def initialize(maybeCollectionId: Option[String]): TaskService[Unit] = {

    def editCollection(collectionId: Int) =
      for {
        collection <- di.collectionProcess
          .getCollectionById(collectionId)
          .resolveOption(s"Can't find the collection with id $collectionId")
        _ <- di.trackEventProcess.editCollection(collection.name)
        _ <- actions.initializeEditCollection(collection)
      } yield ()

    def createCollection() =
      for {
        _ <- di.trackEventProcess.createNewCollection()
        _ <- actions.initializeNewCollection()
      } yield ()

    for {
      theme <- getThemeTask
      _     <- actions.initialize(theme)
      _ <- maybeCollectionId match {
        case Some(collectionId) => editCollection(collectionId.toInt)
        case None               => createCollection()
      }
    } yield ()
  }

  def editCollection(
      collection: Collection,
      name: String,
      icon: String,
      themedColorIndex: Int): TaskService[Collection] = {
    val request = CollectionData(
      position = collection.position,
      name = name,
      collectionType = collection.collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = collection.appsCategory,
      cards = collection.cards map (_.toData),
      moment = collection.moment map (_.toData))
    for {
      collection <- di.collectionProcess.editCollection(collection.id, request)
      _          <- actions.close()
    } yield collection
  }

  def saveCollection(name: String, icon: String, themedColorIndex: Int): TaskService[Collection] = {
    val request = CollectionData(
      name = name,
      collectionType = FreeCollectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = None,
      cards = Seq.empty,
      moment = None)
    for {
      _          <- di.trackEventProcess.createNewCollection()
      collection <- di.collectionProcess.addCollection(request)
      _          <- actions.close()
    } yield collection
  }

  def updateIcon(maybeIcon: Option[String]): TaskService[Unit] =
    readOption(maybeIcon, "Empty index color")(actions.updateIcon)

  def updateColor(maybeIndexColor: Option[Int]): TaskService[Unit] =
    readOption(maybeIndexColor, "Empty index color")(actions.updateColor)

  def changeColor(maybeColor: Option[Int]): TaskService[Unit] =
    readOption(maybeColor, "Empty color")(actions.showColorDialog)

  def changeIcon(maybeIcon: Option[String]): TaskService[Unit] =
    readOption(maybeIcon, "Empty Icon")(actions.showIconDialog)

  private[this] def readOption[T](maybe: Option[T], errorMessage: String)(
      f: (T) => TaskService[Unit]): TaskService[Unit] =
    maybe match {
      case Some(value) => f(value)
      case None        => TaskService.left(JobException(errorMessage))
    }

}
