package cards.nine.process.sharedcollections

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.sharedcollections.models._
import cards.nine.models.types.NineCardCategory

trait SharedCollectionsProcess {

  /**
    * Get a shared collection
    *
    * @param sharedCollectionId the shared collection identifier
    * @return the SharedCollection
    * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
    * @throws SharedCollectionsException if there was an error fetching the collection or it doesn't exists
    */
  def getSharedCollection(
    sharedCollectionId: String)(implicit context: ContextSupport): TaskService[SharedCollection]

  /**
    * Get shared collections based on a category
    *
    * @param category a valid category identification
    * @param typeShareCollection type of shared collection
    * @param offset offset of query
    * @param limit limit of query
    * @return the Seq[cards.nine.process.sharedcollections.models.SharedCollection]
    * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
    * @throws SharedCollectionsException if there was an error fetching the collections
    */
  def getSharedCollectionsByCategory(
    category: NineCardCategory,
    typeShareCollection: TypeSharedCollection,
    offset: Int = 0,
    limit: Int = 50)(implicit context: ContextSupport): TaskService[Seq[SharedCollection]]

  /**
    * Get published collections
    *
    * @return the Seq[cards.nine.process.sharedcollections.models.SharedCollection]
    * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
    * @throws SharedCollectionsException if there was an error fetching the published collections
    */
  def getPublishedCollections()(implicit context: ContextSupport): TaskService[Seq[SharedCollection]]

  /**
    * Persist a SharedCollection
    *
    * @param sharedCollection the defined collection to create
    * @return shared collection identifier
    * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
    * @throws SharedCollectionsException if the service cannot create the collection for some reason
    */
  def createSharedCollection(
    sharedCollection: CreateSharedCollection
  )(implicit context: ContextSupport): TaskService[String]

  /**
    * Updates a SharedCollection
    *
    * @param sharedCollection the defined collection to update
    * @return shared collection identifier
    * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
    * @throws SharedCollectionsException if the service cannot create the collection for some reason
    */
  def updateSharedCollection(
    sharedCollection: UpdateSharedCollection
  )(implicit context: ContextSupport): TaskService[String]

  /**
    * Gets all the subscriptions of the current user
    *
    * @return the Seq[cards.nine.process.sharedcollections.models.Subscription]
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
  def subscribe(originalSharedCollectionId: String)(implicit context: ContextSupport): TaskService[Unit]

  /**
    * Unsubscribes from a public collection
    *
    * @param originalSharedCollectionId the public id of the collection to unsubscribe from
    * @throws SharedCollectionsConfigurationException if there was an error with the API configuration
    * @throws SharedCollectionsException if the service cannot unsubscribe from the collection
    */
  def unsubscribe(originalSharedCollectionId: String)(implicit context: ContextSupport): TaskService[Unit]
}
