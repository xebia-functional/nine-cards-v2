package com.fortysevendeg.ninecardslauncher.process.sharedcollections

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection

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
    offset: Int,
    limit: Int)(implicit context: ContextSupport): ServiceDef2[Seq[SharedCollection], SharedCollectionsExceptions]

}
