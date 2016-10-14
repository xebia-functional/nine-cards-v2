package cards.nine.commons.test.data

import cards.nine.commons.test.data.UserValues._
import cards.nine.models.{UserData, UserProfile, User}

trait UserTestData {

  def createSeqUser(
    num: Int = 5,
    id: Int = uId,
    email: String = email,
    sessionToken: String = sessionToken,
    apiKey: String = apiKey,
    deviceToken: String = deviceToken,
    marketToken: String = marketToken,
    name: String = nameUser,
    avatar: String = avatar,
    cover: String = cover,
    deviceName: String = deviceName,
    deviceCloudId: String = deviceCloudId): Seq[User] = List.tabulate(num)(
    item => User(
      id = id + item,
      email = Option(email),
      sessionToken = Option(sessionToken),
      apiKey = Option(apiKey),
      deviceToken = Option(deviceToken),
      marketToken = Option(marketToken),
      deviceName = Option(deviceName),
      deviceCloudId = Option(deviceCloudId),
      userProfile = UserProfile(
        name = Option(name),
        avatar = Option(avatar),
        cover = Option(cover))))

  def createUserData(
    email: String = email,
    sessionToken: String = sessionToken,
    apiKey: String = apiKey,
    deviceToken: String = deviceToken,
    marketToken: String = marketToken,
    name: String = nameUser,
    avatar: String = avatar,
    cover: String = cover,
    deviceName: String = deviceName,
    deviceCloudId: String = deviceCloudId): UserData =
    UserData(
      email = Option(email),
      sessionToken = Option(sessionToken),
      apiKey = Option(apiKey),
      deviceToken = Option(deviceToken),
      marketToken = Option(marketToken),
      deviceName = Option(deviceName),
      deviceCloudId = Option(deviceCloudId),
      userProfile = UserProfile(
        name = Option(name),
        avatar = Option(avatar),
        cover = Option(cover)))

  val seqUser: Seq[User] = createSeqUser()
  val user: User = seqUser(0)
  val userData: UserData = createUserData()

}
