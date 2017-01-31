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

package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{
  ImplicitsTrackEventException,
  TrackEventException,
  TrackEventProcess
}

trait CollectionDetailTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def useNavigationBar() = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = NavigationBarAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def reorderApplication(newPosition: Int) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = ReorderApplicationAction,
      label = Option(newPosition.toString),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def moveApplications(collectionName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = MoveApplicationsAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeApplications(packageNames: Seq[String]) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = RemoveApplicationsAction,
      label = Option(packageNames.mkString(",")),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def closeCollectionByGesture() = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = CloseCollectionByGestureAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addShortcutByFab(shortcutName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddShortcutByFabAction,
      label = Option(shortcutName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addShortcutFromReceiver(shortcutName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddShortcutFromReceiverAction,
      label = Option(shortcutName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addRecommendationByFab(packageName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddRecommendationByFabAction,
      label = Option(packageName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addContactByFab() = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddContactByFabAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppsByFab(packageNames: Seq[String]) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddAppsByFabAction,
      label = Option(packageNames.mkString(",")),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeAppsByFab(packageNames: Seq[String]) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = RemoveAppsByFabAction,
      label = Option(packageNames.mkString(",")),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addCardByMenu() = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddCardByMenuAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def publishCollectionByMenu(collectionName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = PublishCollectionByMenuAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def shareCollectionAfterPublishing(sharedCollectionId: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = ShareCollectionAfterPublishingAction,
      label = Option(sharedCollectionId),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def shareCollectionByMenu(sharedCollectionId: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = ShareCollectionByMenuAction,
      label = Option(sharedCollectionId),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openAppFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = OpenCardAction,
      label = Option(packageName),
      value = Option(OpenAppFromCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppToCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = AddedToCollectionAction,
      label = Option(packageName),
      value = Option(AddedToCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = RemovedFromCollectionAction,
      label = Option(packageName),
      value = Option(RemovedFromCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
