package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog.publishcollection

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.{DialogFragment, Fragment}
import android.support.v7.app.AlertDialog
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs.SharedCollectionJobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.SharedCollectionsConfigurationException
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._

import scala.language.postfixOps

case class PublishCollectionFragment(collection: Collection)(implicit val sharedCollectionJobs: SharedCollectionJobs)
  extends DialogFragment
  with PublishCollectionDOM
  with PublishCollectionUiListener
  with TypedFindView
  with Contexts[Fragment]
  with NineCardIntentConversions { self =>

  lazy val actions = new PublishCollectionActions(self)

  lazy val publishCollectionJobs = new PublishCollectionJobs(actions)

  protected var rootView: Option[PublishCollectionWizardStartView] = None

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    val view = new PublishCollectionWizardStartView

    rootView = Some(view)

    publishCollectionJobs.initialize(collection).resolveAsync()

    new AlertDialog.Builder(getActivity).setView(view).create()
  }

  class PublishCollectionWizardStartView
    extends LinearLayout(fragmentContextWrapper.bestAvailable) {

    LayoutInflater.from(getActivity).inflate(R.layout.publish_collection_wizard, this)

  }

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  override def showCollectionInformation(): Unit =
    publishCollectionJobs.
      showCollectionInformation().
      resolveAsyncServiceOr(_ => publishCollectionJobs.showCollectionError())

  override def launchShareCollection(sharedCollectionId: String): Unit =
    publishCollectionJobs.
      launchShareCollection(sharedCollectionId).
      resolveAsyncServiceOr(_ => publishCollectionJobs.showGenericError())

  override def reloadSharedCollectionId(): Unit =
    sharedCollectionJobs.reloadSharedCollectionId().resolveAsync()

  override def publishCollection(name: Option[String], category: Option[NineCardCategory]): Unit =
    publishCollectionJobs.publishCollection(name, category).resolveAsyncServiceOr { (e: Throwable) =>
      e match {
        case e: SharedCollectionsConfigurationException =>
          AppLog.invalidConfigurationV2
          publishCollectionJobs.showPublishingError(name, category)
        case _ => publishCollectionJobs.showPublishingError(name, category)
      }
    }
}
