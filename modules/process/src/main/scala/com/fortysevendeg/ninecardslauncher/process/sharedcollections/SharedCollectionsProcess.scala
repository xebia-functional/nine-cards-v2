package com.fortysevendeg.ninecardslauncher.process.sharedcollections

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{CreateSharedCollection, SharedCollection, CreatedCollection}

trait SharedCollectionsProcess {

  /**
    * Get shared collections based on a category
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
    limit: Int = 50)(implicit context: ContextSupport): ServiceDef2[Seq[SharedCollection], SharedCollectionsExceptions]

  /**
    * Persist a [[com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection]]
    * @param sharedCollection the defined collection to create
    * @return unit, to signify successful completion
    * @throws SharedCollectionsExceptions if the service cannot create the collection for some reason
    */
  def createSharedCollection(
    sharedCollection: CreateSharedCollection
  )(implicit context: ContextSupport): ServiceDef2[CreatedCollection, SharedCollectionsExceptions]
}
