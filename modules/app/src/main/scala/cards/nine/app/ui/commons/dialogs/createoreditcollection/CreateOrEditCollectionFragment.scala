/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
