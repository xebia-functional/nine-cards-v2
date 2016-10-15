package cards.nine.services.persistence.data

import cards.nine.commons.test.data.UserValues._
import cards.nine.repository.model.{User, UserData}

trait UserPersistenceServicesData {

  def userData(num: Int = 0) = UserData(
    email = Option(email),
    sessionToken = Option(sessionToken),
    apiKey = Option(apiKey),
    deviceToken = Option(deviceToken),
    marketToken = Option(marketToken),
    deviceName = Option(deviceName),
    deviceCloudId = Option(deviceCloudId),
    name = Option(userName),
    avatar = Option(avatar),
    cover = Option(cover))

  val repoUserData: UserData = userData(0)
  val seqRepoUserData: Seq[UserData]  = Seq(userData(0), userData(1), userData(2))

  def user(num: Int = 0) = User(
    id = userId + num,
    data = userData(num))

  val repoUser: User = user(0)
  val seqRepoUser: Seq[User] = Seq(user(0), user(1), user(2))

}
