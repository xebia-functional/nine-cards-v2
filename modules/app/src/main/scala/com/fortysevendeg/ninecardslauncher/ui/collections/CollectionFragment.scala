package com.fortysevendeg.ninecardslauncher.ui.collections

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
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

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = layout

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    import CollectionFragment._
    val position = getArguments.getInt(KeyPosition, 0)
    val collection = getArguments.getParcelable[Collection](KeyCollection)

    runUi(recyclerView <~ vGlobalLayoutListener(view => {
      val heightCard = (view.getHeight / NumInLine) - resGetDimensionPixelSize(R.dimen.padding_small)
      loadCollection(collection, heightCard)
    }))

    super.onViewCreated(view, savedInstanceState)
  }

  def loadCollection(collection: Collection, heightCard: Int): Ui[_] = {
    val adapter = new CollectionAdapter(collection, heightCard, card => uiShortToast(card.term))
    recyclerView <~ rvLayoutManager(new GridLayoutManager(appContextProvider.get, NumInLine)) <~
      rvFixedSize <~
      rvAddItemDecoration(new CollectionItemDecorator) <~
      rvAdapter(adapter)
  }

}

object CollectionFragment {
  val KeyPosition = "tab_position"
  val KeyCollection = "collection"
}

