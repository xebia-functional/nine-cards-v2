package cards.nine.app.ui.wizard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{ActivityUiContext, SynchronizeDeviceJobs, UiContext}
import cards.nine.app.ui.wizard.jobs._
import cards.nine.commons.services.TaskService._
import cards.nine.models.PackagesByCategory
import cards.nine.models.types.{HomeMorningMoment, NineCardsMoment, StudyMoment, WorkMoment}
import cards.nine.process.cloud.CloudStorageClientListener
import cards.nine.process.social.{SocialProfileClientListener, SocialProfileProcessException}
import cards.nine.process.user.UserException
import cards.nine.process.userv1.UserV1Exception
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import com.google.android.gms.common.ConnectionResult
import macroid.Contexts

class WizardActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with SocialProfileClientListener
  with CloudStorageClientListener
  with WizardDOM
  with WizardUiListener { self =>

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(self)

  lazy val wizardUiActions = new WizardUiActions(self)

  lazy val visibilityUiActions = new VisibilityUiActions(self)

  lazy val wizardJobs = new WizardJobs(wizardUiActions, visibilityUiActions)

  lazy val newConfigurationActions = new NewConfigurationUiActions(self)

  lazy val newConfigurationJobs = new NewConfigurationJobs(visibilityUiActions)

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
    wizardJobs.requestPermissionsResult(requestCode, permissions, grantResults).resolveAsyncServiceOr(onException)

  override def onClickAcceptTermsButton(): Unit =
    wizardJobs.connectAccount().resolveAsync()

  override def onClickSelectDeviceButton(maybeCloudId: Option[String]): Unit =
    wizardJobs.deviceSelected(maybeCloudId).resolveAsyncServiceOr(_ => visibilityUiActions.goToUser())

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
    visibilityUiActions.goToUser().resolveAsync()

  override def onClickOkGooglePermissionDialog(): Unit =
    wizardJobs.requestGooglePermission().resolveAsyncServiceOr(onException)

  override def onClickCancelGooglePermissionDialog(): Unit =
    visibilityUiActions.goToUser().resolveAsync()

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
      _ <- loadConfigurationJobs.loadConfiguration(client, cloudId)
      _ <- wizardUiActions.showDiveIn()
    } yield ()).resolveAsyncServiceOr(_ => wizardUiActions.showErrorGeneral() *> visibilityUiActions.goToUser())

  override def onStartNewConfiguration(): Unit =
    newConfigurationActions.loadFirstStep().resolveAsync()

  private[this] def loadBetterCollections(hidePrevious: Boolean): TaskService[Unit] = for {
    collections <- newConfigurationJobs.loadBetterCollections(hidePrevious)
    _ <- visibilityUiActions.showNewConfiguration()
    _ <- newConfigurationActions.loadSecondStep(collections)
  } yield ()

  override def onLoadBetterCollections(): Unit = loadBetterCollections(hidePrevious = true).resolveAsync()

  override def onSaveCollections(collections: Seq[PackagesByCategory]): Unit =
    (for {
      _ <- newConfigurationJobs.saveCollections(collections)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- newConfigurationActions.loadThirdStep()
    } yield ()).resolveAsyncServiceOr[Throwable] {
      case ex: WizardNoCollectionsSelectedException =>
        wizardUiActions.showNoCollectionsSelectedMessage() *> loadBetterCollections(hidePrevious = false)
      case _ =>
        wizardUiActions.showErrorGeneral() *> loadBetterCollections(hidePrevious = false)
    }

  private[this] def loadMomentWithWifi(hidePrevious: Boolean): TaskService[Unit] =
    for {
      wifis <- newConfigurationJobs.loadMomentWithWifi(hidePrevious)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- newConfigurationActions.loadFourthStep(wifis, Seq(
        (HomeMorningMoment, true),
        (WorkMoment, false),
        (StudyMoment, false)))
    } yield ()

  override def onLoadMomentWithWifi(): Unit = loadMomentWithWifi(hidePrevious = true).resolveAsync()

  override def onSaveMomentsWithWifi(infoMoment: Seq[(NineCardsMoment, Option[String])]): Unit =
    (for {
      _ <- newConfigurationJobs.saveMomentsWithWifi(infoMoment)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- newConfigurationActions.loadFifthStep()
    } yield ()).resolveAsyncServiceOr(_ => wizardUiActions.showErrorGeneral() *> loadMomentWithWifi(hidePrevious = false))

  override def onSaveMoments(moments: Seq[NineCardsMoment]): Unit = {
    (for {
      _ <- newConfigurationJobs.saveMoments(moments)
      client <- wizardJobs.googleDriveClient
      _ <- synchronizeDeviceJobs.synchronizeDevice(client)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- newConfigurationActions.loadSixthStep()
    } yield ()).resolveAsyncServiceOr(_ => newConfigurationActions.loadSixthStep())
  }

  private[this] def onException[E >: Throwable]: (E) => TaskService[Unit] = {
    case ex: SocialProfileProcessException if ex.recoverable => wizardJobs.googleSignIn()
    case _: UserException => wizardUiActions.showErrorLoginUser() *> visibilityUiActions.goToUser()
    case _: UserV1Exception => wizardUiActions.showErrorLoginUser() *> visibilityUiActions.goToUser()
    case _: WizardMarketTokenRequestCancelledException => wizardJobs.errorOperationMarketTokenCancelled()
    case _: WizardGoogleTokenRequestCancelledException => wizardJobs.errorOperationGoogleTokenCancelled()
    case _ => wizardUiActions.showErrorConnectingGoogle() *> visibilityUiActions.goToUser()
  }
}