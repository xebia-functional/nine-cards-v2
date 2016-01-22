package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{OperationCanceledException, Account, AccountManager}
import android.os.Build
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserCloudDevices
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.{ImplicitsCloudStorageProcessExceptions, CloudStorageProcess, CloudStorageProcessException}
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageCollectionItem, CloudStorageCollection, CloudStorageDevice, CloudStorageResource}
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollectionItem, UserCollection, UserDevice}
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import rapture.core._

import scala.reflect.ClassTag
import scalaz.{-\/, \/-, \/}
import scalaz.concurrent.Task

trait WizardTasks
  extends ImplicitsWizardTasksExceptions
  with ImplicitsCloudStorageProcessExceptions {

  self: WizardActivity =>

  def loadDevices(
    accountManager: AccountManager,
    account: Account,
    client: GoogleApiClient): ServiceDef2[UserCloudDevices, AuthTokenException with AuthTokenOperationCancelledException with ServiceConnectionException] = {
    val oauthScopes = "androidmarket" // TODO - This should be removed when we switch off the server v1
    val driveScope = resGetString(R.string.oauth_scopes)
    for {
      token <- getAuthToken(accountManager, account, oauthScopes)
      _ = setToken(token)
      _ <- getAuthToken(accountManager, account, driveScope)
      device = Device(
        name = Build.MODEL,
        deviceId = androidId,
        secretToken = token,
        permissions = Seq(oauthScopes))
      userCloudDevices <- signInUser(client, account.name, device)
    } yield userCloudDevices

  }

  private[this] def signInUser(client: GoogleApiClient, username: String, device: Device): ServiceDef2[UserCloudDevices, ServiceConnectionException] = {
      val cloudStorageProcess = di.createCloudStorageProcess(client, username)
      (for {
        response <- di.userProcess.signIn(username, device)
        cloudcloudStorageResources <- cloudStorageProcess.getCloudStorageDevices()
        userCloudDevices <- verifyAndUpdate(cloudStorageProcess, username, cloudcloudStorageResources)
      } yield userCloudDevices).resolve[ServiceConnectionException]
    }

  private[this] def verifyAndUpdate(
    cloudStorageProcess: CloudStorageProcess,
    name: String,
    cloudStorageResources: Seq[CloudStorageResource]): ServiceDef2[UserCloudDevices, UserConfigException with CloudStorageProcessException] = {
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

  private[this] def loadFromCloud(cloudStorageProcess: CloudStorageProcess, cloudStorageResources: Seq[CloudStorageResource]) = Service {
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

  def toCloudStorageDevice(userDevice: UserDevice) =
    CloudStorageDevice(
      deviceId = userDevice.deviceId,
      deviceName = userDevice.deviceName,
      documentVersion = CloudStorageProcess.actualDocumentVersion,
      userDevice.collections map toCloudStorageCollection)

  def toCloudStorageCollection(userCollection: UserCollection) =
    CloudStorageCollection(
      name = userCollection.name,
      originalSharedCollectionId = userCollection.originalSharedCollectionId,
      sharedCollectionId = userCollection.sharedCollectionId,
      sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
      items = userCollection.items map toCloudStorageCollectionItem,
      collectionType = userCollection.collectionType,
      icon = userCollection.icon,
      category = userCollection.category)

  def toCloudStorageCollectionItem(userCollectionItem: UserCollectionItem) =
    CloudStorageCollectionItem(
      itemType = userCollectionItem.itemType,
      title = userCollectionItem.title,
      intent = userCollectionItem.intent)
}
