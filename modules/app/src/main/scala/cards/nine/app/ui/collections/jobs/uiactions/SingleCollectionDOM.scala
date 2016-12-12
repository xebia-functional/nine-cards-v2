package cards.nine.app.ui.collections.jobs.uiactions

import android.support.v7.widget.RecyclerView.ViewHolder
import cards.nine.app.ui.collections.CollectionAdapter
import cards.nine.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import cards.nine.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait SingleCollectionDOM {

  self: TypedFindView =>

  lazy val emptyCollectionView = findView(TR.collection_detail_empty)

  lazy val emptyCollectionMessage = findView(TR.collection_empty_message)

  lazy val recyclerView = findView(TR.collection_detail_recycler)

  lazy val pullToCloseView = findView(TR.collection_detail_pull_to_close)

  def getAdapter: Option[CollectionAdapter] = recyclerView.getAdapter match {
    case a: CollectionAdapter => Some(a)
    case _ => None
  }

  def isPulling: Boolean = (pullToCloseView ~> pdvIsPulling()).get

  def getCurrentCollection: Option[Collection] = getAdapter map (_.collection)

}

trait SingleCollectionUiListener {

  def reorderCard(collectionId: Int, cardId: Int, position: Int): Unit

  def scrollStateChanged(idDragging: Boolean): Unit

  def close(): Unit

  def pullToClose(scroll: Int, close: Boolean): Unit

  def reloadCards(): Unit

  def moveToCollection(toCollectionId: Int, collectionPosition: Int): Unit

  def firstItemInCollection(): Unit

  def emptyCollection(): Unit

  def openReorderMode(): Unit

  def closeReorderMode(position: Int): Unit

  def performCard(card: Card, position: Int): Unit

  def startReorderCards(holder: ViewHolder): Unit

}