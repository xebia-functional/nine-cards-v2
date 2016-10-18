package cards.nine.commons.test.data

import cards.nine.commons.test.data.ApiV1Values._
import cards.nine.commons.test.data.UserValues._
import cards.nine.models._

trait ApiV1TestData {

  val requestConfigV1 = RequestConfigV1(
    deviceId = deviceId,
    token = sessionToken,
    marketToken = Some(marketToken))

  def device(num: Int = 0) = Device(
    name = deviceName + num,
    deviceId = deviceId + num,
    secretToken = marketToken,
    permissions = permissions)

  val device: Device = device(0)
  val seqDevice: Seq[Device] = Seq(device(0), device(1), device(2))

  val loginResponseV1 = LoginResponseV1(
    userId = Option(userId.toString),
    sessionToken = Option(sessionToken),
    email = Option(email),
    devices = seqDevice)

  def userV1CollectionItem(num: Int = 0) = UserV1CollectionItem(
    itemType = itemType,
    title = title + num,
    intent = apiV1Intent,
    categories = Option(Seq(apiV1CollectionCategory, apiV1CollectionAnotherCategory)))

  val seqUserV1CollectionItem: Seq[UserV1CollectionItem] = Seq(userV1CollectionItem(0), userV1CollectionItem(1), userV1CollectionItem(2))

  def userV1Collection(num: Int = 0) = UserV1Collection(
    name = apiV1CollectionName,
    originalSharedCollectionId = Option(apiV1OriginalSharedCollectionId),
    sharedCollectionId = Option(apiV1SharedCollectionId),
    sharedCollectionSubscribed = Option(apiV1SharedCollectionSubscribed),
    items = seqUserV1CollectionItem,
    collectionType = apiV1CollectionType,
    constrains = constrains,
    wifi = wifi,
    occurrence = occurrence,
    icon = apiV1CollectionIcon,
    category = Option(apiV1CollectionCategory))

  val seqUserV1Collection: Seq[UserV1Collection] = Seq(userV1Collection(0), userV1Collection(1), userV1Collection(2))

  def userV1Device(num: Int = 0) = UserV1Device(
    deviceId = deviceIdPrefix + num,
    deviceName = deviceName + num,
    collections = seqUserV1Collection)

  val seqUserV1Device: Seq[UserV1Device] = Seq(userV1Device(0), userV1Device(1), userV1Device(2))

  def userV1(num: Int = 0) = UserV1(
    _id = userV1Id,
    email = email,
    plusProfile = UserV1PlusProfile(
      displayName = displayName,
      profileImage = UserV1ProfileImage(
        imageType = 0,
        imageUrl = imageUrl)),
    devices = seqUserV1Device,
    status = UserV1StatusInfo(
      products = products,
      friendsReferred = 0,
      themesShared = 0,
      collectionsShared = 0,
      customCollections = 0,
      earlyAdopter = false,
      communityMember = true,
      joinedThrough = None,
      tester = false))

  val userV1: UserV1 = userV1(0)
  val seqUserV1: Seq[UserV1] = Seq(userV1(0), userV1(1), userV1(2))

}
