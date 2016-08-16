package com.fortysevendeg.ninecardslauncher.services.persistence.data


import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepositoryCard, CardData => RepositoryCardData, CardsWithCollectionId, Collection => RepositoryCollection, CollectionData => RepositoryCollectionData, DataCounter => RepositoryDataCounter, Moment => RepositoryMoment, MomentData => RepositoryMomentData, User => RepositoryUser, UserData => RepositoryUserData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import com.fortysevendeg.ninecardslauncher.services.persistence.reads.MomentImplicits
import play.api.libs.json.Json

import scala.util.Random

trait PersistenceServicesData extends Conversions {

  import MomentImplicits._

  val items = 5
  val item = 1

  val className: String = Random.nextString(5)
  val resourceIcon: Int = Random.nextInt(10)
  val dateInstalled: Long = Random.nextLong()
  val dateUpdate: Long = Random.nextLong()
  val version: String = Random.nextString(5)
  val installedFromGooglePlay: Boolean = Random.nextBoolean()

  val packageName: String = Random.nextString(5)
  val nonExistentPackageName: String = "nonExistentPackageName"
  val category: String = Random.nextString(5)
  val ratingsCount: Int = Random.nextInt(10)
  val commentCount: Int = Random.nextInt(10)

  val collectionId: Int = Random.nextInt(10)
  val nonExistentCollectionId: Int = Random.nextInt(10) + 100
  val name: String = Random.nextString(5)
  val collectionType: String = Random.nextString(5)
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: String = Random.nextString(5)
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val nonExistentSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()

  val cardId: Int = Random.nextInt(10)
  val nonExistentCardId: Int = Random.nextInt(10) + 100
  val position: Int = Random.nextInt(10)
  val nonExistentPosition: Int = Random.nextInt(10) + 100
  val term: String = Random.nextString(5)
  val cardType: String = Random.nextString(5)
  val intent: String = Random.nextString(5)
  val imagePath: String = Random.nextString(5)
  val notification: String = Random.nextString(5)

  val uId: Int = Random.nextInt(10)
  val nonExistentUserId: Int = Random.nextInt(10) + 100
  val userId: String = Random.nextString(5)
  val email: String = Random.nextString(5)
  val sessionToken: String = Random.nextString(5)
  val installationId: String = Random.nextString(5)
  val deviceToken: String = Random.nextString(5)
  val androidToken: String = Random.nextString(5)
  val nameUser: String = Random.nextString(5)
  val avatar: String = Random.nextString(5)
  val cover: String = Random.nextString(5)
  val deviceName: String = Random.nextString(5)
  val deviceCloudId: String = Random.nextString(5)

  val momentId: Int = Random.nextInt(10)
  val nonExistentMomentId: Int = Random.nextInt(10) + 100
  val wifi1: String = Random.nextString(5)
  val wifi2: String = Random.nextString(5)
  val wifi3: String = Random.nextString(5)
  val headphone: Boolean = Random.nextBoolean()
  val wifiSeq: Seq[String] = Seq(wifi1, wifi2, wifi3)
  val wifiString: String = wifiSeq.mkString(",")
  val timeslotJson: String = """[{"from":"from1","to":"to1","days":[11,12,13]},{"from":"from2","to":"to2","days":[21,22,23]}]"""
  val collectionIdOption = Option(collectionId)

  val termDataCounter: String = Random.nextString(1)
  val countDataCounter: Int = Random.nextInt(2)
  val momentType1: String = "HOME"

  val seqMoment: Seq[Moment] = createSeqMoment()
  val servicesMoment: Moment = seqMoment(0)
  val repoMomentData: RepositoryMomentData = createRepoMomentData()
  val seqRepoMoment: Seq[RepositoryMoment] = createSeqRepoMoment(data = repoMomentData)
  val repoMoment: RepositoryMoment = seqRepoMoment(0)


  def createSeqCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: String = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    cards: Seq[Card] = seqCard,
    moment: Option[Moment] = Some(servicesMoment),
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed): Seq[Collection] = List.tabulate(num)(
    item =>
      Collection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(appsCategory),
        cards = cards,
        moment = moment,
        originalSharedCollectionId = Option(originalSharedCollectionId),
        sharedCollectionId = Option(sharedCollectionId),
        sharedCollectionSubscribed = sharedCollectionSubscribed))

  def createSeqRepoCollection(
    num: Int = 5,
    id: Int = collectionId,
    data: RepositoryCollectionData = createRepoCollectionData()): Seq[RepositoryCollection] =
    List.tabulate(num)(item => RepositoryCollection(id = id + item, data = data))

  def createRepoCollectionData(
    position: Int = position,
    name: String = name,
    collectionType: String = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed): RepositoryCollectionData =
    RepositoryCollectionData(
      position = position,
      name = name,
      collectionType = collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = Option(appsCategory),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed))

  def createSeqAddCardRequest(
    num: Int = 5,
    collectionId: Int = collectionId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): Seq[AddCardRequest] = List.tabulate(num)(
    item => AddCardRequest(
      collectionId = Option(collectionId),
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      notification = Option(notification)))

  def createSeqCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): Seq[Card] = List.tabulate(num)(
    item => Card(
      id = id + item,
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      notification = Option(notification)))

  def createSeqRepoCard(
    num: Int = 5,
    id: Int = cardId,
    data: RepositoryCardData = createRepoCardData()): Seq[RepositoryCard] =
    List.tabulate(num)(item => RepositoryCard(id = id + item, data = data))

  def createRepoCardData(
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): RepositoryCardData =
    RepositoryCardData(
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      notification = Option(notification))

  def createSeqUser(
    num: Int = 5,
    id: Int = uId,
    userId: String = userId,
    email: String = email,
    sessionToken: String = sessionToken,
    installationId: String = installationId,
    deviceToken: String = deviceToken,
    androidToken: String = androidToken,
    name: String = nameUser,
    avatar: String = avatar,
    cover: String = cover,
    deviceName: String = deviceName,
    deviceCloudId: String = deviceCloudId): Seq[User] = List.tabulate(num)(
    item =>
      User(
        id = id + item,
        userId = Option(userId),
        email = Option(email),
        sessionToken = Option(sessionToken),
        installationId = Option(installationId),
        deviceToken = Option(deviceToken),
        androidToken = Option(androidToken),
        name = Option(name),
        avatar = Option(avatar),
        cover = Option(cover),
        deviceName = Option(deviceName),
        deviceCloudId = Option(deviceCloudId)))

  def createSeqRepoUser(
    num: Int = 5,
    id: Int = uId,
    data: RepositoryUserData = createRepoUserData()): Seq[RepositoryUser] =
    List.tabulate(num)(item => RepositoryUser(id = id + item, data = data))

  def createRepoUserData(
    userId: String = userId,
    email: String = email,
    sessionToken: String = sessionToken,
    installationId: String = installationId,
    deviceToken: String = deviceToken,
    androidToken: String = androidToken,
    name: String = nameUser,
    avatar: String = avatar,
    cover: String = cover,
    deviceName: String = deviceName,
    deviceCloudId: String = deviceCloudId): RepositoryUserData =
    RepositoryUserData(
      userId = Option(userId),
      email = Option(email),
      sessionToken = Option(sessionToken),
      installationId = Option(installationId),
      deviceToken = Option(deviceToken),
      androidToken = Option(androidToken),
      name = Option(name),
      avatar = Option(avatar),
      cover = Option(cover),
      deviceName = Option(deviceName),
      deviceCloudId = Option(deviceCloudId))

  def createSeqMoment(
    num: Int = 5,
    id: Int = momentId,
    collectionId: Option[Int] = collectionIdOption,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone,
    momentType: Option[String] = Option(momentType1)): Seq[Moment] = List.tabulate(num)(
    item =>
      Moment(
        id = id + item,
        collectionId = collectionId,
        timeslot = timeslot,
        wifi = wifi,
        headphone = headphone,
        momentType = momentType))

  def createSeqRepoMoment(
    num: Int = 5,
    id: Int = momentId,
    data: RepositoryMomentData = createRepoMomentData()): Seq[RepositoryMoment] =
    List.tabulate(num)(item => RepositoryMoment(id = id + item, data = data))

  def createRepoMomentData(
    collectionId: Option[Int] = collectionIdOption,
    timeslot: String = timeslotJson,
    wifiString: String = wifiString,
    headphone: Boolean = headphone,
    momentType: Option[String] = Option(momentType1)): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = collectionId,
      timeslot = timeslot,
      wifi = wifiString,
      headphone = headphone,
      momentType = momentType)



  val seqCard: Seq[Card] = createSeqCard()
  val card: Card = seqCard(0)
  val repoCardData: RepositoryCardData = createRepoCardData()
  val seqRepoCard: Seq[RepositoryCard] = createSeqRepoCard(data = repoCardData)
  val repoCard: RepositoryCard = seqRepoCard(0)

  val seqCollection: Seq[Collection] = createSeqCollection()
  val collection: Collection = seqCollection(0)
  val repoCollectionData: RepositoryCollectionData = createRepoCollectionData()
  val seqRepoCollection: Seq[RepositoryCollection] = createSeqRepoCollection(data = repoCollectionData)
  val repoCollection: RepositoryCollection = seqRepoCollection(0)

  val seqUser: Seq[User] = createSeqUser()
  val user: User = seqUser(0)
  val repoUserData: RepositoryUserData = createRepoUserData()
  val seqRepoUser: Seq[RepositoryUser] = createSeqRepoUser(data = repoUserData)
  val repoUser: RepositoryUser = seqRepoUser(0)

  val where: String = ""


  def createAddCardRequest(
    collectionId: Int = collectionId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): AddCardRequest =
    AddCardRequest(
      collectionId = Option(collectionId),
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      notification = Option(notification))

  def createDeleteCardRequest(card: Card): DeleteCardRequest = DeleteCardRequest(card = card)

  def createFetchCardsByCollectionRequest(collectionId: Int): FetchCardsByCollectionRequest =
    FetchCardsByCollectionRequest(collectionId = collectionId)

  def createFindCardByIdRequest(id: Int): FindCardByIdRequest = FindCardByIdRequest(id = id)

  def createUpdateCardsRequest(
    num: Int = 5,
    id: Int = cardId) =
    UpdateCardsRequest(
      List.tabulate(num)(item => createUpdateCardRequest(id = id + item)))

  def createUpdateCardRequest(
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): UpdateCardRequest =
    UpdateCardRequest(
      id = id,
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      notification = Option(notification))

  def createAddCollectionRequest(
    position: Int = position,
    name: String = name,
    collectionType: String = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard): AddCollectionRequest =
    AddCollectionRequest(
      position = position,
      name = name,
      collectionType = collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = Option(appsCategory),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
      cards = createSeqAddCardRequest(),
      moment = Option(createAddMomentRequest()))

  def createDeleteCollectionRequest(collection: Collection): DeleteCollectionRequest =
    DeleteCollectionRequest(collection = collection)

  def createFetchCollectionByPositionRequest(position: Int): FetchCollectionByPositionRequest =
    FetchCollectionByPositionRequest(position = position)

  def createFetchCollectionBySharedCollection(sharedCollectionId: String): FetchCollectionBySharedCollectionRequest =
    FetchCollectionBySharedCollectionRequest(sharedCollectionId = sharedCollectionId)

  def createFindCollectionByIdRequest(id: Int): FindCollectionByIdRequest = FindCollectionByIdRequest(id = id)

  def createUpdateCollectionRequest(
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: String = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard): UpdateCollectionRequest =
    UpdateCollectionRequest(
      id = id,
      position = position,
      name = name,
      collectionType = collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = Option(appsCategory),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
      cards = seqCard)

  def createAddUserRequest(
    userId: String = userId,
    email: String = email,
    sessionToken: String = sessionToken,
    installationId: String = installationId,
    deviceToken: String = deviceToken,
    androidToken: String = androidToken,
    name: String = nameUser,
    avatar: String = avatar,
    cover: String = cover,
    deviceName: String = deviceName,
    deviceCloudId: String = deviceCloudId): AddUserRequest =
    AddUserRequest(
      userId = Option(userId),
      email = Option(email),
      sessionToken = Option(sessionToken),
      installationId = Option(installationId),
      deviceToken = Option(deviceToken),
      androidToken = Option(androidToken),
      name = Option(name),
      avatar = Option(avatar),
      cover = Option(cover),
      deviceName = Option(deviceName),
      deviceCloudId = Option(deviceCloudId))

  def createDeleteUserRequest(user: User): DeleteUserRequest =
    DeleteUserRequest(user = user)

  def createFindUserByIdRequest(id: Int): FindUserByIdRequest =
    FindUserByIdRequest(id = id)

  def createUpdateUserRequest(
    id: Int = uId,
    userId: String = userId,
    email: String = email,
    sessionToken: String = sessionToken,
    installationId: String = installationId,
    deviceToken: String = deviceToken,
    androidToken: String = androidToken,
    name: String = nameUser,
    avatar: String = avatar,
    cover: String = cover,
    deviceName: String = deviceName,
    deviceCloudId: String = deviceCloudId): UpdateUserRequest =
    UpdateUserRequest(
      id = id,
      userId = Option(userId),
      email = Option(email),
      sessionToken = Option(sessionToken),
      installationId = Option(installationId),
      deviceToken = Option(deviceToken),
      androidToken = Option(androidToken),
      name = Option(name),
      avatar = Option(avatar),
      cover = Option(cover),
      deviceName = Option(deviceName),
      deviceCloudId = Option(deviceCloudId))

  def createDataCounter(i: Int): RepositoryDataCounter =
    RepositoryDataCounter(
      term = s"$i - $termDataCounter",
      count = countDataCounter
    )

  val keyword = "fake-keyword"

  def createAddMomentRequest(
    collectionId: Option[Int] = collectionIdOption,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone,
    momentType: Option[String] = Option(momentType1)): AddMomentRequest =
    AddMomentRequest(
      collectionId = collectionId,
      timeslot = timeslot,
      wifi = wifi,
      headphone = headphone,
      momentType = momentType)

  def createDeleteMomentRequest(moment: Moment): DeleteMomentRequest =
    DeleteMomentRequest(moment = moment)

  def createFindMomentByIdRequest(id: Int): FindMomentByIdRequest =
    FindMomentByIdRequest(id = id)

  def createUpdateMomentRequest(
    id: Int = momentId,
    collectionId: Option[Int] = collectionIdOption,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone,
    momentType: Option[String] = Option(momentType1)): UpdateMomentRequest =
    UpdateMomentRequest(
      id = id,
      collectionId = collectionId,
      timeslot = timeslot,
      wifi = wifi,
      headphone = headphone,
      momentType = momentType)

  val dataCounters = 1 to 10 map createDataCounter

  val addCollectionRequest = createAddCollectionRequest()

  val seqAddCardWithCollectionIdRequest = Seq(CardsWithCollectionId(collection.id, Seq.empty))

}
