package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.Tweak

class AppsFragment
  extends BaseActionFragment
  with AppsComposer {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.list_action_fragment, container, false)
    rootView = Option(view)
    view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      override def onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int): Unit = {
        v.removeOnLayoutChangeListener(this)
        width = right - left
        height = bottom - top
        runUi(reveal)
      }
    })
    view
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(toolbar <~ Tweak[Toolbar](_.setTitle(R.string.applications)))
  }
}


