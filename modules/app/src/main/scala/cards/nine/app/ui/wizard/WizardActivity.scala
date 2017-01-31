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

package cards.nine.app.ui.wizard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{ActivityUiContext, SynchronizeDeviceJobs, UiContext}
import cards.nine.app.ui.wizard.jobs._
import cards.nine.app.ui.wizard.jobs.uiactions._
import cards.nine.commons.services.TaskService._
import cards.nine.models.PackagesByCategory
import cards.nine.models.types.NineCardsMoment
import cards.nine.process.cloud.CloudStorageClientListener
import cards.nine.process.social.{SocialProfileClientListener, SocialProfileProcessException}
import cards.nine.process.user.UserException
import cards.nine.process.userv1.UserV1Exception
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import com.google.android.gms.common.ConnectionResult
import macroid.{ActivityContextWrapper, Contexts, FragmentManagerContext}

import WizardActivity._

class WizardActivity
    extends AppCompatActivity
    with Contexts[AppCompatActivity]
    with ContextSupportProvider
    with TypedFindView
    with SocialProfileClientListener
    with CloudStorageClientListener
    with WizardUiListener { self =>

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(self)

  lazy val wizardJobs = createWizardJobs

  lazy val newConfigurationJobs = createNewConfigurationJobs

  lazy val loadConfigurationJobs = new LoadConfigurationJobs

  lazy val synchronizeDeviceJobs = new SynchronizeDeviceJobs

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.wizard_activity)
    wizardJobs.initialize().resolveAsync()
  }

  override def onStop(): Unit = {
    wizardJobs.stop().resolveAsync()
    super.onStop()
  }

  override def onBackPressed(): Unit = {}

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit =
    wizardJobs.activityResult(requestCode, resultCode, data).resolveAsyncServiceOr(onException)

  override def onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array[String],
      grantResults: Array[Int]): Unit =
    wizardJobs
      .requestPermissionsResult(requestCode, permissions, grantResults)
      .resolveAsyncServiceOr(onException)

  override def onClickAcceptTermsButton(): Unit =
    wizardJobs.connectAccount().resolveAsync()

  override def onClickVisitTermsButton(): Unit =
    wizardJobs.showTermOfUseWebsite().resolveAsync()

  override def onClickSelectV1DeviceButton(packages: Seq[PackagesByCategory]): Unit =
    wizardJobs
      .deviceSelected(packages)
      .resolveAsyncServiceOr(_ => wizardJobs.visibilityUiActions.goToUser())

  override def onClickSelectDeviceButton(maybeCloudId: Option[String]): Unit =
    wizardJobs
      .deviceSelected(maybeCloudId)
      .resolveAsyncServiceOr(_ => wizardJobs.visibilityUiActions.goToUser())

  override def onClickFinishWizardButton(): Unit =
    wizardJobs.finishWizard().resolveAsync()

  override def onPlusConnectionSuspended(cause: Int): Unit = {}

  override def onPlusConnected(): Unit =
    wizardJobs.plusConnected().resolveAsyncServiceOr(onException)

  override def onPlusConnectionFailed(connectionResult: ConnectionResult): Unit =
    wizardJobs.plusConnectionFailed(connectionResult).resolveAsync()

  override def onDriveConnectionSuspended(cause: Int): Unit = {}

  override def onDriveConnected(): Unit =
    wizardJobs.driveConnected().resolveAsyncServiceOr(onException)

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    wizardJobs.driveConnectionFailed(connectionResult).resolveAsync()

  override def onClickOkMarketPermissionDialog(): Unit =
    wizardJobs.requestAndroidMarketPermission().resolveAsyncServiceOr(onException)

  override def onClickCancelMarketPermissionDialog(): Unit =
    wizardJobs.visibilityUiActions.goToUser().resolveAsync()

  override def onClickOkGooglePermissionDialog(): Unit =
    wizardJobs.requestGooglePermission().resolveAsyncServiceOr(onException)

  override def onClickCancelGooglePermissionDialog(): Unit =
    wizardJobs.visibilityUiActions.goToUser().resolveAsync()

  override def onClickOkSelectAccountsDialog(): Unit =
    wizardJobs.connectAccount().resolveAsync()

  override def onClickCancelSelectAccountsDialog(): Unit = {}

  override def onClickOkPermissionsDialog(): Unit =
    wizardJobs.requestPermissions().resolveAsync()

  override def onClickCancelPermissionsDialog(): Unit =
    wizardJobs.permissionDialogCancelled().resolveAsync()

  override def onStartLoadConfiguration(cloudId: String): Unit =
    (for {
      client <- wizardJobs.googleDriveClient
      _      <- loadConfigurationJobs.loadConfiguration(client, cloudId)
      _      <- wizardJobs.wizardUiActions.showDiveIn()
    } yield ()).resolveAsyncServiceOr(_ =>
      wizardJobs.wizardUiActions.showErrorGeneral() *> wizardJobs.visibilityUiActions.goToUser())

  override def onStartNewConfiguration(packages: Seq[PackagesByCategory]): Unit =
    newConfigurationJobs.newConfigurationActions.loadFirstStep(packages).resolveAsync()

  override def onLoadBetterCollections(packages: Seq[PackagesByCategory]): Unit =
    newConfigurationJobs.loadBetterCollections(packages).resolveAsync()

  override def onSaveCollections(collections: Seq[PackagesByCategory]): Unit =
    newConfigurationJobs.saveCollections(collections).resolveAsyncServiceOr[Throwable] {
      case ex: WizardNoCollectionsSelectedException =>
        wizardJobs.wizardUiActions.showNoCollectionsSelectedMessage() *>
          newConfigurationJobs.rollbackLoadBetterCollections()
      case _ =>
        wizardJobs.wizardUiActions.showErrorGeneral() *>
          newConfigurationJobs.rollbackLoadBetterCollections()
    }

  override def onLoadMomentWithWifi(): Unit =
    newConfigurationJobs.loadMomentWithWifi().resolveAsync()

  override def onSaveMomentsWithWifi(infoMoment: Seq[(NineCardsMoment, Option[String])]): Unit =
    newConfigurationJobs
      .saveMomentsWithWifi(infoMoment)
      .resolveAsyncServiceOr(_ => newConfigurationJobs.rollbackMomentWithWifi())

  override def onSaveMoments(moments: Seq[NineCardsMoment]): Unit = {
    (for {
      _      <- newConfigurationJobs.saveMoments(moments)
      client <- wizardJobs.googleDriveClient
      _      <- synchronizeDeviceJobs.synchronizeDevice(client)
      _      <- wizardJobs.visibilityUiActions.showNewConfiguration()
      _      <- newConfigurationJobs.newConfigurationActions.loadSixthStep()
    } yield ()).resolveAsyncServiceOr { _ =>
      for {
        _ <- wizardJobs.visibilityUiActions.cleanNewConfiguration()
        _ <- wizardJobs.visibilityUiActions.showNewConfiguration()
        _ <- newConfigurationJobs.newConfigurationActions.loadSixthStep()
      } yield ()
    }
  }

  private[this] def onException[E >: Throwable]: (E) => TaskService[Unit] = {
    case ex: SocialProfileProcessException if ex.recoverable =>
      wizardJobs.googleSignIn()
    case _: UserException =>
      wizardJobs.wizardUiActions.showErrorLoginUser() *> wizardJobs.visibilityUiActions.goToUser()
    case _: UserV1Exception =>
      wizardJobs.wizardUiActions.showErrorLoginUser() *> wizardJobs.visibilityUiActions.goToUser()
    case _: WizardMarketTokenRequestCancelledException =>
      wizardJobs.errorOperationMarketTokenCancelled()
    case _: WizardGoogleTokenRequestCancelledException =>
      wizardJobs.errorOperationGoogleTokenCancelled()
    case _ =>
      wizardJobs.wizardUiActions.showErrorConnectingGoogle() *> wizardJobs.visibilityUiActions
        .goToUser()
  }
}

object WizardActivity {

  def createWizardJobs(
      implicit activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]) = {
    val dom = new WizardDOM(activityContextWrapper.getOriginal)
    val listener =
      activityContextWrapper.getOriginal.asInstanceOf[WizardUiListener]
    new WizardJobs(
      wizardUiActions = new WizardUiActions(dom, listener),
      visibilityUiActions = new VisibilityUiActions(dom, listener))
  }

  def createNewConfigurationJobs(
      implicit activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]) = {
    val dom = new WizardDOM(activityContextWrapper.getOriginal)
    val listener =
      activityContextWrapper.getOriginal.asInstanceOf[WizardUiListener]
    new NewConfigurationJobs(
      wizardUiActions = new WizardUiActions(dom, listener),
      newConfigurationActions = new NewConfigurationUiActions(dom, listener),
      visibilityUiActions = new VisibilityUiActions(dom, listener))
  }

}
