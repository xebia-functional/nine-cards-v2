package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.fortysevendeg.ninecardslauncher.process.cloud.models._
import com.fortysevendeg.ninecardslauncher.process.commons.models._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, AppsCollectionType, CollectionType, Social}

trait WizardPresenterData {

  val accountName = "device@47deg.com"

  val nonExistingAccountName = "non-existing@47deg.com"

  val accountType = "com.google"

  val account = new Account(accountName, accountType)

  val accounts = Seq(account)

  val token = "fake-token"

  val permissions = Seq.empty

  val userPermission = UserPermissions(token, permissions)

  val nameDevice = "Nexus 47"

  val deviceId = "XXX-47"

  val androidMarketScopes = "androidmarket"

  val googleScopes = "fakeGoogleScope"

  val deviceName = nameDevice

  val intentKey = "intent-key"

  val cloudStorageDevice = CloudStorageDevice(
    deviceId = deviceId,
    deviceName = deviceName,
    documentVersion = 1,
    collections = Seq.empty,
    moments = None
  )

  val userCloudDevices = UserCloudDevices(
    name = nameDevice,
    devices = Seq(cloudStorageDevice)
  )

  val items = Seq(CloudStorageCollectionItem("APP", "App 1", "{\"intentExtras\":{},\"className\":\"\",\"packageName\":\"\",\"categories\":[],\"action\":\"\",\"extras\":{},\"flags\":1,\"type\":\"\",\"dataString\":null}"))

  val cloudStorageMomentCollection = CloudStorageMoment(
    timeslot = Seq(CloudStorageMomentTimeSlot(from = "from-1", to = "to-1", days = 0 to 4)),
    wifi = Seq("wifi-1"),
    headphones = true)

  val cloudStorageMoment = CloudStorageMoment(
    timeslot = Seq(CloudStorageMomentTimeSlot(from = "from-2", to = "to-2", days = 5 to 6)),
    wifi = Seq("wifi-2"),
    headphones = false)

  val cloudStorageCollection = CloudStorageCollection(
    name = "Collection 1",
    originalSharedCollectionId = Some("originalSharedCollectionId"),
    sharedCollectionId = Some("sharedCollectionId"),
    sharedCollectionSubscribed = Some(false),
    items = items,
    collectionType = AppsCollectionType,
    icon = "icon",
    category = Some(Social),
    moment = Some(cloudStorageMomentCollection))

  val cards = Seq(
    Card(
      id = 1,
      position = 0,
      micros = 0,
      term = "App 1",
      packageName = Some("package.name"),
      cardType = AppCardType,
      intent = NineCardIntent(NineCardIntentExtras()),
      imagePath = "imagePath",
      starRating = Some(3.5),
      numDownloads = Some("1000000"),
      notification = Some("notification")
    )
  )

  val collection = Collection(
    id = 1,
    position = 0,
    name = "Collection 1",
    collectionType = AppsCollectionType,
    icon = "icon",
    themedColorIndex = 0,
    appsCategory = Some(Social),
    constrains = Some("constrain"),
    originalSharedCollectionId = Some("originalSharedCollectionId"),
    sharedCollectionId = Some("sharedCollectionId"),
    sharedCollectionSubscribed = false,
    cards = cards)

  val momentCollection = Moment(
    collectionId = Some(1),
    timeslot = Seq(MomentTimeSlot(from = "from-1", to = "to-1", days = 0 to 4)),
    wifi = Seq("wifi-1"),
    headphone = true)

  val moment = Moment(
    collectionId = None,
    timeslot = Seq(MomentTimeSlot(from = "from-2", to = "to-2", days = 5 to 6)),
    wifi = Seq("wifi-2"),
    headphone = false)

  val moments = Seq(momentCollection, moment)

}
