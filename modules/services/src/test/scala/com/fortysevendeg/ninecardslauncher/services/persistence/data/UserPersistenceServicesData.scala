package cards.nine.services.persistence.data

import cards.nine.models.{User, UserData, UserProfile}
import cards.nine.repository.model.{User => RepositoryUser, UserData => RepositoryUserData}

import scala.util.Random

trait UserPersistenceServicesData extends PersistenceServicesData {

  val uId: Int = Random.nextInt(10)
  val nonExistentUserId: Int = Random.nextInt(10) + 100
  val email: String = Random.nextString(5)
  val sessionToken: String = Random.nextString(5)
  val apiKey: String = Random.nextString(5)
  val deviceToken: String = Random.nextString(5)
  val marketToken: String = Random.nextString(5)
  val nameUser: String = Random.nextString(5)
  val avatar: String = Random.nextString(5)
  val cover: String = Random.nextString(5)
  val deviceName: String = Random.nextString(5)
  val deviceCloudId: String = Random.nextString(5)

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
  val repoUserData: RepositoryUserData = createRepoUserData()
  val seqRepoUser: Seq[RepositoryUser] = createSeqRepoUser(data = repoUserData)
  val repoUser: RepositoryUser = seqRepoUser(0)


}
