package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v4.app.Fragment
import android.support.v7.widget.{CardView, RecyclerView, GridLayoutManager}
import android.widget.{LinearLayout, TextView, ImageView}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RecyclerViewListenerTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardIntent, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Tweak, Ui, ActivityContextWrapper, Contexts}
import macroid.FullDsl._

trait CollectionFragmentComposer
  extends CollectionFragmentStyles {

  self: Contexts[Fragment] =>

  var sType = -1

  var canScroll = false

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val padding = resGetDimensionPixelSize(R.dimen.padding_small)

  lazy val layoutManager = new GridLayoutManager(fragmentContextWrapper.application, numInLine)

  var activeFragment = false

  var scrolledListener: Option[ScrolledListener] = None

  var recyclerView = slot[RecyclerView]

  def layout = getUi(
    w[RecyclerView] <~ wire(recyclerView) <~ recyclerStyle
  )

  def initUi(collection: Collection)(implicit fragment: Fragment, theme: NineCardsTheme) =
    recyclerView <~ vGlobalLayoutListener(view => {
      val heightCard = (view.getHeight - (padding + spaceMove)) / numInLine
      loadCollection(collection, heightCard) ~
        uiHandler(startScroll())
    })

  def loadCollection(collection: Collection, heightCard: Int)(implicit fragment: Fragment, theme: NineCardsTheme): Ui[_] = {
    val adapter = new CollectionAdapter(collection, heightCard)
    recyclerView <~ rvLayoutManager(layoutManager) <~
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
                val (moveTo, sType) = if (scrollY < spaceMove / 2) (0, ScrollType.Down) else (spaceMove, ScrollType.Up)
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

  private[this] def startScroll(): Ui[_] = (canScroll, sType) match {
    case (scroll, s) if scroll => recyclerView <~ vScrollBy(0, if (s == ScrollType.Up) spaceMove else 0)
    case (_, s) => recyclerView <~ vPadding(padding, if (s == ScrollType.Up) padding else spaceMove, padding, padding)
    case _ => Ui.nop
  }

  def scrollType(newSType: Int): Ui[_] = (canScroll, sType) match {
    case (scroll, s) if s != newSType && scroll =>
      sType = newSType
      recyclerView <~
        vScrollBy(0, -Int.MaxValue) <~
        (if (sType == ScrollType.Up) vScrollBy(0, spaceMove) else Tweak.blank)
    case (_, s) if s != newSType =>
      sType = newSType
      recyclerView <~ vPadding(padding, if (newSType == ScrollType.Up) padding else spaceMove, padding, padding)
    case _ => Ui.nop
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