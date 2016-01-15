package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{OperationCanceledException, Account, AccountManager}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserCloudDevices
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.{ImplicitsCloudStorageProcessExceptions, CloudStorageProcess, CloudStorageProcessException}
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageCollectionItem, CloudStorageCollection, CloudStorageDevice, CloudStorageResource}
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollectionItem, UserCollection, UserDevice, UserInfo}
import macroid.ActivityContextWrapper
import rapture.core._

import scala.reflect.ClassTag
import scalaz.{-\/, \/-, \/}
import scalaz.concurrent.Task

trait WizardTasks
  extends ImplicitsCloudStorageProcessExceptions {

  self: GoogleApiClientProvider =>

  def signInUser(username: String, device: Device)
    (implicit context: ContextSupport, di: Injector): ServiceDef2[UserCloudDevices, UserException with CloudStorageProcessException with UserConfigException] = {
      val cloudStorageProcess = di.createCloudStorageProcess(this, username)
      for {
        response <- di.userProcess.signIn(username, device)
        cloudDevices <- cloudStorageProcess.getCloudStorageDevices()
        userCloudDevices <- verifyAndUpdate(cloudStorageProcess, username, cloudDevices)
      } yield userCloudDevices
    }

  private[this] def verifyAndUpdate(
    cloudStorageProcess: CloudStorageProcess,
    name: String,
    cloudDevices: Seq[CloudStorageResource])
    (implicit context: ContextSupport, di: Injector): ServiceDef2[UserCloudDevices, UserConfigException with CloudStorageProcessException] = {
    if (cloudDevices.isEmpty) {
      for {
        userInfo <- di.userConfigProcess.getUserInfo
        cloudStorageDevices = userInfo.devices map toCloudStorageDevice
        _ <- storeOnCloud(cloudStorageProcess, cloudStorageDevices)
      } yield UserCloudDevices(userInfo.name, cloudStorageDevices)
    } else {
      for {
        devices <- loadFromCloud(cloudStorageProcess, cloudDevices)
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

  def getAuthToken(accountManager: AccountManager, account: Account, scopes: String)(implicit context: ActivityContextWrapper): ServiceDef2[String, AuthTokenException with AuthTokenOperationCancelledException] = Service {
    Task {
      \/.fromTryCatchNonFatal{
        val result = accountManager.getAuthToken(account, scopes, javaNull, context.original.get.get, javaNull, javaNull).getResult
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
      intent = userCollectionItem.intent,
      categories = userCollectionItem.categories getOrElse Seq.empty)
}
