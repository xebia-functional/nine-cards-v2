package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.commons.models.{PrivateCollection, Collection}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Ui

class PrivateCollectionsFragment(implicit launcherPresenter: LauncherPresenter)
  extends BaseActionFragment
  with PrivateCollectionsComposer
  with NineCardIntentConversions
  with PrivateCollectionsActions { self =>

  implicit lazy val presenter = new PrivateCollectionsPresenter(self)

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    presenter.initialize()
  }

  override def initialize(): Ui[Any] = initUi

  override def addPrivateCollections(privateCollections: Seq[PrivateCollection]): Ui[Any] =
    reloadPrivateCollections(privateCollections)

  override def addCollection(collection: Collection): Ui[Any] = Ui {
    launcherPresenter.addCollection(collection)
  }

  override def showLoading(): Ui[Any] = showLoadingView

  override def showEmptyMessage(): Ui[Any] = showError(R.string.messageEmpty, presenter.loadPrivateCollections())

  override def showContactUsError(): Ui[Any] = showError(R.string.contactUsError)

  override def close(): Ui[Any] = unreveal()
}



