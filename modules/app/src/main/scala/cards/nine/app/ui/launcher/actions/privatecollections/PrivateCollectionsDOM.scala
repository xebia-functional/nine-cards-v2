package cards.nine.app.ui.launcher.actions.privatecollections

import cards.nine.models.{Collection, CollectionData}
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait PrivateCollectionsDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.actions_recycler)

}

trait PrivateCollectionsListener {

  def loadPrivateCollections(): Unit

  def saveCollection(collection: CollectionData): Unit

}
