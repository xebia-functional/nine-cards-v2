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

package cards.nine.process.sharedcollections

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models._
import cards.nine.models.types.{NineCardsCategory, TypeSharedCollection}

trait SharedCollectionsProcess {

  /**
   * Get a shared collection
   *
   * @param sharedCollectionId the shared collection identifier
   * @return the SharedCollection
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if there was an error fetching the collection or it doesn't exists
   */
  def getSharedCollection(sharedCollectionId: String)(
      implicit context: ContextSupport): TaskService[SharedCollection]

  /**
   * Get shared collections based on a category
   *
   * @param category a valid category identification
   * @param typeShareCollection type of shared collection
   * @param offset offset of query
   * @param limit limit of query
   * @return the Seq[cards.nine.models.SharedCollection]
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if there was an error fetching the collections
   */
  def getSharedCollectionsByCategory(
      category: NineCardsCategory,
      typeShareCollection: TypeSharedCollection,
      offset: Int = 0,
      limit: Int = 50)(implicit context: ContextSupport): TaskService[Seq[SharedCollection]]

  /**
   * Get published collections
   *
   * @return the Seq[cards.nine.models.SharedCollection]
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if there was an error fetching the published collections
   */
  def getPublishedCollections()(
      implicit context: ContextSupport): TaskService[Seq[SharedCollection]]

  /**
   * Persist a SharedCollection
   *
   * @param name The name of the collection
   * @param author The original author of the collection
   * @param packages The list of packages in the collection
   * @param category the NineCardsCategory of the SharedCollection
   * @param icon The collection's icon
   * @param community A flag for whether this is a community collection
   * @return shared collection identifier
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if the service cannot create the collection for some reason
   */
  def createSharedCollection(
      name: String,
      author: String,
      packages: Seq[String],
      category: NineCardsCategory,
      icon: String,
      community: Boolean)(implicit context: ContextSupport): TaskService[String]

  /**
   * Updates a SharedCollection
   *
   * @param sharedCollectionId the SharedCollection id
   * @param name the name of the SharedCollection
   * @param packages the packages of the SharedCollection
   * @return shared collection identifier
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if the service cannot create the collection for some reason
   */
  def updateSharedCollection(sharedCollectionId: String, name: String, packages: Seq[String])(
      implicit context: ContextSupport): TaskService[String]

  /**
   * Gets all the subscriptions of the current user
   *
   * @return the Seq[cards.nine.models.Subscription]
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if the service cannot get the subscriptions or the collections
   */
  def getSubscriptions()(implicit context: ContextSupport): TaskService[Seq[Subscription]]

  /**
   * Subscribes to a public collection
   *
   * @param originalSharedCollectionId the public id of the collection to subscribe on
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if the service cannot subscribe to the collection
   */
  def subscribe(originalSharedCollectionId: String)(
      implicit context: ContextSupport): TaskService[Unit]

  /**
   * Unsubscribes from a public collection
   *
   * @param originalSharedCollectionId the public id of the collection to unsubscribe from
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if the service cannot unsubscribe from the collection
   */
  def unsubscribe(originalSharedCollectionId: String)(
      implicit context: ContextSupport): TaskService[Unit]

  /**
   * Updates the number of view inSharedCollection
   *
   * @param sharedCollectionId the SharedCollection id
   * @return shared collection identifier
   * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
   * @throws SharedCollectionsException if the service cannot updated the collection
   */
  def updateViewSharedCollection(sharedCollectionId: String)(
      implicit context: ContextSupport): TaskService[Unit]

}
