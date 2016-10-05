package cards.nine.app.ui.commons.ops

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

object SharedCollectionOps {

  implicit class PrivateCollectionOp(sharedCollection: SharedCollection) {

    def getIconCollectionWorkspace(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${sharedCollection.icon.toLowerCase}") getOrElse R.drawable.icon_collection_default

    def getIconCollectionDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${sharedCollection.icon.toLowerCase}_detail") getOrElse R.drawable.icon_collection_default_detail

    def getName(implicit contextWrapper: ContextWrapper): String =
      resGetString(sharedCollection.name.toLowerCase) getOrElse sharedCollection.name

  }

}
