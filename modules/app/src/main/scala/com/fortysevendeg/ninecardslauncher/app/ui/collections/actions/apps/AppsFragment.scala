package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import scalaz.concurrent.Task

class AppsFragment
  extends BaseActionFragment
  with AppsComposer {

  implicit lazy val di: Injector = new Injector

  implicit lazy val fragment: Fragment = this // TODO : javi => We need that, but I don't like. We need a better way

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View =
    createBaseView(inflater.inflate(R.layout.list_action_fragment, container, false))

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi)
    Task.fork(di.deviceProcess.getCategorizedApps.run).resolveAsyncUi(
      onResult = (apps: Seq[AppCategorized]) => addApps(apps)
    )
  }
}


