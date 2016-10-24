package cards.nine.app.ui.collections.jobs

import android.support.v7.widget.RecyclerView.ViewHolder
import cards.nine.app.ui.collections.CollectionAdapter
import cards.nine.app.ui.components.dialogs.CollectionDialog
import cards.nine.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import cards.nine.models.{Card, Collection}
import cards.nine.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}
import macroid.ContextWrapper

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

  def showCollectionDialog(
    moments: Seq[Collection],
    onCollection: (Int) => Unit)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme): Unit = {
    new CollectionDialog(moments, onCollection, () => ()).show()
  }

  def isPulling: Boolean = (pullToCloseView ~> pdvIsPulling()).get

  def getCurrentCollection: Option[Collection] = getAdapter map (_.collection)

}

trait SingleCollectionUiListener {

  def reorderCard(collectionId: Int, cardId: Int, position: Int): Unit

  def scrollY(dy: Int): Unit

  def scrollStateChanged(idDragging: Boolean, isIdle: Boolean): Unit

  def close(): Unit

  def pullToClose(scroll: Int, scrollType: ScrollType, close: Boolean): Unit

  def reloadCards(): Unit

  def moveToCollection(toCollectionId: Int, collectionPosition: Int): Unit

  def firstItemInCollection(): Unit

  def emptyCollection(): Unit

  def forceScrollType(scrollType: ScrollType): Unit

  def openReorderMode(current: ScrollType, canScroll: Boolean): Unit

  def closeReorderMode(position: Int): Unit

  def performCard(card: Card, position: Int): Unit

  def startReorderCards(holder: ViewHolder): Unit

}