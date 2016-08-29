package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections._
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{CreateSharedCollection, SharedCollection, UpdateSharedCollection}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._

class SharedCollectionsProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends SharedCollectionsProcess
  with Conversions
  with ImplicitsSharedCollectionsExceptions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getSharedCollectionsByCategory(
    category: NineCardCategory,
    typeShareCollection: TypeSharedCollection,
    offset: Int = 0,
    limit: Int = 50)
    (implicit context: ContextSupport): TaskService[Seq[SharedCollection]] =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getSharedCollectionsByCategory(category.name, typeShareCollection.name, offset, limit)(userConfig)
    } yield response.items map toSharedCollection).resolve[SharedCollectionsExceptions]

  override def createSharedCollection(
    sharedCollection: CreateSharedCollection)
    (implicit context: ContextSupport) = {
    import sharedCollection._
    (for {
      userConfig <- apiUtils.getRequestConfig
      result <- apiServices.createSharedCollection(name, description, author, packages, category.name, icon, community)(userConfig)
    } yield result.sharedCollectionId).resolve[SharedCollectionsExceptions]
  }

  override def updateSharedCollection(sharedCollection: UpdateSharedCollection)(implicit context: ContextSupport) = {
    import sharedCollection._
    (for {
      userConfig <- apiUtils.getRequestConfig
      result <- apiServices.updateSharedCollection(sharedCollectionId, Option(name), description, packages)(userConfig)
    } yield result.sharedCollectionId).resolve[SharedCollectionsExceptions]
  }
}
