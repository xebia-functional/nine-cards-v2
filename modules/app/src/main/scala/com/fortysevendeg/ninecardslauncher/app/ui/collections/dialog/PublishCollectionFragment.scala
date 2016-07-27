package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.{DialogFragment, Fragment}
import android.support.v7.app.AlertDialog
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._

case class PublishCollectionFragment(collection: Collection)
  extends DialogFragment
  with PublishCollectionActionsImpl
  with TypedFindView
  with Contexts[Fragment]
  with NineCardIntentConversions { self =>

  lazy val publishCollectionPresenter = new PublishCollectionPresenter(self)

  protected var rootView: Option[PublishCollectionWizardStartView] = None

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    val view = new PublishCollectionWizardStartView

    rootView = Some(view)

    publishCollectionPresenter.initialize(collection)

    new AlertDialog.Builder(getActivity).setView(view).create()
  }

  class PublishCollectionWizardStartView
    extends LinearLayout(fragmentContextWrapper.bestAvailable) {

    LayoutInflater.from(getActivity).inflate(R.layout.publish_collection_wizard, this)

  }

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull
}
