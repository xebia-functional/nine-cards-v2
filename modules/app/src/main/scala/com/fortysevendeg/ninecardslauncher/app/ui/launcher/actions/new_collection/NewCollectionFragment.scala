package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.new_collection

import android.graphics.Color
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._

class NewCollectionFragment
  extends BaseActionFragment
  with NewCollectionComposer
  with NineCardIntentConversions {

  implicit lazy val di: Injector = new Injector

  override def getLayoutId: Int = R.layout.new_collection

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    runUi(tintIcons(Color.GRAY))
    super.onCreateView(inflater, container, savedInstanceState)
  }

  override def onDestroy(): Unit = {
    runUi(tintIcons(Color.TRANSPARENT))
    super.onDestroy()
  }
}


