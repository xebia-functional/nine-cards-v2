package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import com.fortysevendeg.ninecardslauncher.process.commons.types.{Communication, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.{TopSharedCollection, TypeSharedCollection}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection

trait PublicCollectionsListener {

  protected var statuses = PublicCollectionStatuses(Communication, TopSharedCollection)

  def changeCategory(category: NineCardCategory) = {
    statuses = statuses.copy(category = category)
    loadPublicCollections()
  }

  def changeTypeSharedCollection(typeSharedCollection: TypeSharedCollection) = {
    statuses = statuses.copy(typeSharedCollection = typeSharedCollection)
    loadPublicCollections()
  }

  def loadPublicCollections(): Unit

  def saveSharedCollection(sharedCollection: SharedCollection): Unit

}

case class PublicCollectionStatuses(category: NineCardCategory, typeSharedCollection: TypeSharedCollection)
