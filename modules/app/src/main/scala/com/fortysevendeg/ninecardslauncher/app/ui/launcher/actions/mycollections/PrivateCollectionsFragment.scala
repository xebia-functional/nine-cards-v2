package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.mycollections

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, NineCardIntentConversions, UiContext}
import com.fortysevendeg.ninecardslauncher.process.collection.PrivateCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import macroid.Ui

import scalaz.concurrent.Task

class PrivateCollectionsFragment
  extends BaseActionFragment
  with PrivateCollectionsComposer
  with NineCardIntentConversions
  with PrivateCollectionsTasks {

  implicit lazy val di: Injector = new Injector

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi)
    Task.fork(getPrivateCollections.run).resolveAsyncUi(
      onPreTask = () => showLoading,
      onResult = (privateCollections: Seq[PrivateCollection]) => Ui.nop,
      onException = (ex: Throwable) => showGeneralError
    )
  }
}



