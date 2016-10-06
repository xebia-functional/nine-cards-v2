package cards.nine.models

case class User(
  id: Int,
  email: Option[String],
  apiKey: Option[String],
  sessionToken: Option[String],
  deviceToken: Option[String],
  marketToken: Option[String],
  name: Option[String],
  avatar: Option[String],
  cover: Option[String],
  deviceName: Option[String],
  deviceCloudId: Option[String])

case class UserData(
  email: Option[String],
  apiKey: Option[String],
  sessionToken: Option[String],
  deviceToken: Option[String],
  marketToken: Option[String],
  name: Option[String],
  avatar: Option[String],
  cover: Option[String],
  deviceName: Option[String],
  deviceCloudId: Option[String])

object User {

  implicit class UserOps(user: User) {

    def toData = UserData(
      email = user.email,
      apiKey = user.apiKey,
      sessionToken = user.sessionToken,
      deviceToken = user.deviceToken,
      marketToken = user.marketToken,
      name = user.name,
      avatar = user.avatar,
      cover = user.cover,
      deviceName = user.deviceName,
      deviceCloudId = user.deviceCloudId)

  }
}