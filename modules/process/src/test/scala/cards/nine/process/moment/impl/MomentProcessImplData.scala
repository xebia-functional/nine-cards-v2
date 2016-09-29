package cards.nine.process.moment.impl

import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.commons.models._
import cards.nine.process.commons.types.CardType._
import cards.nine.process.commons.types.CollectionType._
import cards.nine.process.commons.types.NineCardCategory._
import cards.nine.process.commons.types._
import cards.nine.process.moment.models.App
import cards.nine.process.moment.{SaveMomentRequest, UpdateMomentRequest}
import cards.nine.services.persistence.models.{App => ServicesApp, Card => ServicesCard, Collection => ServicesCollection, Moment => ServicesMoment, MomentTimeSlot => ServicesMomentTimeSlot}
import org.joda.time.DateTime
import play.api.libs.json.Json

import scala.util.Random

trait MomentProcessImplData {

  val collectionId = Random.nextInt(10)
  val name: String = Random.nextString(5)
  val collectionType: CollectionType = collectionTypes(Random.nextInt(collectionTypes.length))
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: NineCardCategory = appsCategories(Random.nextInt(appsCategories.length))
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()
  val publicCollectionStatusSeq = Seq(NotPublished, PublishedByMe, PublishedByOther, Subscribed)
  val publicCollectionStatus = publicCollectionStatusSeq(Random.nextInt(publicCollectionStatusSeq.size))

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val category1 = "category1"
  val imagePath1 = "imagePath1"
  val dateInstalled1 = 1L
  val dateUpdate1 = 1L
  val version1 = "22"
  val installedFromGooglePlay1 = true

  val appId = Random.nextInt(10)
  val momentId = Random.nextInt(10)
  val cardId = Random.nextInt(10)
  val position: Int = Random.nextInt(10)
  val term: String = Random.nextString(5)
  val packageName = Random.nextString(5)
  val className = Random.nextString(5)
  val cardType: CardType = cardTypes(Random.nextInt(cardTypes.length))
  val imagePath: String = Random.nextString(5)
  val notification: String = Random.nextString(5)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val from = "8:00"
  val to = "19:00"
  val days = Seq(0, 1, 1, 1, 1, 1, 0)

  val collectionId1 = 1
  val homeAppPackageName = "com.google.android.apps.plus"
  val nightAppPackageName = "com.Slack"
  val workAppPackageName = "com.google.android.apps.photos"
  val transitAppPackageName = "com.google.android.apps.maps"
  val momentType = Seq("HOME", "WORK", "NIGHT", "TRANSIT")

  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int = Random.nextInt(8)
  val spanY: Int = Random.nextInt(8)

  val ssid: String = Random.nextString(5)

  val item: Int = Random.nextInt(5)

  val updateMomentRequest =
    UpdateMomentRequest(
      id = momentId,
      collectionId = Option(collectionId1),
      timeslot = createSeqMomentTimeSlot(),
      wifi = Seq.empty,
      headphone = false,
      momentType = Option(NineCardsMoment(momentType(0)))
    )

  def createSeqCollection(
    num: Int = 4,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: NineCardCategory = appsCategory,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard,
    publicCollectionStatus: PublicCollectionStatus = publicCollectionStatus) =
    (0 until num) map (
      item =>
        Collection(
          id = id + item,
          position = position,
          name = name,
          collectionType = collectionType,
          icon = icon,
          themedColorIndex = themedColorIndex,
          appsCategory = None,
          originalSharedCollectionId = Option(originalSharedCollectionId),
          sharedCollectionId = Option(sharedCollectionId),
          sharedCollectionSubscribed = sharedCollectionSubscribed,
          cards = cards,
          publicCollectionStatus = publicCollectionStatus))

  def createSeqMomentCollection(
    num: Int = 4,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = MomentCollectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: NineCardCategory = appsCategory,
    cards: Seq[Card] = seqCard,
    moment: Option[Moment] = Option(processMoment),
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    publicCollectionStatus: PublicCollectionStatus = publicCollectionStatus) =
    (0 until num) map (
      item =>
        Collection(
          id = id + item,
          position = position,
          name = name,
          collectionType = collectionType,
          icon = icon,
          themedColorIndex = themedColorIndex,
          appsCategory = None,
          cards = cards,
          moment = moment,
          originalSharedCollectionId = Option(originalSharedCollectionId),
          sharedCollectionId = Option(sharedCollectionId),
          sharedCollectionSubscribed = sharedCollectionSubscribed,
          publicCollectionStatus = publicCollectionStatus))

  def createSeqServicesCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: NineCardCategory = appsCategory,
    cards: Seq[ServicesCard] = seqServicesCard,
    moment: Option[ServicesMoment] = Option(servicesMoment),
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed) =
    (0 until num) map (item =>
      ServicesCollection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType.name,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(appsCategory.name),
        cards = cards,
        moment = moment,
        originalSharedCollectionId = Option(originalSharedCollectionId),
        sharedCollectionId = Option(sharedCollectionId),
        sharedCollectionSubscribed = sharedCollectionSubscribed))

  def createSeqCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification) =
    (0 until num) map (item =>
      Card(
        id = id + item,
        position = position,
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = Option(imagePath),
        notification = Option(notification)))

  def createSeqServicesCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification) =
    (1 until num) map (item =>
      ServicesCard(
        id = id + item,
        position = position,
        term = term,
        packageName = Option(packageName),
        cardType = cardType.name,
        intent = intent,
        imagePath = Option(imagePath),
        notification = Option(notification)))

  def createSeqServicesApp(
    num: Int = 5,
    id: Int = appId,
    name: String = name1,
    packageName: String = packageName1,
    className: String = className1,
    category: String = category1,
    imagePath: String = imagePath1,
    dateInstalled: Long = dateInstalled1,
    dateUpdate: Long = dateUpdate1,
    version: String = version1,
    installedFromGooglePlay: Boolean = installedFromGooglePlay1) =
    (1 until num) map (item =>
      ServicesApp(
        id = id + item,
        name = name,
        packageName = packageName,
        className = className,
        category = category,
        dateInstalled = dateInstalled,
        dateUpdate = dateUpdate,
        version = version,
        installedFromGooglePlay = installedFromGooglePlay))

  def createSeqServicesMoment(
    num: Int = 4,
    id: Int = momentId,
    collectionId: Option[Int] = Option(collectionId1),
    timeslot: Seq[ServicesMomentTimeSlot] = createSeqServicesMomentTimeSlot(),
    wifi: Seq[String] = Seq.empty,
    headphone: Boolean = false,
    momentType: Seq[String] = momentType) =
    (0 until num) map (item =>
      ServicesMoment(
        id = id + item,
        collectionId = collectionId,
        timeslot = timeslot,
        wifi = wifi,
        headphone = headphone,
        momentType = Option(momentType(item))))

  def createSeqServicesMomentTimeSlot(
    from: String = from,
    to: String = to,
    days: Seq[Int] = days) =
    (1 until 4) map (item =>
      ServicesMomentTimeSlot(
        from = from,
        to = to,
        days = days))

  def createSeqFormedWidgets(
    num: Int = 5,
    packageName: String = packageName,
    className: String = className,
    startX: Int = startX,
    startY: Int = startX,
    spanX: Int = startX,
    spanY: Int = startX,
    widgetType: WidgetType = AppWidgetType,
    label: Option[String] = None,
    imagePath: Option[String] = None,
    intent: Option[String] = None) =
    (0 until 5) map (
      item =>
        FormedWidget(
          packageName = packageName + item,
          className = className + item,
          startX = startX + item,
          startY = startY + item,
          spanX = spanX + item,
          spanY = spanY + item,
          widgetType = widgetType,
          label = label,
          imagePath = imagePath,
          intent = intent))

  val seqFormedWidgets = createSeqFormedWidgets()

  def createSeqMoment(
    num: Int = 4,
    collectionId: Option[Int] = Option(collectionId1),
    timeslot: Seq[MomentTimeSlot] = createSeqMomentTimeSlot(),
    wifi: Seq[String] = Seq.empty,
    headphone: Boolean = false,
    momentType: Seq[String] = momentType) =
    (0 until num) map (item =>
      SaveMomentRequest(
        collectionId = collectionId,
        timeslot = timeslot,
        wifi = wifi,
        headphone = headphone,
        momentType = Option(NineCardsMoment(momentType(item))),
        widgets = Option(seqFormedWidgets)))

  def createSeqMomentTimeSlot(
    from: String = from,
    to: String = to,
    days: Seq[Int] = days)=
    (1 until 4) map (item =>
      MomentTimeSlot(
        from = from,
        to = to,
        days = days))

  val homeApp =
    App(
      name = name,
      packageName = homeAppPackageName,
      className = className1)

  val workApp =
    App(
      name = name,
      packageName = workAppPackageName,
      className = className1)

  val nightApp =
    App(
      name = name,
      packageName = nightAppPackageName,
      className = className1)

  val transitApp =
    App(
      name = name,
      packageName = transitAppPackageName,
      className = className1)

  val seqCard = createSeqCard()
  val seqServicesCard = createSeqServicesCard()

  val seqCollection = createSeqCollection()
  val collection = seqCollection.headOption
  val seqServicesCollection = createSeqServicesCollection()
  val seqServicesCollection10 = createSeqServicesCollection(num = 10)
  val servicesCollection = seqServicesCollection(0)

  val seqServicesApps = createSeqServicesApp()
  val seqApps = Seq(homeApp, workApp, nightApp, transitApp)
  val seqMomentCollections = createSeqMomentCollection()
  val seqServicesMoments = createSeqServicesMoment()
  val seqServicesMomentsWithoutCollection = createSeqServicesMoment(collectionId = None)
  val servicesMoment = seqServicesMoments(0)
  val servicesMomentWihoutCollection = seqServicesMomentsWithoutCollection(0)
  val seqMoments = createSeqMoment()

  val processMoment = Moment(
    id = 0,
    collectionId = Option(collectionId),
    timeslot = createSeqMomentTimeSlot(),
    wifi = Seq.empty,
    headphone = false,
    momentType = Option(NineCardsMoment(momentType(1))))

  val now = DateTime.now()

  val nowMorning = now.withDayOfWeek(2).withTime(10, 0, 0, 0)
  val nowLateNight = now.withDayOfWeek(2).withTime(3, 0, 0, 0)
  val nowAfternoon = now.withDayOfWeek(2).withTime(18, 0, 0, 0)
  val nowNight = now.withDayOfWeek(2).withTime(21, 0, 0, 0)
  val nowMorningWeekend = now.withDayOfWeek(7).withTime(10, 0, 0, 0)

  val homeMorningId = 1
  val homeMorningCollectionId = Option(1)
  val homeWifi = Seq("homeWifi")
  val homeMorningFrom = "08:00"
  val homeMorningTo = "19:00"
  val homeMorningDays = Seq(1, 1, 1, 1, 1, 1, 1)

  val homeMorningServicesTimeSlot =
    Seq(ServicesMomentTimeSlot(
      from = homeMorningFrom,
      to = homeMorningTo,
      days = homeMorningDays))

  val homeMorningServicesMoment =
    ServicesMoment(
      id = homeMorningId,
      collectionId = homeMorningCollectionId,
      timeslot = homeMorningServicesTimeSlot,
      wifi = homeWifi,
      headphone = false,
      momentType = Option(momentType(0)))

  val homeMorningTimeSlot =
    Seq(MomentTimeSlot(
      from = homeMorningFrom,
      to = homeMorningTo,
      days = homeMorningDays))

  val homeMorningMoment =
    Moment(
      id = homeMorningId,
      collectionId = homeMorningCollectionId,
      timeslot = homeMorningTimeSlot,
      wifi = homeWifi,
      headphone = false,
      momentType = Option(NineCardsMoment(momentType(0))))

  val workId = 2
  val workCollectionId = Option(2)
  val workWifi = Seq("workWifi")
  val workFrom = "08:00"
  val workTo = "17:00"
  val workDays = Seq(0, 1, 1, 1, 1, 1, 0)

  val workServicesTimeSlot =
    Seq(ServicesMomentTimeSlot(
      from = workFrom,
      to = workTo,
      days = workDays))

  val workServicesMoment =
    ServicesMoment(
      id = workId,
      collectionId = workCollectionId,
      timeslot = workServicesTimeSlot,
      wifi = workWifi,
      headphone = false,
      momentType = Option(momentType(1)))

  val workTimeSlot =
    Seq(MomentTimeSlot(
      from = workFrom,
      to = workTo,
      days = workDays))

  val workMoment =
    Moment(
      id = workId,
      collectionId = workCollectionId,
      timeslot = workTimeSlot,
      wifi = workWifi,
      headphone = false,
      momentType = Option(NineCardsMoment(momentType(1))))

  val homeNightId = 3
  val homeNightCollectionId = Option(3)
  val homeNightFrom1 = "19:00"
  val homeNightTo1 = "23:59"
  val homeNightFrom2 = "00:00"
  val homeNightTo2 = "08:00"
  val homeNightDays = Seq(1, 1, 1, 1, 1, 1, 1)

  val homeNightServicesTimeSlot =
    Seq(
      ServicesMomentTimeSlot(
        from = homeNightFrom1,
        to = homeNightTo1,
        days = homeNightDays),
      ServicesMomentTimeSlot(
        from = homeNightFrom2,
        to = homeNightTo2,
        days = homeNightDays))

  val homeNightServicesMoment =
    ServicesMoment(
      id = homeNightId,
      collectionId = homeNightCollectionId,
      timeslot = homeNightServicesTimeSlot,
      wifi = homeWifi,
      headphone = false,
      momentType = Option(momentType(2)))

  val homeNightTimeSlot =
    Seq(
      MomentTimeSlot(
        from = homeNightFrom1,
        to = homeNightTo1,
        days = homeNightDays),
      MomentTimeSlot(
        from = homeNightFrom2,
        to = homeNightTo2,
        days = homeNightDays))

  val homeNightMoment =
    Moment(
      id = homeNightId,
      collectionId = homeNightCollectionId,
      timeslot = homeNightTimeSlot,
      wifi = homeWifi,
      headphone = false,
      momentType = Option(NineCardsMoment(momentType(2))))

  val transitId = 4
  val transitCollectionId = Option(4)
  val transitWifi = Seq.empty
  val transitFrom1 = "00:00"
  val transitTo1 = "23:59"
  val transitDays = Seq(1, 1, 1, 1, 1, 1, 1)

  val transitServicesTimeSlot =
    Seq(
      ServicesMomentTimeSlot(
        from = transitFrom1,
        to = transitTo1,
        days = transitDays))

  val transitServicesMoment =
    ServicesMoment(
      id = transitId,
      collectionId = transitCollectionId,
      timeslot = transitServicesTimeSlot,
      wifi = transitWifi,
      headphone = false,
      momentType = Option(momentType(3)))

  val transitTimeSlot =
    Seq(
      MomentTimeSlot(
        from = transitFrom1,
        to = transitTo1,
        days = transitDays))

  val transitMoment =
    Moment(
      id = transitId,
      collectionId = transitCollectionId,
      timeslot = transitTimeSlot,
      wifi = transitWifi,
      headphone = false,
      momentType = Option(NineCardsMoment(momentType(3))))

  val servicesMomentSeq = Seq(homeMorningServicesMoment, workServicesMoment, homeNightServicesMoment, transitServicesMoment)
  val processMomentSeq = Seq(homeMorningMoment, workMoment, homeNightMoment, transitMoment)

  val seqServicesCollectionForMoments =
    createSeqServicesCollection(num = 1, id = homeMorningCollectionId.get) ++
      createSeqServicesCollection(num = 1, id = homeNightCollectionId.get) ++
      createSeqServicesCollection(num = 1, id = workCollectionId.get) ++
      createSeqServicesCollection(num = 1, id = transitCollectionId.get)

  val seqServicesCollectionForMomentsWithoutId =
    createSeqServicesCollection(num = 1, id = 0) ++
      createSeqServicesCollection(num = 1, id = 0) ++
      createSeqServicesCollection(num = 1, id = 0) ++
      createSeqServicesCollection(num = 1, id = 0)

  val servicesAvailableMomentsSeq = Seq(homeMorningServicesMoment, workServicesMoment, homeNightServicesMoment, transitServicesMoment)

  val mockNineCardMoment: NineCardsMoment = servicesMoment.momentType map (NineCardsMoment(_)) getOrElse NineCardsMoment(momentType(0))

  val processMomentWithoutCollection = Moment(
    id = servicesMomentWihoutCollection.id,
    collectionId = None,
    timeslot = servicesMomentWihoutCollection.timeslot map toTimeSlot,
    wifi = servicesMomentWihoutCollection.wifi,
    headphone = servicesMomentWihoutCollection.headphone,
    momentType = servicesMomentWihoutCollection.momentType map (NineCardsMoment(_)))

  def toTimeSlot(servicesMomentTimeSlot: ServicesMomentTimeSlot): MomentTimeSlot = MomentTimeSlot(
    from = servicesMomentTimeSlot.from,
    to = servicesMomentTimeSlot.to,
    days = servicesMomentTimeSlot.days)

 val processMomentByType = Moment(
   id = servicesMoment.id,
   collectionId = servicesMoment.collectionId,
   timeslot = servicesMoment.timeslot map toTimeSlot,
   wifi = servicesMoment.wifi,
   headphone = servicesMoment.headphone,
   momentType = servicesMoment.momentType map (NineCardsMoment(_)))

}
