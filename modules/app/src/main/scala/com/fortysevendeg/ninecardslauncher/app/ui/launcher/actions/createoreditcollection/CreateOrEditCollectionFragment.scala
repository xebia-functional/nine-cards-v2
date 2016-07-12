package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.createoreditcollection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Ui

class CreateOrEditCollectionFragment(implicit lPresenter: LauncherPresenter)
  extends BaseActionFragment
  with CreateOrEditCollectionActionsImpl
  with NineCardIntentConversions { self =>

  lazy val launcherPresenter = lPresenter

  lazy val presenter = new CreateOrEditCollectionPresenter(self)

  override def getLayoutId: Int = R.layout.new_collection

  override def useFab: Boolean = true

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    presenter.initialize()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (RequestCodes.selectInfoIcon, Activity.RESULT_OK) =>
        val maybeCategory = Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(CreateOrEditCollectionFragment.iconRequest) =>
            Some(NineCardCategory(extras.getString(CreateOrEditCollectionFragment.iconRequest)))
          case _ => None
        } getOrElse None
        presenter.updateCategory(maybeCategory)
      case (RequestCodes.selectInfoColor, Activity.RESULT_OK) =>
        val maybeIndexColor = Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(CreateOrEditCollectionFragment.colorRequest) =>
            Some(extras.getInt(CreateOrEditCollectionFragment.colorRequest))
          case _ => None
        } getOrElse None
        presenter.updateColor(maybeIndexColor)
      case _ =>
    }
  }
}

object CreateOrEditCollectionFragment {
  val iconRequest = "icon-request"
  val colorRequest = "color-request"
}