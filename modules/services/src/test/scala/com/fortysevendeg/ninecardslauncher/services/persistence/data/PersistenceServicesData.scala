package cards.nine.services.persistence.data

import cards.nine.models.types._
import cards.nine.models.{CardData, _}
import cards.nine.repository.model.{Card => RepositoryCard, CardData => RepositoryCardData, CardsWithCollectionId, Collection => RepositoryCollection, CollectionData => RepositoryCollectionData, DataCounter => RepositoryDataCounter, Moment => RepositoryMoment, MomentData => RepositoryMomentData, User => RepositoryUser, UserData => RepositoryUserData, Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}
import cards.nine.services.persistence.conversions.Conversions
import cards.nine.services.persistence.reads.MomentImplicits
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
  val collectionType: CollectionType = FreeCollectionType
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: String = "MISC"
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val nonExistentSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()
  val publicCollectionStatus: PublicCollectionStatus = NotPublished

  val cardId: Int = Random.nextInt(10)
  val nonExistentCardId: Int = Random.nextInt(10) + 100
  val position: Int = Random.nextInt(10)
  val nonExistentPosition: Int = Random.nextInt(10) + 100
  val term: String = Random.nextString(5)
  val cardType: String = "APP"
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val imagePath: String = Random.nextString(5)
  val notification: String = Random.nextString(5)

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
  val momentType: NineCardsMoment = HomeMorningMoment
  val momentTypeStr: String = "HOME"

  val widgetId: Int = Random.nextInt(10)
  val widgetType: String = "APP"
  val appWidgetId: Int = Random.nextInt(10) + 1
  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int = Random.nextInt(8)
  val spanY: Int = Random.nextInt(8)
  val label: String = Random.nextString(5)
  val widgetImagePath: String = Random.nextString(5)
  val labelOption = Option(label)
  val widgetImagePathOption = Option(widgetImagePath)
  val widgetIntentOption = Option(intent)

  val nonExistentWidgetId: Int = Random.nextInt(10) + 100
  val nonExistentAppWidgetId: Int = Random.nextInt(10) + 100

  def createSeqCardData(
    num: Int = 5,
    collectionId: Int = collectionId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): Seq[CardData] = List.tabulate(num)(
    item => CardData(
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = CardType(cardType),
      intent = jsonToNineCardIntent(intent),
      imagePath = Option(imagePath),
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
      cardType = CardType(cardType),
      intent = jsonToNineCardIntent(intent),
      imagePath = Option(imagePath),
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
      imagePath = Option(imagePath),
      notification = Option(notification))

  val seqCard: Seq[Card] = createSeqCard()
  val card: Card = seqCard(0)
  val seqCardData: Seq[CardData] = createSeqCardData()
  val cardData: CardData = seqCardData(0)
  val repoCardData: RepositoryCardData = createRepoCardData()
  val seqRepoCard: Seq[RepositoryCard] = createSeqRepoCard(data = repoCardData)
  val repoCard: RepositoryCard = seqRepoCard(0)

  def createSeqRepoWidget(
    num: Int = 5,
    id: Int = widgetId,
    data: RepositoryWidgetData = createRepoWidgetData()): Seq[RepositoryWidget] =
    List.tabulate(num)(item => RepositoryWidget(id = id + item, data = data))

  def createRepoWidgetData(
    momentId: Int = momentId,
    packageName: String = packageName,
    className: String = className,
    appWidgetId: Int = appWidgetId,
    startX: Int = startX,
    startY: Int = startY,
    spanX: Int = spanX,
    spanY: Int = spanY,
    widgetType: String = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption): RepositoryWidgetData =
    RepositoryWidgetData(
      momentId = momentId,
      packageName = packageName,
      className = className,
      appWidgetId = appWidgetId,
      startX = startX,
      startY = startY,
      spanX = spanX,
      spanY = spanY,
      widgetType = widgetType,
      label = label,
      imagePath = imagePath,
      intent = intent)

  def createSeqWidget(
    num: Int = 5,
    id: Int = widgetId,
    momentId: Int = momentId,
    packageName: String = packageName,
    className: String = className,
    appWidgetId: Int = appWidgetId,
    startX: Int = startX,
    startY: Int = startY,
    spanX: Int = spanX,
    spanY: Int = spanY,
    widgetType: String = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption) =
    (0 until 5) map (
      item =>
        Widget(
          id = id + item,
          momentId = momentId,
          packageName = packageName,
          className = className,
          appWidgetId = Option(appWidgetId),
          area =
            WidgetArea(
              startX = startX,
              startY = startY,
              spanX = spanX,
              spanY = spanY),
          widgetType = WidgetType(widgetType),
          label = label,
          imagePath = imagePath,
          intent = intent map jsonToNineCardIntent))

  def createSeqWidgetData(
    num: Int = 5,
    momentId: Int = momentId,
    packageName: String = packageName,
    className: String = className,
    appWidgetId: Int = appWidgetId,
    startX: Int = startX,
    startY: Int = startY,
    spanX: Int = spanX,
    spanY: Int = spanY,
    widgetType: String = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption) =
    (0 until 5) map (
      item =>
        WidgetData(
          momentId = momentId,
          packageName = packageName,
          className = className,
          appWidgetId = Option(appWidgetId),
          area =
            WidgetArea(
              startX = startX,
              startY = startY,
              spanX = spanX,
              spanY = spanY),
          widgetType = WidgetType(widgetType),
          label = label,
          imagePath = imagePath,
          intent = intent map jsonToNineCardIntent))

  val repoWidgetData: RepositoryWidgetData = createRepoWidgetData()
  val seqRepoWidget: Seq[RepositoryWidget] = createSeqRepoWidget(data = repoWidgetData)
  val repoWidgetDataNone: RepositoryWidgetData = createRepoWidgetData(appWidgetId = 0)
  val seqRepoWidgetNone: Seq[RepositoryWidget] = createSeqRepoWidget(data = repoWidgetDataNone)
  val seqWidget: Seq[Widget] = createSeqWidget()
  val seqWidgetData: Seq[WidgetData] = createSeqWidgetData()
  val repoWidget = seqRepoWidget(0)
  val repoWidgetNone = seqRepoWidgetNone(0)
  val widget = seqWidget(0)
  val widgetData = seqWidgetData(0)

  def createMomentData(
    collectionId: Option[Int] = collectionIdOption,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone,
    momentType: NineCardsMoment = momentType): MomentData =
    MomentData(
      collectionId = collectionId,
      timeslot = timeslot,
      wifi = wifi,
      headphone = headphone,
      momentType = Option(momentType),
      widgets = Option(seqWidgetData))

  def createRepoMomentData(
    collectionId: Option[Int] = collectionIdOption,
    timeslot: String = timeslotJson,
    wifiString: String = wifiString,
    headphone: Boolean = headphone,
    momentType: String = momentTypeStr): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = collectionId,
      timeslot = timeslot,
      wifi = wifiString,
      headphone = headphone,
      momentType = Option(momentType))

  def createSeqMoment(
    num: Int = 5,
    id: Int = momentId,
    collectionId: Option[Int] = collectionIdOption,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone,
    momentType: NineCardsMoment = momentType): Seq[Moment] = List.tabulate(num)(
    item =>
      Moment(
        id = id + item,
        collectionId = collectionId,
        timeslot = timeslot,
        wifi = wifi,
        headphone = headphone,
        momentType = Option(momentType),
        widgets = Option(seqWidgetData)))

  def createSeqRepoMoment(
    num: Int = 5,
    id: Int = momentId,
    data: RepositoryMomentData = createRepoMomentData()): Seq[RepositoryMoment] =
    List.tabulate(num)(item => RepositoryMoment(id = id + item, data = data))

  def createSeqMomentData(
    num: Int = 5) :Seq[MomentData]  =
    List.tabulate(num)(item => createMomentData())


  val seqMoment: Seq[Moment] = createSeqMoment()
  val repoMomentData: RepositoryMomentData = createRepoMomentData()
  val seqRepoMoment: Seq[RepositoryMoment] = createSeqRepoMoment(data = repoMomentData)
  val repoMoment: RepositoryMoment = seqRepoMoment(0)

  val moment = seqMoment(0)
  val momentData = createMomentData()
  val seqMomentData = createSeqMomentData()

  val where: String = ""

  def createSeqCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    cards: Seq[Card] = seqCard,
    moment: Moment = moment,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    publicCollectionStatus: PublicCollectionStatus = publicCollectionStatus): Seq[Collection] = List.tabulate(num)(
    item =>
      Collection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(NineCardsCategory(appsCategory)),
        cards = cards,
        moment = Option(moment),
        originalSharedCollectionId = Option(originalSharedCollectionId),
        sharedCollectionId = Option(sharedCollectionId),
        sharedCollectionSubscribed = sharedCollectionSubscribed,
        publicCollectionStatus = publicCollectionStatus))

  def createSeqRepoCollection(
    num: Int = 5,
    id: Int = collectionId,
    data: RepositoryCollectionData = createRepoCollectionData()): Seq[RepositoryCollection] =
    List.tabulate(num)(item => RepositoryCollection(id = id + item, data = data))

  def createRepoCollectionData(
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed): RepositoryCollectionData =
    RepositoryCollectionData(
      position = position,
      name = name,
      collectionType = collectionType.name,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = Option(appsCategory),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed))

  def createCollectionData(
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard): CollectionData =
    CollectionData(
      position = position,
      name = name,
      collectionType = collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = Option(NineCardsCategory(appsCategory)),
      cards = seqCardData,
      moment = Option(createMomentData()),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = sharedCollectionSubscribed)

  def createSeqCollectionData(
    num: Int = 5) :Seq[CollectionData]  =
    List.tabulate(num)(item => createCollectionData())

  val seqCollection: Seq[Collection] = createSeqCollection()
  val collection: Collection = seqCollection(0)
  val repoCollectionData: RepositoryCollectionData = createRepoCollectionData()
  val seqRepoCollection: Seq[RepositoryCollection] = createSeqRepoCollection(data = repoCollectionData)
  val repoCollection: RepositoryCollection = seqRepoCollection(0)

  val collectionData = createCollectionData()
  val seqCollectionData = createSeqCollectionData()
  val seqAddCardWithCollectionIdRequest = Seq(CardsWithCollectionId(collection.id, Seq.empty))

}
