package com.fortysevendeg.ninecardslauncher.ui.collections

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.macroid.extras.TextTweaks._
import macroid.{AppContext, Contexts}
import macroid.FullDsl._

class CollectionFragment
  extends Fragment
  with Contexts[Fragment]
  with CollectionFragmentLayout
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = fragmentAppContext

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = layout

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    import CollectionFragment._
    val position = getArguments.getInt(KeyPosition, 0)

    runUi(test <~ tvText(position.toString))

    super.onViewCreated(view, savedInstanceState)
  }
}

object CollectionFragment {
  val KeyPosition = "tab_position"
}

