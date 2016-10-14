package cards.nine.services.persistence.data

import cards.nine.commons.test.data.UserValues._
import cards.nine.repository.model.{User => RepositoryUser, UserData => RepositoryUserData}

trait UserPersistenceServicesData {

  def createSeqRepoUser(
    num: Int = 5,
    id: Int = uId,
    data: RepositoryUserData = createRepoUserData()): Seq[RepositoryUser] =
    List.tabulate(num)(item => RepositoryUser(id = id + item, data = data))

  def createRepoUserData(
    email: String = email,
    sessionToken: String = sessionToken,
    apiKey: String = apiKey,
    deviceToken: String = deviceToken,
    marketToken: String = marketToken,
    name: String = nameUser,
    avatar: String = avatar,
    cover: String = cover,
    deviceName: String = deviceName,
    deviceCloudId: String = deviceCloudId): RepositoryUserData =
    RepositoryUserData(
      email = Option(email),
      sessionToken = Option(sessionToken),
      apiKey = Option(apiKey),
      deviceToken = Option(deviceToken),
      marketToken = Option(marketToken),
      name = Option(name),
      avatar = Option(avatar),
      cover = Option(cover),
      deviceName = Option(deviceName),
      deviceCloudId = Option(deviceCloudId))

  val repoUserData: RepositoryUserData = createRepoUserData()
  val seqRepoUser: Seq[RepositoryUser] = createSeqRepoUser(data = repoUserData)
  val repoUser: RepositoryUser = seqRepoUser(0)


}
