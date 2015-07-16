package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.repository.{model => repositoryModel}
import com.fortysevendeg.ninecardslauncher.services.persistence.models._

import scala.util.Random

trait PersistenceServicesData {

  val cacheCategoryId = Random.nextInt(10)
  val nonExistentCacheCategoryId = Random.nextInt(10) + 100
  val packageName = Random.nextString(5)
  val nonExistentPackageName = "nonExistentPackageName"
  val category = Random.nextString(5)
  val starRating = Random.nextDouble()
  val numDownloads = Random.nextString(5)
  val ratingsCount = Random.nextInt(10)
  val commentCount = Random.nextInt(10)

  val geoInfoId = Random.nextInt(10)
  val nonExistentGeoInfoId = Random.nextInt(10) + 100
  val constrain = Random.nextString(5)
  val nonExistentConstrain = "nonExistentPackageName"
  val occurrence = Random.nextString(5)
  val wifi = Random.nextString(5)
  val longitude = Random.nextDouble()
  val latitude = Random.nextDouble()
  val system = Random.nextBoolean()

  val collectionId = Random.nextInt(10)
  val nonExistentCollectionId = Random.nextInt(10) + 100
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

  val cardId = Random.nextInt(10)
  val nonExistentCardId = Random.nextInt(10) + 100
  val position: Int = Random.nextInt(10)
  val nonExistentPosition: Int = Random.nextInt(10) + 100
  val micros: Int = Random.nextInt(10)
  val term: String = Random.nextString(5)
  val cardType: String = Random.nextString(5)
  val intent: String = Random.nextString(5)
  val imagePath: String = Random.nextString(5)
  val notification: String = Random.nextString(5)

  def createSeqCacheCategory(
    num: Int = 5,
    id: Int = cacheCategoryId,
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount) = (0 until 5) map (item => CacheCategory(
    id = id + item,
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount))

  def createSeqRepoCacheCategory(
    num: Int = 5,
    id: Int = cacheCategoryId,
    data: repositoryModel.CacheCategoryData = createRepoCacheCategoryData()
    ) = (0 until 5) map (item => repositoryModel.CacheCategory(id = id + item, data = data))

  def createRepoCacheCategoryData(
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount) = repositoryModel.CacheCategoryData(
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount)

  def createSeqGeoInfo(
    num: Int = 5,
    id: Int = geoInfoId,
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system) = (0 until 5) map (item => GeoInfo(
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
    data: repositoryModel.GeoInfoData = createRepoGeoInfoData()
    ) = (0 until 5) map (item => repositoryModel.GeoInfo(id = id + item, data = data))

  def createRepoGeoInfoData(
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system) = repositoryModel.GeoInfoData(
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
    cards: Seq[Card] = seqCard) =
    (0 until 5) map (item => Collection(
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
    data: repositoryModel.CollectionData = createRepoCollectionData()
    ) = (0 until 5) map (item => repositoryModel.Collection(id = id + item, data = data))

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
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed) = repositoryModel.CollectionData(
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
    notification: String = notification) =
    (0 until 5) map (item => Card(
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
    data: repositoryModel.CardData = createRepoCardData()
    ) = (0 until 5) map (item => repositoryModel.Card(id = id + item, data = data))

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
    notification: String = notification) = repositoryModel.CardData(
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

  val seqCacheCategory = createSeqCacheCategory()
  val cacheCategory = seqCacheCategory.head
  val repoCacheCategoryData = createRepoCacheCategoryData()
  val seqRepoCacheCategory = createSeqRepoCacheCategory(data = repoCacheCategoryData)
  val repoCacheCategory = seqRepoCacheCategory.head

  val seqGeoInfo = createSeqGeoInfo()
  val geoInfo = seqGeoInfo.head
  val repoGeoInfoData = createRepoGeoInfoData()
  val seqRepoGeoInfo = createSeqRepoGeoInfo(data = repoGeoInfoData)
  val repoGeoInfo = seqRepoGeoInfo.head

  val seqCard = createSeqCard()
  val card = seqCard.head
  val repoCardData = createRepoCardData()
  val seqRepoCard = createSeqRepoCard(data = repoCardData)
  val repoCard = seqRepoCard.head

  val seqCollection = createSeqCollection()
  val collection = seqCollection.head
  val repoCollectionData = createRepoCollectionData()
  val seqRepoCollection = createSeqRepoCollection(data = repoCollectionData)
  val repoCollection = seqRepoCollection.head

  def createAddCacheCategoryRequest(
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount) = AddCacheCategoryRequest(
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount)

  def createDeleteCacheCategoryRequest(cacheCategory: CacheCategory) = DeleteCacheCategoryRequest(
    cacheCategory = cacheCategory)

  def createDeleteCacheCategoryByPackageRequest(packageName: String) = DeleteCacheCategoryByPackageRequest(
    packageName = packageName)

  def createFetchCacheCategoryByPackageRequest(packageName: String) = FetchCacheCategoryByPackageRequest(
    packageName = packageName)

  def createFindCacheCategoryByIdRequest(id: Int) = FindCacheCategoryByIdRequest(
    id = id)

  def createUpdateCacheCategoryRequest(
    id: Int = cacheCategoryId,
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount) = UpdateCacheCategoryRequest(
    id = id,
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount)

  def createAddGeoInfoRequest(
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system) = AddGeoInfoRequest(
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    longitude = longitude,
    latitude = latitude,
    system = system)

  def createDeleteGeoInfoRequest(geoInfo: GeoInfo) = DeleteGeoInfoRequest(
    geoInfo = geoInfo)

  def createFetchGeoInfoByConstrainRequest(constrain: String) = FetchGeoInfoByConstrainRequest(
    constrain = constrain)

  def createFindGeoInfoByIdRequest(id: Int) = FindGeoInfoByIdRequest(
    id = id)

  def createUpdateGeoInfoRequest(
    id: Int = geoInfoId,
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system) = UpdateGeoInfoRequest(
    id = id,
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    longitude = longitude,
    latitude = latitude,
    system = system)

  def createAddCardRequest(
    position: Int = position,
    micros: Int = micros,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification) = AddCardRequest(
    collectionId = collectionId,
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

  def createDeleteCardRequest(card: Card) = DeleteCardRequest(card = card)

  def createFetchCardsByCollectionRequest(collectionId: Int) = FetchCardsByCollectionRequest(
    collectionId = collectionId)

  def createFindCardByIdRequest(id: Int) = FindCardByIdRequest(id = id)

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
    notification: String = notification) = UpdateCardRequest(
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
    cards: Seq[Card] = seqCard) = AddCollectionRequest(
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

  def createDeleteCollectionRequest(collection: Collection) = DeleteCollectionRequest(collection = collection)

  def createFetchCollectionByPositionRequest(position: Int) = FetchCollectionByPositionRequest(
    position = position)

  def createFetchCollectionBySharedCollection(sharedCollectionId: String) = FetchCollectionBySharedCollectionRequest(
    sharedCollectionId = sharedCollectionId)

  def createFindCollectionByIdRequest(id: Int) = FindCollectionByIdRequest(id = id)

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
    cards: Seq[Card] = seqCard) = UpdateCollectionRequest(
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
}
