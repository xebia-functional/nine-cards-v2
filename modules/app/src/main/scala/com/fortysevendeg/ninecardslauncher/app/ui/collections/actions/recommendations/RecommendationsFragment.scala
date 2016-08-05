package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsPagerPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher2.R

class RecommendationsFragment(implicit collectionsPagerPresenter: CollectionsPagerPresenter)
  extends BaseActionFragment
  with RecommendationsUiActionsImpl
  with NineCardIntentConversions { self =>

  override val collectionsPresenter: CollectionsPagerPresenter = collectionsPagerPresenter

  lazy val nineCardCategory = NineCardCategory(getString(Seq(getArguments), RecommendationsFragment.categoryKey, ""))

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  lazy val presenter = new RecommendationsPresenter(nineCardCategory, packages, self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = loadBackgroundColor

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    presenter.initialize()
  }

}

object RecommendationsFragment {
  val categoryKey = "category"
}


