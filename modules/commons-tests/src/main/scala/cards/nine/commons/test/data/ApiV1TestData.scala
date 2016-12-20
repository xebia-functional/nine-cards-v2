package cards.nine.commons.test.data

import cards.nine.commons.test.data.ApiV1Values._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.SharedCollectionValues._
import cards.nine.commons.test.data.UserValues._
import cards.nine.models._

trait ApiV1TestData {

  def requestConfigV1(num: Int = 0) =
    RequestConfigV1(
      deviceId = userDeviceId + num,
      token = sessionToken,
      marketToken = Option(marketToken))

  implicit val requestConfigV1: RequestConfigV1 = requestConfigV1(0)

  def device(num: Int = 0) =
    Device(
      name = userDeviceName + num,
      deviceId = userDeviceId + num,
      secretToken = marketToken,
      permissions = permissions)

  val device: Device         = device(0)
  val seqDevice: Seq[Device] = Seq(device(0), device(1), device(2))

  val loginResponseV1 = LoginResponseV1(
    userId = Option(userId.toString),
    sessionToken = Option(sessionToken),
    email = Option(email),
    devices = seqDevice)

  def userV1CollectionItem(num: Int = 0) =
    UserV1CollectionItem(
      itemType = itemType,
      title = title + num,
      intent = intent,
      categories = Option(Seq(category, anotherCategory)))

  val seqUserV1CollectionItem: Seq[UserV1CollectionItem] =
    Seq(userV1CollectionItem(0), userV1CollectionItem(1), userV1CollectionItem(2))

  def userV1Collection(num: Int = 0) =
    UserV1Collection(
      name = collectionName,
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
      items = seqUserV1CollectionItem,
      collectionType = collectionType,
      constrains = constrains,
      wifi = wifiSeq,
      occurrence = occurrence,
      icon = apiV1CollectionIcon,
      category = Option(category))

  val seqUserV1Collection: Seq[UserV1Collection] =
    Seq(userV1Collection(0), userV1Collection(1), userV1Collection(2))

  def userV1Device(num: Int = 0) =
    UserV1Device(
      deviceId = deviceIdPrefix + num,
      deviceName = userDeviceName + num,
      collections = seqUserV1Collection)

  val seqUserV1Device: Seq[UserV1Device] = Seq(userV1Device(0), userV1Device(1), userV1Device(2))

  def userV1(num: Int = 0) =
    UserV1(
      _id = userV1Id,
      email = email,
      plusProfile = UserV1PlusProfile(
        displayName = displayName,
        profileImage = UserV1ProfileImage(imageType = 0, imageUrl = imageUrl)),
      devices = seqUserV1Device,
      status = UserV1StatusInfo(
        products = products,
        friendsReferred = friendsReferred,
        themesShared = themesShared,
        collectionsShared = collectionsShared,
        customCollections = customCollections,
        earlyAdopter = earlyAdopter,
        communityMember = communityMember,
        joinedThrough = joinedThrough,
        tester = tester))

  val userV1: UserV1         = userV1(0)
  val seqUserV1: Seq[UserV1] = Seq(userV1(0), userV1(1), userV1(2))

}
