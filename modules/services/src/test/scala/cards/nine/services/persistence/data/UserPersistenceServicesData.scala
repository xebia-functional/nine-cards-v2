package cards.nine.services.persistence.data

import cards.nine.commons.test.data.UserValues._
import cards.nine.repository.model.{User, UserData}

trait UserPersistenceServicesData {

  def repoUserData(num: Int = 0) =
    UserData(
      email = Option(email),
      sessionToken = Option(sessionToken),
      apiKey = Option(apiKey),
      deviceToken = Option(deviceToken),
      marketToken = Option(marketToken),
      deviceName = Option(userDeviceName),
      deviceCloudId = Option(deviceCloudId),
      name = Option(userName),
      avatar = Option(avatar),
      cover = Option(cover))

  val repoUserData: UserData         = repoUserData(0)
  val seqRepoUserData: Seq[UserData] = Seq(repoUserData(0), repoUserData(1), repoUserData(2))

  def repoUser(num: Int = 0) = User(id = userId + num, data = repoUserData(num))

  val repoUser: User         = repoUser(0)
  val seqRepoUser: Seq[User] = Seq(repoUser(0), repoUser(1), repoUser(2))

}
