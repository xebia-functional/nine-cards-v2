package com.fortysevendeg.ninecardslauncher.process.userconfig

import com.fortysevendeg.ninecardslauncher.process.commons.types.{CollectionType, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.userconfig.models._
import com.fortysevendeg.ninecardslauncher.services.api.models.{UserConfig, UserConfigCollection, UserConfigCollectionItem, UserConfigDevice}

trait UserConfigConversions {

  def toUserInfo(androidId: String, userConfig: UserConfig): UserInfo = UserInfo(
    email = userConfig.email,
    name = userConfig.plusProfile.displayName,
    imageUrl = userConfig.plusProfile.profileImage.imageUrl,
    androidId = androidId,
    devices = userConfig.devices map toUserDevice)

  def toUserDevice(userConfigDevice: UserConfigDevice): UserDevice = UserDevice(
    deviceId = userConfigDevice.deviceId,
    deviceName = userConfigDevice.deviceName,
    collections = userConfigDevice.collections map toUserCollection)

  def toUserCollection(userConfigCollection: UserConfigCollection): UserCollection = UserCollection(
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

  def toUserCollectionItem(item: UserConfigCollectionItem): UserCollectionItem = UserCollectionItem(
    itemType = item.itemType,
    title = item.title,
    intent = item.metadata.toString(),
    categories = item.categories map (_ map (NineCardCategory(_))))

}
