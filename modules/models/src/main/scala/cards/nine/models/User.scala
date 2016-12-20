package cards.nine.models

case class User(
    id: Int,
    email: Option[String],
    apiKey: Option[String],
    sessionToken: Option[String],
    deviceToken: Option[String],
    marketToken: Option[String],
    deviceName: Option[String],
    deviceCloudId: Option[String],
    userProfile: UserProfile)

case class UserData(
    email: Option[String],
    apiKey: Option[String],
    sessionToken: Option[String],
    deviceToken: Option[String],
    marketToken: Option[String],
    deviceName: Option[String],
    deviceCloudId: Option[String],
    userProfile: UserProfile)

case class UserProfile(name: Option[String], avatar: Option[String], cover: Option[String])

object User {

  implicit class UserOps(user: User) {

    def toData =
      UserData(
        email = user.email,
        apiKey = user.apiKey,
        sessionToken = user.sessionToken,
        deviceToken = user.deviceToken,
        marketToken = user.marketToken,
        deviceName = user.deviceName,
        deviceCloudId = user.deviceCloudId,
        userProfile = user.userProfile)

  }
}
