package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection

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

class NewCollectionFragment(implicit launcherPresenter: LauncherPresenter)
  extends BaseActionFragment
  with NewCollectionComposer
  with NewCollectionActions
  with NineCardIntentConversions { self =>

  implicit lazy val presenter = new NewCollectionPresenter(self)

  override def getLayoutId: Int = R.layout.new_collection

  override def useFab: Boolean = true

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    initUi.run
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    val ui = (requestCode, resultCode) match {
      case (RequestCodes.selectInfoIcon, Activity.RESULT_OK) =>
        Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(NewCollectionFragment.iconRequest) =>
            setCategory(NineCardCategory(extras.getString(NewCollectionFragment.iconRequest)))
          case _ => Ui.nop
        } getOrElse showGeneralError
      case (RequestCodes.selectInfoColor, Activity.RESULT_OK) =>
        Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(NewCollectionFragment.colorRequest) =>
            setIndexColor(extras.getInt(NewCollectionFragment.colorRequest))
          case _ => Ui.nop
        } getOrElse showGeneralError
      case _ => Ui.nop
    }
    ui.run
  }

  override def addCollection(collection: Collection): Ui[Any] = {
    launcherPresenter.addCollection(collection) ~
      hideKeyboard ~
      unreveal()
  }

  override def showMessageContactUsError: Ui[Any] = showMessage(R.string.contactUsError)

  override def showMessageFormFieldError: Ui[Any] = showMessage(R.string.formFieldError)
}

object NewCollectionFragment {
  val iconRequest = "icon-request"
  val colorRequest = "color-request"
}