package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor
import com.fortysevendeg.ninecardslauncher.repository.model.{App => RepositoryApp, AppData => RepositoryAppData, Card => RepositoryCard, CardData => RepositoryCardData, Collection => RepositoryCollection, CollectionData => RepositoryCollectionData, DockApp => RepositoryDockApp, DockAppData => RepositoryDockAppData, Moment => RepositoryMoment, MomentData => RepositoryMomentData, User => RepositoryUser, UserData => RepositoryUserData}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App, Card, Collection, DockApp, Moment, _}
import com.fortysevendeg.ninecardslauncher.services.persistence.reads.MomentImplicits
import play.api.libs.json.Json

import scala.util.Random

trait PersistenceServicesData {

  import MomentImplicits._

  val items = 5
  val item = 1

  val appId: Int =  Random.nextInt(10)
  val className: String = Random.nextString(5)
  val resourceIcon: Int = Random.nextInt(10)
  val colorPrimary: String = Random.nextString(5)
  val dateInstalled: Long = Random.nextLong()
  val dateUpdate: Long = Random.nextLong()
  val version: String = Random.nextString(5)
  val installedFromGooglePlay: Boolean = Random.nextBoolean()

  val packageName: String = Random.nextString(5)
  val nonExistentPackageName: String = "nonExistentPackageName"
  val category: String = Random.nextString(5)
  val starRating: Double = Random.nextDouble()
  val numDownloads: String = Random.nextString(5)
  val ratingsCount: Int = Random.nextInt(10)
  val commentCount: Int = Random.nextInt(10)

  val collectionId: Int = Random.nextInt(10)
  val nonExistentCollectionId: Int = Random.nextInt(10) + 100
  val name: String = Random.nextString(5)
  val collectionType: String = Random.nextString(5)
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: String = Random.nextString(5)
  val constrains: String = Random.nextString(5)
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val nonExistentSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()

  val cardId: Int = Random.nextInt(10)
  val nonExistentCardId: Int = Random.nextInt(10) + 100
  val position: Int = Random.nextInt(10)
  val nonExistentPosition: Int = Random.nextInt(10) + 100
  val micros: Int = Random.nextInt(10)
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

  val dockAppId: Int = Random.nextInt(10)
  val nonExistentDockAppId: Int = Random.nextInt(10) + 100
  val dockType: String = Random.nextString(5)

  val momentId: Int = Random.nextInt(10)
  val nonExistentMomentId: Int = Random.nextInt(10) + 100
  val wifi1: String = Random.nextString(5)
  val wifi2: String = Random.nextString(5)
  val wifi3: String = Random.nextString(5)
  val headphone: Boolean = Random.nextBoolean()
  val wifiSeq: Seq[String] = Seq(wifi1, wifi2, wifi3)
  val wifiString: String = wifiSeq.mkString(",")
  val timeslotJson: String = """[{"from":"from1","to":"to1","days":[11,12,13]},{"from":"from2","to":"to2","days":[21,22,23]}]"""

  def createSeqApp(
    num: Int = 5,
    id: Int = appId,
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    imagePath: String = imagePath,
    colorPrimary: String = colorPrimary,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): Seq[App] = List.tabulate(num)(
    item => App(
      id = id + item,
      name = name,
      packageName = packageName,
      className = className,
      category = category,
      imagePath = imagePath,
      colorPrimary = colorPrimary,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay))

  def createSeqRepoApp(
    num: Int = 5,
    id: Int = appId,
    data: RepositoryAppData = createRepoAppData()): Seq[RepositoryApp] =
    List.tabulate(num)(item => RepositoryApp(id = id + item, data = data))

  def createRepoAppData(
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    imagePath: String = imagePath,
    colorPrimary: String = colorPrimary,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): RepositoryAppData = RepositoryAppData(
    name = name,
    packageName = packageName,
    className = className,
    category = category,
    imagePath = imagePath,
    colorPrimary = colorPrimary,
    dateInstalled = dateInstalled,
    dateUpdate = dateUpdate,
    version = version,
    installedFromGooglePlay = installedFromGooglePlay)

  def createSeqCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: String = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    constrains: String = constrains,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard): Seq[Collection] = List.tabulate(num)(
    item =>
      Collection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(appsCategory),
        constrains = Option(constrains),
        originalSharedCollectionId = Option(originalSharedCollectionId),
        sharedCollectionId = Option(sharedCollectionId),
        sharedCollectionSubscribed = sharedCollectionSubscribed,
        cards = cards))

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
    constrains: String = constrains,
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
      constrains = Option(constrains),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed))

  def createSeqAddCardRequest(
    num: Int = 5,
    collectionId: Int = collectionId,
    position: Int = position,
    micros: Int = micros,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification): Seq[AddCardRequest] = List.tabulate(num)(
    item => AddCardRequest(
      collectionId = Option(collectionId),
      position = position,
      micros = micros,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      starRating = Option(starRating),
      numDownloads = Option(numDownloads),
      notification = Option(notification)))

  def createSeqCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    micros: Int = micros,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification): Seq[Card] = List.tabulate(num)(
    item => Card(
      id = id + item,
      position = position,
      micros = micros,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      starRating = Option(starRating),
      numDownloads = Option(numDownloads),
      notification = Option(notification)))

  def createSeqRepoCard(
    num: Int = 5,
    id: Int = cardId,
    data: RepositoryCardData = createRepoCardData()): Seq[RepositoryCard] =
    List.tabulate(num)(item => RepositoryCard(id = id + item, data = data))

  def createRepoCardData(
    position: Int = position,
    micros: Int = micros,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification): RepositoryCardData =
    RepositoryCardData(
      position = position,
      micros = micros,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      starRating = Option(starRating),
      numDownloads = Option(numDownloads),
      notification = Option(notification))

  def createSeqUser(
    num: Int = 5,
    id: Int = uId,
    userId: String = userId,
    email: String = email,
    sessionToken: String = sessionToken,
    installationId: String = installationId,
    deviceToken: String = deviceToken,
    androidToken: String = androidToken): Seq[User] = List.tabulate(num)(
    item =>
      User(
        id = id + item,
        userId = Option(userId),
        email = Option(email),
        sessionToken = Option(sessionToken),
        installationId = Option(installationId),
        deviceToken = Option(deviceToken),
        androidToken = Option(androidToken)))

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
    androidToken: String = androidToken): RepositoryUserData =
    RepositoryUserData(
      userId = Option(userId),
      email = Option(email),
      sessionToken = Option(sessionToken),
      installationId = Option(installationId),
      deviceToken = Option(deviceToken),
      androidToken = Option(androidToken))

  def createSeqDockApp(
    num: Int = 5,
    id: Int = dockAppId,
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): Seq[DockApp] = List.tabulate(num)(
    item =>
      DockApp(
        id = id + item,
        name = name,
        dockType = dockType,
        intent = intent,
        imagePath = imagePath,
        position = position))

  def createSeqRepoDockApp(
    num: Int = 5,
    id: Int = dockAppId,
    data: RepositoryDockAppData = createRepoDockAppData()): Seq[RepositoryDockApp] =
    List.tabulate(num)(item => RepositoryDockApp(id = id + item, data = data))

  def createRepoDockAppData(
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): RepositoryDockAppData =
    RepositoryDockAppData(
      name = name,
      dockType = dockType,
      intent = intent,
      imagePath = imagePath,
      position = position)

  def createSeqMoment(
    num: Int = 5,
    id: Int = momentId,
    collectionId: Int = collectionId,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone): Seq[Moment] = List.tabulate(num)(
    item =>
      Moment(
        id = id + item,
        collectionId = Option(collectionId),
        timeslot = timeslot,
        wifi = wifi,
        headphone = headphone))

  def createSeqRepoMoment(
    num: Int = 5,
    id: Int = momentId,
    data: RepositoryMomentData = createRepoMomentData()): Seq[RepositoryMoment] =
    List.tabulate(num)(item => RepositoryMoment(id = id + item, data = data))

  def createRepoMomentData(
    collectionId: Int = collectionId,
    timeslot: String = timeslotJson,
    wifiString: String = wifiString,
    headphone: Boolean = headphone): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = Option(collectionId),
      timeslot = timeslot,
      wifi = wifiString,
      headphone = headphone)

  val seqApp: Seq[App] = createSeqApp()
  val app: App = seqApp(0)
  val repoAppData: RepositoryAppData = createRepoAppData()
  val seqRepoApp: Seq[RepositoryApp] = createSeqRepoApp(data = repoAppData)
  val repoApp: RepositoryApp = seqRepoApp(0)

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

  val seqDockApp: Seq[DockApp] = createSeqDockApp()
  val dockApp: DockApp = seqDockApp(0)
  val repoDockAppData: RepositoryDockAppData = createRepoDockAppData()
  val seqRepoDockApp: Seq[RepositoryDockApp] = createSeqRepoDockApp(data = repoDockAppData)
  val repoDockApp: RepositoryDockApp = seqRepoDockApp(0)

  val seqMoment: Seq[Moment] = createSeqMoment()
  val moment: Moment = seqMoment(0)
  val repoMomentData: RepositoryMomentData = createRepoMomentData()
  val seqRepoMoment: Seq[RepositoryMoment] = createSeqRepoMoment(data = repoMomentData)
  val repoMoment: RepositoryMoment = seqRepoMoment(0)

  val where: String = ""

  def createAddAppRequest(
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    imagePath: String = imagePath,
    colorPrimary: String = colorPrimary,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): AddAppRequest =
    AddAppRequest(
      name = name,
      packageName = packageName,
      className = className,
      category = category,
      imagePath = imagePath,
      colorPrimary = colorPrimary,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay)

  def createUpdateAppRequest(
    id: Int = appId,
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    imagePath: String = imagePath,
    colorPrimary: String = colorPrimary,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): UpdateAppRequest =
    UpdateAppRequest(
      id = id,
      name = name,
      packageName = packageName,
      className = className,
      category = category,
      imagePath = imagePath,
      colorPrimary = colorPrimary,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay)

  def createAddCardRequest(
    collectionId: Int = collectionId,
    position: Int = position,
    micros: Int = micros,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification): AddCardRequest =
    AddCardRequest(
      collectionId = Option(collectionId),
      position = position,
      micros = micros,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      starRating = Option(starRating),
      numDownloads = Option(numDownloads),
      notification = Option(notification))

  def createDeleteCardRequest(card: Card): DeleteCardRequest = DeleteCardRequest(card = card)

  def createFetchCardsByCollectionRequest(collectionId: Int): FetchCardsByCollectionRequest =
    FetchCardsByCollectionRequest(collectionId = collectionId)

  def createFindCardByIdRequest(id: Int): FindCardByIdRequest = FindCardByIdRequest(id = id)

  def createUpdateCardRequest(
    id: Int = cardId,
    position: Int = position,
    micros: Int = micros,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification): UpdateCardRequest =
    UpdateCardRequest(
      id = id,
      position = position,
      micros = micros,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = imagePath,
      starRating = Option(starRating),
      numDownloads = Option(numDownloads),
      notification = Option(notification))

  def createAddCollectionRequest(
    position: Int = position,
    name: String = name,
    collectionType: String = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    constrains: String = constrains,
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
      constrains = Option(constrains),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
      cards = createSeqAddCardRequest())

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
    constrains: String = constrains,
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
      constrains = Option(constrains),
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
    androidToken: String = androidToken): AddUserRequest =
    AddUserRequest(
      userId = Option(userId),
      email = Option(email),
      sessionToken = Option(sessionToken),
      installationId = Option(installationId),
      deviceToken = Option(deviceToken),
      androidToken = Option(androidToken))

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
    androidToken: String = androidToken): UpdateUserRequest =
    UpdateUserRequest(
      id = id,
      userId = Option(userId),
      email = Option(email),
      sessionToken = Option(sessionToken),
      installationId = Option(installationId),
      deviceToken = Option(deviceToken),
      androidToken = Option(androidToken))

  def createCreateOrUpdateDockAppRequest(
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): CreateOrUpdateDockAppRequest =
    CreateOrUpdateDockAppRequest(
      name = name,
      dockType = dockType,
      intent = intent,
      imagePath = imagePath,
      position = position)

  def createDeleteDockAppRequest(dockApp: DockApp): DeleteDockAppRequest =
    DeleteDockAppRequest(dockApp = dockApp)

  def createFindDockAppByIdRequest(id: Int): FindDockAppByIdRequest =
    FindDockAppByIdRequest(id = id)

  val iterableCursorApp = new IterableCursor[RepositoryApp] {
    override def count(): Int = seqRepoApp.length
    override def moveToPosition(pos: Int): RepositoryApp = seqRepoApp(pos)
    override def close(): Unit = ()
  }
  val iterableApps = new IterableApps(iterableCursorApp)

  val iterableCursorDockApps = new IterableCursor[RepositoryDockApp] {
    override def count(): Int = seqRepoDockApp.length
    override def moveToPosition(pos: Int): RepositoryDockApp = seqRepoDockApp(pos)
    override def close(): Unit = ()
  }
  val iterableDockApps = new IterableDockApps(iterableCursorDockApps)

  val keyword = "fake-keyword"

  def createAddMomentRequest(
    collectionId: Int = collectionId,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone): AddMomentRequest =
    AddMomentRequest(
      collectionId = Option(collectionId),
      timeslot = timeslot,
      wifi = wifi,
      headphone = headphone)

  def createDeleteMomentRequest(moment: Moment): DeleteMomentRequest =
    DeleteMomentRequest(moment = moment)

  def createFindMomentByIdRequest(id: Int): FindMomentByIdRequest =
    FindMomentByIdRequest(id = id)

  def createUpdateMomentRequest(
    id: Int = momentId,
    collectionId: Int = collectionId,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone): UpdateMomentRequest =
    UpdateMomentRequest(
      id = id,
      collectionId = Option(collectionId),
      timeslot = timeslot,
      wifi = wifi,
      headphone = headphone)

}
