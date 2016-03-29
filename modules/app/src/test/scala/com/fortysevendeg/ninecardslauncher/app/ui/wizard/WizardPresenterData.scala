package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageCollectionItem, CloudStorageCollection, CloudStorageDevice}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{Social, AppsCollectionType, CollectionType}

trait WizardPresenterData {

  val accountName = "device@47deg.com"

  val accountType = "com.google"

  val account = new Account(accountName, accountType)

  val token = "fake-token"

  val permissions = Seq.empty

  val userPermission = UserPermissions(token, permissions)

  val nameDevice = "Nexus 47"

  val deviceId = "XXX-47"

  val deviceName = nameDevice

  val cloudStorageDevice = CloudStorageDevice(
    deviceId = deviceId,
    deviceName = deviceName,
    documentVersion = 1,
    collections = Seq.empty
  )

  val userCloudDevices = UserCloudDevices(
    name = nameDevice,
    devices = Seq(cloudStorageDevice)
  )

  val items = Seq(
    CloudStorageCollectionItem("APP", "App 1", "{}"),
    CloudStorageCollectionItem("APP", "App 2", "{}"),
    CloudStorageCollectionItem("APP", "App 3", "{}")
  )

  val cloudStorageCollection = CloudStorageCollection(
    name = "Collection 1",
    originalSharedCollectionId = None,
    sharedCollectionId = None,
    sharedCollectionSubscribed = None,
    items = items,
    collectionType = AppsCollectionType,
    icon = "icon",
    category = Some(Social)
  )

}
