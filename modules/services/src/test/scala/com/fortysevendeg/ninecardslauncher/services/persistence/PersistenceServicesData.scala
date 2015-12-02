package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor.IterableCursorSeq
import com.fortysevendeg.ninecardslauncher.repository.model._
import com.fortysevendeg.ninecardslauncher.repository.{model => repositoryModel}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App, Card, Collection, DockApp, GeoInfo, User, _}

import scala.util.Random

trait PersistenceServicesData {

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

  val geoInfoId: Int = Random.nextInt(10)
  val nonExistentGeoInfoId: Int = Random.nextInt(10) + 100
  val constrain: String = Random.nextString(5)
  val nonExistentConstrain: String = "nonExistentPackageName"
  val occurrence: String = Random.nextString(5)
  val wifi: String = Random.nextString(5)
  val longitude: Double = Random.nextDouble()
  val latitude: Double = Random.nextDouble()
  val system: Boolean = Random.nextBoolean()

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
    data: repositoryModel.AppData = createRepoAppData()): Seq[repositoryModel.App] =
    List.tabulate(num)(item => repositoryModel.App(id = id + item, data = data))

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
    installedFromGooglePlay: Boolean = installedFromGooglePlay): repositoryModel.AppData = repositoryModel.AppData(
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

  def createSeqGeoInfo(
    num: Int = 5,
    id: Int = geoInfoId,
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system): Seq[GeoInfo] = List.tabulate(num)(
    item =>
      GeoInfo(
        id = id + item,
        constrain = constrain,
        occurrence = occurrence,
        wifi = wifi,
        longitude = longitude,
        latitude = latitude,
        system = system))

  def createSeqRepoGeoInfo(
    num: Int = 5,
    id: Int = geoInfoId,
    data: repositoryModel.GeoInfoData = createRepoGeoInfoData()): Seq[repositoryModel.GeoInfo] =
    List.tabulate(num)(item => repositoryModel.GeoInfo(id = id + item, data = data))

  def createRepoGeoInfoData(
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system): GeoInfoData =
    repositoryModel.GeoInfoData(
      constrain = constrain,
      occurrence = occurrence,
      wifi = wifi,
      longitude = longitude,
      latitude = latitude,
      system = system)

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
    data: repositoryModel.CollectionData = createRepoCollectionData()): Seq[repositoryModel.Collection] =
    List.tabulate(num)(item => repositoryModel.Collection(id = id + item, data = data))

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
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed): CollectionData =
    repositoryModel.CollectionData(
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
    data: repositoryModel.CardData = createRepoCardData()): Seq[repositoryModel.Card] =
    List.tabulate(num)(item => repositoryModel.Card(id = id + item, data = data))

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
    notification: String = notification): CardData =
    repositoryModel.CardData(
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
    data: repositoryModel.UserData = createRepoUserData()): Seq[repositoryModel.User] =
    List.tabulate(num)(item => repositoryModel.User(id = id + item, data = data))

  def createRepoUserData(
    userId: String = userId,
    email: String = email,
    sessionToken: String = sessionToken,
    installationId: String = installationId,
    deviceToken: String = deviceToken,
    androidToken: String = androidToken): UserData =
    repositoryModel.UserData(
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
        name =name,
        dockType =dockType,
        intent =intent,
        imagePath =imagePath,
        position =position))

  def createSeqRepoDockApp(
    num: Int = 5,
    id: Int = dockAppId,
    data: repositoryModel.DockAppData = createRepoDockAppData()): Seq[repositoryModel.DockApp] =
    List.tabulate(num)(item => repositoryModel.DockApp(id = id + item, data = data))

  def createRepoDockAppData(
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): DockAppData =
    repositoryModel.DockAppData(
      name =name,
      dockType =dockType,
      intent =intent,
      imagePath =imagePath,
      position =position)

  val seqApp: Seq[App] = createSeqApp()
  val app: App = seqApp(0)
  val repoAppData: AppData = createRepoAppData()
  val seqRepoApp: Seq[repositoryModel.App] = createSeqRepoApp(data = repoAppData)
  val repoApp: repositoryModel.App = seqRepoApp(0)

  val seqGeoInfo: Seq[GeoInfo] = createSeqGeoInfo()
  val geoInfo: GeoInfo = seqGeoInfo(0)
  val repoGeoInfoData: GeoInfoData = createRepoGeoInfoData()
  val seqRepoGeoInfo: Seq[repositoryModel.GeoInfo] = createSeqRepoGeoInfo(data = repoGeoInfoData)
  val repoGeoInfo: repositoryModel.GeoInfo = seqRepoGeoInfo(0)

  val seqCard: Seq[Card] = createSeqCard()
  val card: Card = seqCard(0)
  val repoCardData: CardData = createRepoCardData()
  val seqRepoCard: Seq[repositoryModel.Card] = createSeqRepoCard(data = repoCardData)
  val repoCard: repositoryModel.Card = seqRepoCard(0)

  val seqCollection: Seq[Collection] = createSeqCollection()
  val collection: Collection = seqCollection(0)
  val repoCollectionData: CollectionData = createRepoCollectionData()
  val seqRepoCollection: Seq[repositoryModel.Collection] = createSeqRepoCollection(data = repoCollectionData)
  val repoCollection: repositoryModel.Collection = seqRepoCollection(0)

  val seqUser: Seq[User] = createSeqUser()
  val user: User = seqUser(0)
  val repoUserData: UserData = createRepoUserData()
  val seqRepoUser: Seq[repositoryModel.User] = createSeqRepoUser(data = repoUserData)
  val repoUser: repositoryModel.User = seqRepoUser(0)

  val seqDockApp: Seq[DockApp] = createSeqDockApp()
  val dockApp: DockApp = seqDockApp(0)
  val repoDockAppData: DockAppData = createRepoDockAppData()
  val seqRepoDockApp: Seq[repositoryModel.DockApp] = createSeqRepoDockApp(data = repoDockAppData)
  val repoDockApp: repositoryModel.DockApp = seqRepoDockApp(0)

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

  def createAddGeoInfoRequest(
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system): AddGeoInfoRequest =
    AddGeoInfoRequest(
      constrain = constrain,
      occurrence = occurrence,
      wifi = wifi,
      longitude = longitude,
      latitude = latitude,
      system = system)

  def createDeleteGeoInfoRequest(geoInfo: GeoInfo): DeleteGeoInfoRequest =
    DeleteGeoInfoRequest(geoInfo = geoInfo)

  def createFetchGeoInfoByConstrainRequest(constrain: String): FetchGeoInfoByConstrainRequest =
    FetchGeoInfoByConstrainRequest(constrain = constrain)

  def createFindGeoInfoByIdRequest(id: Int): FindGeoInfoByIdRequest =
    FindGeoInfoByIdRequest(id = id)

  def createUpdateGeoInfoRequest(
    id: Int = geoInfoId,
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system): UpdateGeoInfoRequest =
    UpdateGeoInfoRequest(
      id = id,
      constrain = constrain,
      occurrence = occurrence,
      wifi = wifi,
      longitude = longitude,
      latitude = latitude,
      system = system)

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

  def createAddDockAppRequest(
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): AddDockAppRequest =
    AddDockAppRequest(
      name = name,
      dockType = dockType,
      intent = intent,
      imagePath = imagePath,
      position = position)

  def createDeleteDockAppRequest(dockApp: DockApp): DeleteDockAppRequest =
    DeleteDockAppRequest(dockApp = dockApp)

  def createFindDockAppByIdRequest(id: Int): FindDockAppByIdRequest =
    FindDockAppByIdRequest(id = id)

  def createUpdateDockAppRequest(
    id: Int = dockAppId,
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): UpdateDockAppRequest =
    UpdateDockAppRequest(
      id = id,
      name = name,
      dockType = dockType,
      intent = intent,
      imagePath = imagePath,
      position = position)

  val iterableCursorApp = new IterableCursorSeq[repositoryModel.App] {
    override def count(): Int = seqRepoApp.length
    override def moveToPosition(pos: Int): repositoryModel.App = seqRepoApp(pos)
    override def close(): Unit = ()
  }
  val iterableApps = new IterableApps(iterableCursorApp)

  val iterableCursorDockApps = new IterableCursorSeq[repositoryModel.DockApp] {
    override def count(): Int = seqRepoDockApp.length
    override def moveToPosition(pos: Int): repositoryModel.DockApp = seqRepoDockApp(pos)
    override def close(): Unit = ()
  }
  val iterableDockApps = new IterableDockApps(iterableCursorDockApps)

  val keyword = "fake-keyword"

}
