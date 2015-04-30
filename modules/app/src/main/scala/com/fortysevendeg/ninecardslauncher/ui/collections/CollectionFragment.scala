package com.fortysevendeg.ninecardslauncher.ui.collections

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.models.Collection
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{AppContext, Contexts, Tweak, Ui}
import CollectionFragment._

class CollectionFragment
  extends Fragment
  with Contexts[Fragment]
  with CollectionFragmentLayout
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = fragmentAppContext

  implicit lazy val fragment: Fragment = this

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val padding = resGetDimensionPixelSize(R.dimen.padding_small)

  lazy val layoutManager = new GridLayoutManager(appContextProvider.get, NumInLine)

  var sType = -1

  lazy val position = getArguments.getInt(KeyPosition, 0)

  lazy val collection = getArguments.getSerializable(KeyCollection).asInstanceOf[Collection]

  var scrolledListener: Option[ScrolledListener] = None

  var activeFragment = false

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = layout

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    sType = getArguments.getInt(KeyScrollType, ScrollType.Down)

    runUi(recyclerView <~ vGlobalLayoutListener(view => {
      val padding = resGetDimensionPixelSize(R.dimen.padding_small)
      val paddingTop = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
      val heightCard = (view.getHeight - (padding + paddingTop)) / NumInLine
      loadCollection(collection, heightCard) ~
        uiHandler(startScroll())
    }))
    super.onViewCreated(view, savedInstanceState)
  }

  def loadCollection(collection: Collection, heightCard: Int): Ui[_] = {
    val adapter = new CollectionAdapter(collection, heightCard, card => uiShortToast(card.term))
    (recyclerView <~ rvLayoutManager(layoutManager) <~
      rvFixedSize <~
      rvAddItemDecoration(new CollectionItemDecorator) <~
      rvAdapter(adapter)) ~
      Ui {
        recyclerView map {
          rv =>
            // TODO we should change that when MultiOn events are in macroid
            rv.setOnScrollListener(new RecyclerView.OnScrollListener {
              var scrollY = 0

              override def onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int): Unit = {
                super.onScrolled(recyclerView, dx, dy)
                scrollY = scrollY + dy
                if (activeFragment && collection.cards.length > NumSpaces) {
                  scrolledListener map (_.scrollY(scrollY, dy))
                }
              }

              override def onScrollStateChanged(recyclerView: RecyclerView, newState: Int): Unit = {
                super.onScrollStateChanged(recyclerView, newState)
                if (activeFragment && newState == RecyclerView.SCROLL_STATE_IDLE  && collection.cards.length > NumSpaces) {
                  scrolledListener map {
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
            })
        }
      }
  }

  def startScroll(): Ui[_] = (collection.cards.length, sType) match {
    case (cards, s) if cards > NumSpaces => recyclerView <~ vScrollBy(0, if (s == ScrollType.Up) spaceMove else 0)
    case (_, s) => recyclerView <~ vPadding(padding, if (s == ScrollType.Up) padding else spaceMove, padding, padding)
    case _ => Ui.nop
  }

  def scrollType(newSType: Int): Ui[_] = (collection.cards.length, sType) match {
    case (cards, s) if s != newSType && cards > NumSpaces =>
      sType = newSType
      recyclerView <~
        vScrollBy(0, -Int.MaxValue) <~
        (if (sType == ScrollType.Up) vScrollBy(0, spaceMove) else Tweak.blank)
    case (_, s) if s != newSType =>
      sType = newSType
      recyclerView <~ vPadding(padding, if (newSType == ScrollType.Up) padding else spaceMove, padding, padding)
    case _ => Ui.nop
  }

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    activity match {
      case scroll: ScrolledListener => scrolledListener = Some(scroll)
      case _ =>
    }
  }

  override def onDetach(): Unit = {
    super.onDetach()
    scrolledListener = None
  }
}

object CollectionFragment {
  val KeyPosition = "tab_position"
  val KeyCollection = "collection"
  val KeyScrollType = "scroll_type"
}

