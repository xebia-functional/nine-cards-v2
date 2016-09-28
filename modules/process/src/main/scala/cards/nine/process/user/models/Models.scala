package cards.nine.process.user.models

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

case class UserProfile(
  name: Option[String],
  avatar: Option[String],
  cover: Option[String])