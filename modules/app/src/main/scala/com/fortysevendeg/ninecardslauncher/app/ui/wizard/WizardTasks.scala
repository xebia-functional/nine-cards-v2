package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{OperationCanceledException, Account, AccountManager}
import android.os.Build
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserPermissions, UserCloudDevices}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud._
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageDevice, CloudStorageDeviceSummary}
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import rapture.core._

import scala.reflect.ClassTag
import scalaz.{-\/, \/-, \/}
import scalaz.concurrent.Task

trait WizardTasks
  extends ImplicitsCloudStorageProcessExceptions {

  self: WizardActivity =>

  import Conversions._

  def requestUserPermissions(
    accountManager: AccountManager,
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

  def loadUserDevices(
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

  def storeActualDevice(
    client: GoogleApiClient,
    username: String): ServiceDef2[Unit, CollectionException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client, username)
    for {
      collections <- di.collectionProcess.getCollections
      _ <- cloudStorageProcess.createOrUpdateActualCloudStorageDevice(collections map toCloudStorageCollection)
    } yield ()
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
        val result = accountManager.getAuthToken(account, scopes, javaNull, this, javaNull, javaNull).getResult
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
