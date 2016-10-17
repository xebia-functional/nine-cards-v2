package cards.nine.app.ui.launcher.actions.privatecollections

import cards.nine.process.commons.models.{Collection, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait PrivateCollectionsDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.actions_recycler)

}

trait PrivateCollectionsListener {

  def loadPrivateCollections(): Unit

  def addLauncherCollection(collection: Collection): Unit

  def saveCollection(collection: PrivateCollection): Unit

}
