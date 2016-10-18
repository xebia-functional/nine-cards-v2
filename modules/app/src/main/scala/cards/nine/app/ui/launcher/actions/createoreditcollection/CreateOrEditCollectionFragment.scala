package cards.nine.app.ui.launcher.actions.createoreditcollection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.launcher.{LauncherActivity, LauncherPresenter}
import cards.nine.commons.javaNull
import cards.nine.models.Collection
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

class CreateOrEditCollectionFragment
  extends BaseActionFragment
  with CreateOrEditCollectionDOM
  with CreateOrEditCollectionUiActions
  with CreateOrEditCollectionListener
  with AppNineCardsIntentConversions { self =>

  // TODO First implementation in order to remove LauncherPresenter
  def launcherPresenter: LauncherPresenter = getActivity match {
    case activity: LauncherActivity => activity.presenter
    case _ => throw new RuntimeException("LauncherPresenter not found")
  }

  lazy val maybeCollectionId = Option(getString(Seq(getArguments), CreateOrEditCollectionFragment.collectionId, javaNull))

  lazy val collectionJobs = new CreateOrEditCollectionJobs(self)

  override def getLayoutId: Int = R.layout.new_collection

  override def useFab: Boolean = true

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
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
        collectionJobs.updateColor(maybeIndexColor).resolveAsyncServiceOr(_ => showMessageContactUsError)
      case _ =>
    }
  }

  override def changeColor(maybeColor: Option[Int]): Unit =
    collectionJobs.changeColor(maybeColor).resolveAsyncServiceOr(_ => showMessageContactUsError)

  override def changeIcon(maybeIcon: Option[String]): Unit =
    collectionJobs.changeIcon(maybeIcon).resolveAsyncServiceOr(_ => showMessageContactUsError)

  override def saveCollection(maybeName: Option[String], maybeIcon: Option[String], maybeIndex: Option[Int]): Unit =
    collectionJobs.saveCollection(maybeName, maybeIcon, maybeIndex).resolveServiceOr(_ => showMessageContactUsError)

  override def editCollection(collection: Collection, maybeName: Option[String], maybeIcon: Option[String], maybeIndex: Option[Int]): Unit =
    collectionJobs.editCollection(collection, maybeName, maybeIcon, maybeIndex).resolveServiceOr(_ => showMessageContactUsError)

  override def updateLauncherCollection(collection: Collection): Unit = launcherPresenter.updateCollection(collection)

  override def addLauncherCollection(collection: Collection): Unit = launcherPresenter.addCollection(collection)
}

object CreateOrEditCollectionFragment {
  val iconRequest = "icon-request"
  val colorRequest = "color-request"
  val collectionId = "collectionId"
}