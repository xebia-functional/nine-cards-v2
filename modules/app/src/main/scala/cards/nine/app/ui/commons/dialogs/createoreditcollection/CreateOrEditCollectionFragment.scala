package cards.nine.app.ui.commons.dialogs.createoreditcollection

import android.app.{Activity, Dialog}
import android.content.Intent
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.jobs.LauncherJobs
import cards.nine.commons.javaNull
import cards.nine.commons.services.TaskService._
import cards.nine.models.Collection
import com.fortysevendeg.ninecardslauncher.R

class CreateOrEditCollectionFragment(implicit launcherJobs: LauncherJobs)
    extends BaseActionFragment
    with CreateOrEditCollectionDOM
    with CreateOrEditCollectionUiActions
    with CreateOrEditCollectionListener
    with AppNineCardsIntentConversions { self =>

  lazy val maybeCollectionId = Option(
    getString(Seq(getArguments), CreateOrEditCollectionFragment.collectionId, javaNull))

  lazy val collectionJobs = new CreateOrEditCollectionJobs(self)

  override def getLayoutId: Int = R.layout.new_collection

  override def useFab: Boolean = true

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    collectionJobs.initialize(maybeCollectionId).resolveServiceOr(_ => showMessageContactUsError)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (RequestCodes.selectInfoIcon, Activity.RESULT_OK) =>
        val maybeIcon = Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(CreateOrEditCollectionFragment.iconRequest) =>
            Some(extras.getString(CreateOrEditCollectionFragment.iconRequest))
          case _ => None
        } getOrElse None
        collectionJobs.updateIcon(maybeIcon).resolveAsyncServiceOr(_ => showMessageContactUsError)
      case (RequestCodes.selectInfoColor, Activity.RESULT_OK) =>
        val maybeIndexColor = Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(CreateOrEditCollectionFragment.colorRequest) =>
            Some(extras.getInt(CreateOrEditCollectionFragment.colorRequest))
          case _ => None
        } getOrElse None
        collectionJobs
          .updateColor(maybeIndexColor)
          .resolveAsyncServiceOr(_ => showMessageContactUsError)
      case _ =>
    }
  }

  override def changeColor(maybeColor: Option[Int]): Unit =
    collectionJobs.changeColor(maybeColor).resolveAsyncServiceOr(_ => showMessageContactUsError)

  override def changeIcon(maybeIcon: Option[String]): Unit =
    collectionJobs.changeIcon(maybeIcon).resolveAsyncServiceOr(_ => showMessageContactUsError)

  override def saveCollection(
      maybeName: Option[String],
      maybeIcon: Option[String],
      maybeIndex: Option[Int]): Unit =
    ((maybeName, maybeIcon, maybeIndex) match {
      case (Some(nameCollection), Some(icon), Some(themedColorIndex)) =>
        for {
          collection <- collectionJobs.saveCollection(nameCollection, icon, themedColorIndex)
          _          <- launcherJobs.addCollection(collection)
        } yield ()
      case _ => showMessageFormFieldError
    }).resolveServiceOr(_ => showMessageContactUsError)

  override def editCollection(
      collection: Collection,
      maybeName: Option[String],
      maybeIcon: Option[String],
      maybeIndex: Option[Int]): Unit = {
    ((maybeName, maybeIcon, maybeIndex) match {
      case (Some(nameCollection), Some(icon), Some(themedColorIndex)) =>
        for {
          collection <- collectionJobs.editCollection(
            collection,
            nameCollection,
            icon,
            themedColorIndex)
          _ <- launcherJobs.updateCollection(collection)
        } yield ()
      case _ => showMessageFormFieldError
    }).resolveServiceOr(_ => showMessageContactUsError)
  }
}

object CreateOrEditCollectionFragment {
  val iconRequest  = "icon-request"
  val colorRequest = "color-request"
  val collectionId = "collectionId"
}
