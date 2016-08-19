package com.fortysevendeg.ninecardslauncher.process.userv1

import com.fortysevendeg.ninecardslauncher.process.commons.types.{CollectionType, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.userv1.models.{Device, UserV1Collection, UserV1CollectionItem, UserV1Device, UserV1Info}
import com.fortysevendeg.ninecardslauncher.services.api.models.{LoginV1Device => ServiceLoginV1Device, UserV1 => ServiceUserV1, UserV1Device => ServiceUserV1Device, UserV1Collection => ServiceUserV1Collection, UserV1CollectionItem => ServiceUserV1CollectionItem}

trait UserV1Conversions {

  def toGoogleDevice(device: Device): ServiceLoginV1Device =
    ServiceLoginV1Device(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toUserInfo(androidId: String, userConfig: ServiceUserV1): UserV1Info = UserV1Info(
    email = userConfig.email,
    name = userConfig.plusProfile.displayName,
    imageUrl = userConfig.plusProfile.profileImage.imageUrl,
    androidId = androidId,
    devices = userConfig.devices map toUserDevice)

  def toUserDevice(userConfigDevice: ServiceUserV1Device): UserV1Device = UserV1Device(
    deviceId = userConfigDevice.deviceId,
    deviceName = userConfigDevice.deviceName,
    collections = userConfigDevice.collections map toUserCollection)

  def toUserCollection(userConfigCollection: ServiceUserV1Collection): UserV1Collection = UserV1Collection(
    name = userConfigCollection.name,
    originalSharedCollectionId = userConfigCollection.originalSharedCollectionId,
    sharedCollectionId = userConfigCollection.sharedCollectionId,
    sharedCollectionSubscribed = userConfigCollection.sharedCollectionSubscribed,
    items = userConfigCollection.items map toUserCollectionItem,
    collectionType = CollectionType(userConfigCollection.collectionType),
    constrains = userConfigCollection.constrains,
    wifi = userConfigCollection.wifi,
    occurrence = userConfigCollection.occurrence,
    icon = userConfigCollection.icon,
    category = userConfigCollection.category map (NineCardCategory(_)))

  def toUserCollectionItem(item: ServiceUserV1CollectionItem): UserV1CollectionItem = UserV1CollectionItem(
    itemType = item.itemType,
    title = item.title,
    intent = item.metadata.toString(),
    categories = item.categories map (_ map (NineCardCategory(_))))

}
