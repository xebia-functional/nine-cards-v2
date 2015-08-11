package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v4.app.Fragment
import android.support.v7.widget.{CardView, GridLayoutManager, RecyclerView}
import android.util.Log
import android.widget.{ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RecyclerViewListenerTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{PullToCloseListener, PullToCloseView}
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._
import com.fortysevendeg.ninecardslauncher.app.ui.components.PullToCloseViewTweaks._

trait CollectionFragmentComposer
  extends CollectionFragmentStyles {

  var sType = -1

  var canScroll = false

  var activeFragment = false

  var scrolledListener: Option[ScrolledListener] = None

  var recyclerView = slot[RecyclerView]

  def layout(implicit contextWrapper: ActivityContextWrapper) = getUi(
    l[PullToCloseView](
      w[RecyclerView] <~ wire(recyclerView) <~ recyclerStyle
    ) <~ pcvListener(PullToCloseListener(
      scroll = (scroll: Int, close: Boolean) => scrolledListener foreach (_.pullToClose(scroll, close)),
      close = () => scrolledListener foreach (_.close())
    ))
  )

  def initUi(collection: Collection)(implicit contextWrapper: ActivityContextWrapper, fragment: Fragment, theme: NineCardsTheme) =
    recyclerView <~ vGlobalLayoutListener(view => {
      val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
      val padding = resGetDimensionPixelSize(R.dimen.padding_small)
      val heightCard = (view.getHeight - (padding + spaceMove)) / numInLine
      loadCollection(collection, heightCard, padding, spaceMove) ~
        uiHandler(startScroll(padding, spaceMove))
    })

  def loadCollection(collection: Collection, heightCard: Int, padding: Int, spaceMove: Int)
    (implicit contextWrapper: ActivityContextWrapper, fragment: Fragment, theme: NineCardsTheme): Ui[_] = {
    val adapter = new CollectionAdapter(collection, heightCard)
    recyclerView <~ rvLayoutManager(new GridLayoutManager(contextWrapper.application, numInLine)) <~
      rvFixedSize <~
      rvAddItemDecoration(new CollectionItemDecorator) <~
      rvAdapter(adapter) <~
      rvCollectionScrollListener(
        (scrollY: Int, dx: Int, dy: Int) => {
          val sy = scrollY + dy
          if (activeFragment && collection.cards.length > numSpaces) {
            scrolledListener foreach (_.scrollY(sy, dy))
          }
          sy
        },
        (scrollY: Int, recyclerView: RecyclerView, newState: Int) => {
          if (activeFragment && newState == RecyclerView.SCROLL_STATE_IDLE && collection.cards.length > numSpaces) {
            scrolledListener foreach {
              sl =>
                val (moveTo, sType) = if (scrollY < spaceMove / 2) (0, ScrollType.down) else (spaceMove, ScrollType.up)
                (scrollY, moveTo, sType) match {
                  case (y, move, st) if y < spaceMove && moveTo != scrollY =>
                    sl.scrollType(sType)
                    recyclerView.smoothScrollBy(0, moveTo - scrollY)
                  case _ =>
                }
                sl.scrollType(sType)
            }
          }
        }
      )
  }

  private[this] def startScroll(padding: Int, spaceMove: Int)(implicit contextWrapper: ContextWrapper): Ui[_] =
    (canScroll, sType) match {
      case (scroll, s) if scroll => recyclerView <~ vScrollBy(0, if (s == ScrollType.up) spaceMove else 0)
      case (_, s) => recyclerView <~ vPadding(padding, if (s == ScrollType.up) padding else spaceMove, padding, padding)
      case _ => Ui.nop
    }

  def scrollType(newSType: Int)(implicit contextWrapper: ContextWrapper): Ui[_] = {
    val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    (canScroll, sType) match {
      case (scroll, s) if s != newSType && scroll =>
        sType = newSType
        recyclerView <~
          vScrollBy(0, -Int.MaxValue) <~
          (if (sType == ScrollType.up) vScrollBy(0, spaceMove) else Tweak.blank)
      case (_, s) if s != newSType =>
        sType = newSType
        recyclerView <~ vPadding(padding, if (newSType == ScrollType.up) padding else spaceMove, padding, padding)
      case _ => Ui.nop
    }
  }
}

class CollectionLayoutAdapter(heightCard: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends CollectionAdapterStyles {

  var icon = slot[ImageView]

  var name = slot[TextView]

  val content = layout

  private def layout(implicit context: ActivityContextWrapper) = getUi(
    l[CardView](
      l[LinearLayout](
        w[ImageView] <~ wire(icon) <~ iconStyle,
        w[TextView] <~ wire(name) <~ nameStyle
      ) <~ contentStyle
    ) <~ rootStyle(heightCard)
  )

}

class ViewHolderCollectionAdapter(adapter: CollectionLayoutAdapter)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(adapter.content) {

  val content = adapter.content

  val icon = adapter.icon

  val name = adapter.name

}