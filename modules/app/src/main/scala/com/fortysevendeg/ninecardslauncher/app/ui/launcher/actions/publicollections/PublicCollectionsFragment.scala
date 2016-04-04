package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.TypeSharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Ui

class PublicCollectionsFragment(implicit launcherPresenter: LauncherPresenter)
  extends BaseActionFragment
  with PublicCollectionsComposer
  with NineCardIntentConversions
  with PublicCollectionsUiActions { self =>

  implicit lazy val presenter: PublicCollectionsPresenter = new PublicCollectionsPresenter(self)

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    presenter.initialize()
  }

  override def initialize(): Ui[Any] = initUi

  override def showContactUsError(): Ui[Any] = showError(R.string.contactUsError, () => {
    presenter.loadPublicCollections()
  })

  override def loadPublicCollections(sharedCollections: Seq[SharedCollection]): Ui[Any] =
    reloadPublicCollections(sharedCollections)

  override def addCollection(collection: Collection): Ui[Any] = {
    launcherPresenter.addCollection(collection)
    unreveal()
  }

  override def showLoading(): Ui[Any] = showLoadingView

  override def updateCategory(category: NineCardCategory): Ui[Any] = changeCategoryName(category)

  override def updateTypeCollection(typeSharedCollection: TypeSharedCollection): Ui[Any] = changeTypeCollection(typeSharedCollection)

}




