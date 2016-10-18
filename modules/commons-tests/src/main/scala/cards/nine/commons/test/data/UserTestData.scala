package cards.nine.commons.test.data

import cards.nine.commons.test.data.UserValues._
import cards.nine.models.{UserData, UserProfile, User}

trait UserTestData {

  def user(num: Int = 0) = User(
    id = userId + num,
    email = Option(email),
    sessionToken = Option(sessionToken),
    apiKey = Option(apiKey),
    deviceToken = Option(deviceToken),
    marketToken = Option(marketToken),
    deviceName = Option(deviceName),
    deviceCloudId = Option(deviceCloudId),
    userProfile = UserProfile(
      name = Option(userName),
      avatar = Option(avatar),
      cover = Option(cover)))

  val user: User = user(0)
  val anotherUser: User = user(1)
  val seqUser: Seq[User] = Seq(user(0), user(1), user(2))

  val userData: UserData = user.toData
  val seqUserData: Seq[UserData] = seqUser map (_.toData)

  val emptyUser = User(userId, None, None, None, None, None, None, None, UserProfile(None, None, None))
  val emptyUserData = UserData(None, None, None, None, None, None, None, UserProfile(None, None, None))

}
