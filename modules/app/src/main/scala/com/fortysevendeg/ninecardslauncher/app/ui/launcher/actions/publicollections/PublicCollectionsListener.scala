package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.TypeSharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection

trait PublicCollectionsListener {

  def loadPublicCollections(category: NineCardCategory, typeSharedCollection: TypeSharedCollection): Unit

  def saveSharedCollection(sharedCollection: SharedCollection): Unit

}
