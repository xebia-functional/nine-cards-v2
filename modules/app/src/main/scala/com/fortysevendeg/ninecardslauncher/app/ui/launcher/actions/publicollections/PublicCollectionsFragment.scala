package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, NineCardIntentConversions, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherActivity
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._

import scalaz.concurrent.Task

class PublicCollectionsFragment
  extends BaseActionFragment
  with PublicCollectionsComposer
  with NineCardIntentConversions
  with PublicCollectionsListener
  with PublicCollectionsTasks {

  implicit lazy val di: Injector = new Injector

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi)
    loadPublicCollections()
  }

  override def loadPublicCollections(): Unit =
    Task.fork(getSharedCollections(statuses.category, statuses.typeSharedCollection).run).resolveAsyncUi(
      onPreTask = () => showLoading,
      onResult = (sharedCollections: Seq[SharedCollection]) => addPublicCollections(sharedCollections),
      onException = (ex: Throwable) => showGeneralError)

  override def saveSharedCollection(sharedCollection: SharedCollection): Unit =
    Task.fork(addCollection(sharedCollection).run).resolveAsyncUi(
      onResult = (c) => {
        activity[LauncherActivity] map (_.addCollection(c))
        unreveal()
      },
      onException = (ex) => showGeneralError)

}



