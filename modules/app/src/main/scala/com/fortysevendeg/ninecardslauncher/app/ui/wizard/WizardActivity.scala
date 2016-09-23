package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ActivityUiContext, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WizardState._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageClientListener
import com.fortysevendeg.ninecardslauncher.process.social.{SocialProfileClientListener, SocialProfileProcessException}
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userv1.UserV1Exception
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

  lazy val actions = new WizardUiActions(self)

  lazy val jobs = new WizardJobs(actions)

  override val actionsFilters: Seq[String] = WizardActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (WizardActionFilter(action), data) match {
    case (WizardStateActionFilter, Some(`stateSuccess`)) =>
      jobs.serviceFinished().resolveAsync()
    case (WizardStateActionFilter, Some(`stateCloudIdNotSend`)) =>
      jobs.serviceCloudIdNotSentError().resolveAsync()
    case (WizardStateActionFilter, Some(`stateUserCloudIdPresent`)) =>
      jobs.serviceCloudIdAlreadySetError().resolveAsync()
    case (WizardStateActionFilter, Some(`stateFailure`)) =>
      jobs.serviceUnknownError().resolveAsync()
    case (WizardAnswerActionFilter, Some(`stateCreatingCollections`)) =>
      jobs.serviceCreatingCollections().resolveAsync()
    case _ =>
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.wizard_activity)
    jobs.initialize().resolveAsync()
  }

  override def onResume(): Unit = {
    super.onResume()
    registerDispatchers
    self ? WizardAskActionFilter.action
  }

  override def onPause(): Unit = {
    super.onPause()
    unregisterDispatcher
  }

  override def onStop(): Unit = {
    jobs.stop().resolveAsync()
    super.onStop()
  }

  override def onBackPressed(): Unit = {}

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit =
    jobs.activityResult(requestCode, resultCode, data).resolveAsyncServiceOr(onException)

  override def onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array[String],
    grantResults: Array[Int]): Unit =
    jobs.requestPermissionsResult(requestCode, permissions, grantResults).resolveAsyncServiceOr(onException)

  override def onClickAcceptTermsButton(termsAccepted: Boolean): Unit =
    jobs.connectAccount(termsAccepted).resolveAsync()

  override def onClickSelectDeviceButton(maybeCloudId: Option[String]): Unit =
    jobs.deviceSelected(maybeCloudId).resolveAsyncServiceOr(_ => actions.goToUser())

  override def onClickFinishWizardButton(): Unit =
    jobs.finishWizard().resolveAsync()

  override def onPlusConnectionSuspended(cause: Int): Unit = {}

  override def onPlusConnected(): Unit =
    jobs.plusConnected().resolveAsyncServiceOr(onException)

  override def onPlusConnectionFailed(connectionResult: ConnectionResult): Unit =
    jobs.plusConnectionFailed(connectionResult).resolveAsync()

  override def onDriveConnectionSuspended(cause: Int): Unit = {}

  override def onDriveConnected(): Unit =
    jobs.driveConnected().resolveAsyncServiceOr(onException)

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    jobs.driveConnectionFailed(connectionResult).resolveAsync()

  override def onClickOkMarketPermissionDialog(): Unit =
    jobs.requestAndroidMarketPermission().resolveAsyncServiceOr(onException)

  override def onClickCancelMarketPermissionDialog(): Unit =
    actions.goToUser().resolveAsync()

  override def onClickOkGooglePermissionDialog(): Unit =
    jobs.requestGooglePermission().resolveAsyncServiceOr(onException)

  override def onClickCancelGooglePermissionDialog(): Unit =
    actions.goToUser().resolveAsync()

  override def onClickOkSelectAccountsDialog(): Unit =
    jobs.connectAccount(true).resolveAsync()

  override def onClickCancelSelectAccountsDialog(): Unit = {}

  override def onClickOkContactsPermissionDialog(): Unit =
    jobs.requestContactsPermission().resolveAsync()

  override def onClickCancelContactsPermissionDialog(): Unit =
    jobs.contactsPermissionDenied().resolveAsync()

  private[this] def onException[E >: Throwable]: (E) => TaskService[Unit] = {
    case ex: SocialProfileProcessException if ex.recoverable => jobs.googleSignIn()
    case _: UserException => actions.showErrorLoginUser()
    case _: UserV1Exception => actions.showErrorLoginUser()
    case _: WizardMarketTokenRequestCancelledException => jobs.errorOperationMarketTokenCancelled()
    case _: WizardGoogleTokenRequestCancelledException => jobs.errorOperationGoogleTokenCancelled()
    case _ => actions.showErrorConnectingGoogle()
  }
}