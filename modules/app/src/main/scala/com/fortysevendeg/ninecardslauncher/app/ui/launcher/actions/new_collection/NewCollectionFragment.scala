package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.new_collection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ActivityResult, NineCardIntentConversions}
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

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (ActivityResult.selectInfoIcon, Activity.RESULT_OK) =>
        Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(NewCollectionFragment.iconRequest) =>
            runUi(setCategory(extras.getString(NewCollectionFragment.iconRequest)))
        } getOrElse runUi(showGeneralError)
      case (ActivityResult.selectInfoColor, Activity.RESULT_OK) =>
        Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(NewCollectionFragment.colorRequest) =>
            runUi(setIndexColor(extras.getInt(NewCollectionFragment.colorRequest)))
        } getOrElse runUi(showGeneralError)
      case _ =>
    }
  }

}

object NewCollectionFragment {
  val iconRequest = "icon-request"
  val colorRequest = "color-request"
}