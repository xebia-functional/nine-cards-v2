package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherActivity
import com.fortysevendeg.ninecardslauncher.process.collection.AddCollectionRequest
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.commons.types.{FreeCollectionType, CollectionType}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import macroid.Ui

import scalaz.concurrent.Task

class NewCollectionFragment
  extends BaseActionFragment
  with NewCollectionComposer
  with NineCardIntentConversions {

  override def getLayoutId: Int = R.layout.new_collection

  override def useFab: Boolean = true

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi(saveCollection))
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
    runUi(ui)
  }

  private[this] def saveCollection(name: String, icon: String, index: Int) = {
    val request = AddCollectionRequest(
      name = name,
      collectionType = FreeCollectionType,
      icon = icon,
      themedColorIndex = index,
      appsCategory = None
    )
    Task.fork(di.collectionProcess.addCollection(request).run).resolveAsyncUi(
      onResult = (c) => {
        activity[LauncherActivity] map { launcherActivity =>
          launcherActivity.addCollection(c)
        }
        hideKeyboard ~ unreveal()
      },
      onException = (ex) => showMessage(R.string.contactUsError)
    )
  }

}

object NewCollectionFragment {
  val iconRequest = "icon-request"
  val colorRequest = "color-request"
}