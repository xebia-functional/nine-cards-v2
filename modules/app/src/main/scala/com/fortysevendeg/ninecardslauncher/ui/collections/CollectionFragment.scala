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
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.repository.Collection
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{AppContext, Contexts, Ui}

class CollectionFragment
  extends Fragment
  with Contexts[Fragment]
  with CollectionFragmentLayout
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = fragmentAppContext

  implicit lazy val fragment: Fragment = this

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  var scrolledListener: Option[ScrolledListener] = None

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = layout

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    import CollectionFragment._
    val position = getArguments.getInt(KeyPosition, 0)
    val collection = getArguments.getSerializable(KeyCollection).asInstanceOf[Collection]

    runUi(recyclerView <~ vGlobalLayoutListener(view => {
      val padding = resGetDimensionPixelSize(R.dimen.padding_small)
      val paddingTop = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
      val heightCard = (view.getHeight - (padding + paddingTop)) / NumInLine
      loadCollection(collection, heightCard)
    }))

    super.onViewCreated(view, savedInstanceState)
  }

  def loadCollection(collection: Collection, heightCard: Int): Ui[_] = {
    val adapter = new CollectionAdapter(collection, heightCard, card => uiShortToast(card.term))
    (recyclerView <~ rvLayoutManager(new GridLayoutManager(appContextProvider.get, NumInLine)) <~
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
                scrolledListener map (_.scrollY(scrollY, dy))
              }

              override def onScrollStateChanged(recyclerView: RecyclerView, newState: Int): Unit = {
                super.onScrollStateChanged(recyclerView, newState)
                scrolledListener map {
                  sl =>
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && scrollY < spaceMove) {
                      val moveTo = if (scrollY < spaceMove / 2) 0 else spaceMove
                      if (moveTo != scrollY) {
                        recyclerView.smoothScrollBy(0, moveTo - scrollY)
                      }
                    }
                }
              }
            })
        }
      }
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
}

