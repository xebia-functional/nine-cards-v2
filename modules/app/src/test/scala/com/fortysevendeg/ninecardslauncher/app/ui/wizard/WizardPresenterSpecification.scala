package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{AccountManager, AccountManagerFuture}
import android.app.Activity
import android.content.res.Resources
import android.content.{Intent, SharedPreferences}
import android.os.Bundle
import android.support.v4.app.{FragmentManager, FragmentTransaction}
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.Statuses.WizardPresenterStatuses
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcess
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionProcess
import com.fortysevendeg.ninecardslauncher.process.moment.MomentProcess
import com.fortysevendeg.ninecardslauncher.process.userv1.UserV1Process
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, ContextWrapper, Ui}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.duration._

trait WizardPresenterSpecification
  extends Specification
  with Mockito
  with WizardPresenterData {

  trait WizardPresenterScope
    extends Scope {

    implicit val mockContextSupport = mock[ActivityContextSupport]

    implicit val mockContextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockCloudStorageProcess = mock[CloudStorageProcess]

    val mockCollectionProcess = mock[CollectionProcess]

    val mockMomentProcess = mock[MomentProcess]

    val mockUserConfigProcess = mock[UserV1Process]

    val mockGoogleApiClient = mock[GoogleApiClient]

    val mockAccountManager = mock[AccountManager]

    val mockSharedPreferences = mock[SharedPreferences]

    val mockAccountManagerFuture = mock[AccountManagerFuture[Bundle]]

    val mockBundle = mock[Bundle]

    val mockContextActivity = mock[AppCompatActivity]

    val mockFragmentManager = mock[FragmentManager]

    val mockFragmentTransaction = mock[FragmentTransaction]

    val mockResources = mock[Resources]

    val mockActions = mock[WizardUiActions]

    val mockIntent = mock[Intent]

    mockActions.initialize() returns Ui[Any](())
    mockActions.showLoading() returns Ui[Any](())
    mockActions.goToUser() returns Ui[Any](())
    mockActions.goToWizard() returns Ui[Any](())
    mockActions.showErrorConnectingGoogle() returns Ui[Any](())
    mockActions.showErrorLoginUser() returns Ui[Any](())
    mockActions.showErrorAcceptTerms() returns Ui[Any](())
    mockActions.showErrorSelectUser() returns Ui[Any](())
    mockActions.showDiveIn() returns Ui[Any](())

    val presenter = new WizardPresenter(mockActions) {

      override implicit def contextSupport(implicit ctx: ContextWrapper): ContextSupport = mockContextSupport

      override implicit def activityContextSupport(implicit ctx: ActivityContextWrapper): ActivityContextSupport = mockContextSupport

      override implicit lazy val di: Injector = mockInjector

      override def createGoogleDriveClient(account: String)(implicit contextWrapper: ContextWrapper): GoogleApiClient = mockGoogleApiClient

      override protected def createIntent[T](activity: Activity, targetClass: Class[T]): Intent = mockIntent
    }

  }

}

class WizardPresenterSpec
  extends WizardPresenterSpecification {

  "Initialize" should {

    "call to initialize in Actions with the accounts" in new WizardPresenterScope {

      presenter.initialize()

      there was after(1.seconds).one(mockActions).initialize()
    }
  }

  "Go to User" should {

    "call to Go to User in Actions" in new WizardPresenterScope {

      presenter.goToUser()

      there was after(1.seconds).one(mockActions).goToUser()
    }
  }

  "Go to Wizard" should {

    "call to Go to Wizard in Actions" in new WizardPresenterScope {

      presenter.goToWizard()

      there was after(1.seconds).one(mockActions).goToWizard()
    }
  }

  "Process Finished" should {

    "call to Show Dive In in Actions" in new WizardPresenterScope {

      presenter.processFinished()

      there was after(1.seconds).one(mockActions).showDiveIn()
    }
  }

  "Connect Account" should {

    "call to show error accept term in Actions when the terms are not accepted" in
      new WizardPresenterScope {
        presenter.connectAccount(termsAccept = false)
        there was after(1.seconds).one(mockActions).showErrorAcceptTerms()
      }

  }

  "Finish Wizard" should {

    "set the result and call finish in the activity" in
      new WizardPresenterScope {

        mockContextSupport.getActivity returns Some(mockContextActivity)

        presenter.finishWizard()

        there was after(1.seconds).one(mockContextActivity).setResult(Activity.RESULT_OK)
        there was after(1.seconds).one(mockContextActivity).finish()
      }

  }

  "Activity Result" should {

    "return true and call to connect in Google Api Client when pass `resolveGooglePlayConnection` and RESULT_OK" in
      new WizardPresenterScope {
        presenter.clientStatuses = WizardPresenterStatuses(driveApiClient = Some(mockGoogleApiClient))

        val result = presenter.activityResult(RequestCodes.resolveGooglePlayConnection, Activity.RESULT_OK, javaNull)

        result should beTrue

        there was after(1.seconds).one(mockGoogleApiClient).connect()
      }

    "return true and call show error connecting google when pass `resolveGooglePlayConnection` and a value distinct to RESULT_OK" in
      new WizardPresenterScope {
        val result = presenter.activityResult(RequestCodes.resolveGooglePlayConnection, Activity.RESULT_CANCELED, javaNull)

        result should beTrue

        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
      }

    "return true and call show error connecting google when pass a different request code" in
      new WizardPresenterScope {
        val result = presenter.activityResult(RequestCodes.goToProfile, Activity.RESULT_OK, javaNull)

        result should beFalse
      }

  }

  "Stop" should {

    "call to disconnect in the Google Api client" in
      new WizardPresenterScope {
        presenter.clientStatuses = WizardPresenterStatuses(driveApiClient = Some(mockGoogleApiClient))

        presenter.stop()

        there was after(1.seconds).one(mockGoogleApiClient).disconnect()
      }

  }

}
