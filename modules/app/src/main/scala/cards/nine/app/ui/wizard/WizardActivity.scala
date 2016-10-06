package cards.nine.app.ui.wizard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import cards.nine.app.ui.commons.WizardState._
import cards.nine.app.ui.commons.action_filters._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{ActivityUiContext, UiContext}
import cards.nine.app.ui.wizard.jobs._
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.NineCardsMoment
import cards.nine.process.cloud.CloudStorageClientListener
import cards.nine.process.collection.models.PackagesByCategory
import cards.nine.process.social.{SocialProfileClientListener, SocialProfileProcessException}
import cards.nine.process.user.UserException
import cards.nine.process.userv1.UserV1Exception
import cats.implicits._
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import com.google.android.gms.common.ConnectionResult
import macroid.Contexts

class WizardActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with BroadcastDispatcher
  with SocialProfileClientListener
  with CloudStorageClientListener
  with WizardDOM
  with WizardUiListener { self =>

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(self)

  lazy val wizardUiActions = new WizardUiActions(self)

  lazy val visibilityUiActions = new VisibilityUiActions(self)

  lazy val wizardJobs = new WizardJobs(wizardUiActions, visibilityUiActions)

  lazy val newConfigurationActions = new NewConfigurationUiActions(self)

  lazy val newConfigurationJobs = new NewConfigurationJobs(newConfigurationActions, visibilityUiActions)

  override val actionsFilters: Seq[String] = WizardActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (WizardActionFilter(action), data) match {
    case (WizardStateActionFilter, Some(`stateSuccess`)) =>
      wizardJobs.serviceFinished().resolveAsync()
    case (WizardStateActionFilter, Some(`stateCloudIdNotSend`)) =>
      wizardJobs.serviceCloudIdNotSentError().resolveAsync()
    case (WizardStateActionFilter, Some(`stateUserCloudIdPresent`)) =>
      wizardJobs.serviceCloudIdAlreadySetError().resolveAsync()
    case (WizardStateActionFilter, Some(`stateUserEmailNotPresent`)) =>
      wizardJobs.serviceUserEmailNotFoundError().resolveAsync()
    case (WizardStateActionFilter, Some(`stateEmptyDevice`)) =>
      wizardJobs.serviceEmptyDeviceError().resolveAsync()
    case (WizardStateActionFilter, Some(`stateFailure`)) =>
      wizardJobs.serviceUnknownError().resolveAsync()
    case (WizardAnswerActionFilter, Some(`stateCreatingCollections`)) =>
      wizardJobs.serviceCreatingCollections().resolveAsync()
    case _ =>
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.wizard_activity)
    wizardJobs.initialize().resolveAsync()
  }

  override def onResume(): Unit = {
    super.onResume()
    registerDispatchers()
    wizardJobs.sendAsk().resolveAsync()
  }

  override def onPause(): Unit = {
    super.onPause()
    unregisterDispatcher()
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

  override def onClickAcceptTermsButton(termsAccepted: Boolean): Unit =
    wizardJobs.connectAccount(termsAccepted).resolveAsync()

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
    wizardJobs.connectAccount(true).resolveAsync()

  override def onClickCancelSelectAccountsDialog(): Unit = {}

  override def onClickOkPermissionsDialog(): Unit =
    wizardJobs.requestPermissions().resolveAsync()

  override def onClickCancelPermissionsDialog(): Unit =
    wizardJobs.permissionDialogCancelled().resolveAsync()

  override def onStartNewConfiguration(): Unit =
    newConfigurationActions.loadFirstStep().resolveAsync()

  override def onLoadBetterCollections(): Unit =
    newConfigurationJobs.loadBetterCollections().resolveAsync()

  override def onSaveCollections(collections: Seq[PackagesByCategory], best9Apps: Boolean): Unit =
    newConfigurationJobs.saveCollections(collections, best9Apps).resolveAsyncServiceOr(_ =>
      wizardUiActions.showErrorGeneral() *> newConfigurationJobs.loadBetterCollections())

  override def onLoadWifiByMoment(): Unit =
    newConfigurationJobs.loadMomentWithWifi().resolveAsync()

  override def onSaveMomentsWithWifi(infoMoment: Seq[(NineCardsMoment, Option[String])]): Unit =
    newConfigurationJobs.saveMomentsWithWifi(infoMoment).resolveAsyncServiceOr(_ =>
      wizardUiActions.showErrorGeneral() *> newConfigurationJobs.loadMomentWithWifi())

  override def onSaveMoments(moments: Seq[NineCardsMoment]): Unit =
    newConfigurationJobs.saveMoments(moments).resolveAsyncServiceOr(_ => newConfigurationActions.loadSixthStep())

  private[this] def onException[E >: Throwable]: (E) => TaskService[Unit] = {
    case ex: SocialProfileProcessException if ex.recoverable => wizardJobs.googleSignIn()
    case _: UserException => wizardUiActions.showErrorLoginUser() *> visibilityUiActions.goToWizard()
    case _: UserV1Exception => wizardUiActions.showErrorLoginUser() *> visibilityUiActions.goToWizard()
    case _: WizardMarketTokenRequestCancelledException => wizardJobs.errorOperationMarketTokenCancelled()
    case _: WizardGoogleTokenRequestCancelledException => wizardJobs.errorOperationGoogleTokenCancelled()
    case _ => wizardUiActions.showErrorConnectingGoogle() *> visibilityUiActions.goToWizard()
  }
}