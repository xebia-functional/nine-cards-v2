package com.fortysevendeg.ninecardslauncher.app.ui.commons

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.CreatedCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

object CollectionOps {

  implicit class CollectionOp(collection: Collection) {

    def getUrlSharedCollection(implicit contextWrapper: ContextWrapper): Option[String] =
      collection.sharedCollectionId map { c =>
        resGetString(R.string.shared_collection_url, c)
      }

    def getUrlOriginalSharedCollection(implicit contextWrapper: ContextWrapper): Option[String] =
      collection.originalSharedCollectionId map { c =>
        resGetString(R.string.shared_collection_url, c)
      }

  }

  implicit class CreatedCollectionOp(createdCollection: CreatedCollection) {

    def getUrlSharedCollection(implicit contextWrapper: ContextWrapper): String =
        resGetString(R.string.shared_collection_url, createdCollection.sharedCollectionId)

  }

}
