package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{Account, AccountManager, OperationCanceledException}
import android.content.{Intent, Context}
import android.os.Build
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.Conversions._
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageDevice, CloudStorageDeviceSummary}
import com.fortysevendeg.ninecardslauncher.process.cloud.{ImplicitsCloudStorageProcessExceptions, CloudStorageProcess, CloudStorageProcessException}
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper
import rapture.core.{Answer, Errata, Result}

import scala.reflect.ClassTag
import scalaz.concurrent.Task
import scalaz.{-\/, \/, \/-}

class WizardPresenter(actions: WizardActions)(implicit contextWrapper: ActivityContextWrapper, contextSupport: ContextSupport)
  extends ImplicitsCloudStorageProcessExceptions {

  implicit lazy val di = new Injector

  private[this] val accountType = "com.google"

  private[this] lazy val accountManager: AccountManager = AccountManager.get(contextWrapper.bestAvailable)

  private[this] lazy val accounts: Seq[Account] = accountManager.getAccountsByType(accountType).toSeq

  private[this] val googleKeyPreferences = "__google_auth__"

  private[this] val googleKeyToken = "__google_token__"

  lazy val preferences = contextWrapper.bestAvailable.getSharedPreferences(googleKeyPreferences, Context.MODE_PRIVATE)

  private[this] def getToken: Option[String] = Option(preferences.getString(googleKeyToken, javaNull))

  private[this] def setToken(token: String) = preferences.edit.putString(googleKeyToken, token).apply()

  protected def getAccount(username: String): Option[Account] = accounts find (_.name == username)

  def getAccounts: Seq[Account] = accounts

  def loadUser(username: String): Unit =
    getAccount(username) match {
      case Some(account) => actions.onResultLoadUser(account).run
      case _ => actions.onExceptionLoadUser().run
    }

  def loadAccount(account: Account, client: GoogleApiClient): Unit = {
    invalidateToken()
    Task.fork(requestUserPermissions(account, client).run).resolveAsyncUi(
      onResult = actions.onResultLoadAccount,
      onException = actions.onExceptionLoadAccount)
  }

  def loadDevices(client: GoogleApiClient,
    username: String,
    userPermissions: UserPermissions): Unit = {
    invalidateToken()
    Task.fork(loadCloudDevices(client, username, userPermissions).run).resolveAsyncUi(
      onResult = actions.onResultLoadDevices,
      onException = actions.onExceptionLoadDevices)
  }

  def storeCurrentDevice(client: GoogleApiClient, username: String): Unit = {
    Task.fork(storeDevice(client, username).run).resolveAsyncUi(
      onResult = actions.onResultStoreCurrentDevice,
      onException = actions.onExceptionStoreCurrentDevice)
  }

  def startService(maybeKey: Option[String]): Unit = {
    val intent = new Intent(contextWrapper.getOriginal, classOf[CreateCollectionService])
    maybeKey map (key => intent.putExtra(CreateCollectionService.keyDevice, key))
    contextWrapper.getOriginal.startService(intent)
  }

  def goToLauncher(): Unit = actions.onResultWizard().run

  private[this] def requestUserPermissions(
    account: Account,
    client: GoogleApiClient): ServiceDef2[UserPermissions, AuthTokenException with AuthTokenOperationCancelledException] = {
    val oauthScopes = "androidmarket" // TODO - This should be removed when we switch off the server v1
    val driveScope = resGetString(R.string.oauth_scopes)
    for {
      token <- getAuthToken(accountManager, account, oauthScopes)
      _ = setToken(token)
      token2 <- getAuthToken(accountManager, account, driveScope)
    } yield UserPermissions(token, Seq(oauthScopes))
  }

  private[this] def loadCloudDevices(
    client: GoogleApiClient,
    username: String,
    userPermissions: UserPermissions): ServiceDef2[UserCloudDevices, UserException with UserConfigException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client, username)
    for {
      response <- di.userProcess.signIn(username, Build.MODEL, userPermissions.token, userPermissions.oauthScopes)
      cloudStorageResources <- cloudStorageProcess.getCloudStorageDevices
      userCloudDevices <- verifyAndUpdate(cloudStorageProcess, username, cloudStorageResources)
    } yield userCloudDevices

  }

  private[this] def storeDevice(
    client: GoogleApiClient,
    username: String): ServiceDef2[Unit, CollectionException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client, username)
    for {
      collections <- di.collectionProcess.getCollections
      _ <- cloudStorageProcess.createOrUpdateActualCloudStorageDevice(collections map toCloudStorageCollection)
    } yield ()
  }

  private[this] def invalidateToken() = {
    getToken foreach (accountManager.invalidateAuthToken(accountType, _))
    setToken(javaNull)
  }

  private[this] def verifyAndUpdate(
    cloudStorageProcess: CloudStorageProcess,
    name: String,
    cloudStorageResources: Seq[CloudStorageDeviceSummary]): ServiceDef2[UserCloudDevices, UserConfigException with CloudStorageProcessException] = {
    if (cloudStorageResources.isEmpty) {
      for {
        userInfo <- di.userConfigProcess.getUserInfo
        cloudStorageDevices = userInfo.devices map toCloudStorageDevice
        _ <- storeOnCloud(cloudStorageProcess, cloudStorageDevices)
      } yield UserCloudDevices(userInfo.name, cloudStorageDevices)
    } else {
      for {
        devices <- loadFromCloud(cloudStorageProcess, cloudStorageResources)
        _ <- fakeUserConfigException
      } yield UserCloudDevices(name, devices)
    }
  }

  private[this] def storeOnCloud(cloudStorageProcess: CloudStorageProcess, cloudStorageDevices: Seq[CloudStorageDevice]) = Service {
    val tasks = cloudStorageDevices map (d => cloudStorageProcess.createOrUpdateCloudStorageDevice(d).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CloudStorageProcessException](c.collect { case Answer(r) => r}))
  }

  private[this] def loadFromCloud(cloudStorageProcess: CloudStorageProcess, cloudStorageResources: Seq[CloudStorageDeviceSummary]) = Service {
    val tasks = cloudStorageResources map (r => cloudStorageProcess.getCloudStorageDevice(r.resourceId).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CloudStorageProcessException](c.collect { case Answer(r) => r}))
  }

  private[this] def fakeUserConfigException: ServiceDef2[Unit, UserConfigException] = Service(Task(Answer()))

  private[this] def getAuthToken(
    accountManager: AccountManager,
    account: Account,
    scopes: String): ServiceDef2[String, AuthTokenException with AuthTokenOperationCancelledException] = Service {
    Task {
      \/.fromTryCatchNonFatal{
        val result = accountManager.getAuthToken(account, scopes, javaNull, contextWrapper.getOriginal, javaNull, javaNull).getResult
        result.getString(AccountManager.KEY_AUTHTOKEN)
      } match {
        case \/-(x) => Result.answer(x)
        case -\/(e: OperationCanceledException) => Errata(Seq((
          implicitly[ClassTag[AuthTokenOperationCancelledException]],
          (e.getMessage, AuthTokenOperationCancelledException(e.getMessage, Some(e))))))
        case -\/(e) => Errata(Seq((
          implicitly[ClassTag[AuthTokenException]],
          (e.getMessage, AuthTokenException(e.getMessage, Some(e))))))
      }
    }
  }

}
