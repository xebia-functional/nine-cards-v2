package com.fortysevendeg.ninecardslauncher.process.sharedcollections

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models._

trait SharedCollectionsProcess {

  /**
    * Get shared collections based on a category
    *
    * @param category a valid category identification
    * @param typeShareCollection type of shared collection
    * @param offset offset of query
    * @param limit limit of query
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection]
    * @throws SharedCollectionsExceptions if there was an error fetching the recommended apps
    */
  def getSharedCollectionsByCategory(
    category: NineCardCategory,
    typeShareCollection: TypeSharedCollection,
    offset: Int = 0,
    limit: Int = 50)(implicit context: ContextSupport): TaskService[Seq[SharedCollection]]

  /**
    * Persist a [[com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection]]
    * @param sharedCollection the defined collection to create
    * @return shared collection identifier
    * @throws SharedCollectionsExceptions if the service cannot create the collection for some reason
    */
  def createSharedCollection(
    sharedCollection: CreateSharedCollection
  )(implicit context: ContextSupport): TaskService[String]

  /**
    * Updates a [[com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection]]
    * @param sharedCollection the defined collection to update
    * @return shared collection identifier
    * @throws SharedCollectionsExceptions if the service cannot create the collection for some reason
    */
  def updateSharedCollection(
    sharedCollection: UpdateSharedCollection
  )(implicit context: ContextSupport): TaskService[String]

  /**
    * Gets all the subscriptions of the current user
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.Subscription]
    * @throws SharedCollectionsExceptions if the service cannot get the subscriptions or the collections
    */
  def getSubscriptions()(implicit context: ContextSupport): TaskService[Seq[Subscription]]

  /**
    * Subscribes to a public collection
    * @param originalSharedCollectionId the public id of the collection to subscribe on
    * @throws SharedCollectionsExceptions if the service cannot subscribe to the collection
    */
  def subscribe(originalSharedCollectionId: String)(implicit context: ContextSupport): TaskService[Unit]

  /**
    * Unsubscribes from a public collection
    * @param originalSharedCollectionId the public id of the collection to unsubscribe from
    * @throws SharedCollectionsExceptions if the service cannot unsubscribe from the collection
    */
  def unsubscribe(originalSharedCollectionId: String)(implicit context: ContextSupport): TaskService[Unit]
}
