package cards.nine.process.moment.impl

import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.ApplicationData
import cards.nine.models.Spaces._
import cards.nine.models.types.NineCardsMoment._
import cards.nine.models.types._
import cards.nine.process.commons.models.{Collection, Moment, MomentTimeSlot, PrivateCollection}
import cards.nine.process.moment.DefaultApps._
import cards.nine.process.moment.{UpdateMomentRequest, _}
import cards.nine.services.persistence._
import cards.nine.services.wifi.WifiServices
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants._
import org.joda.time.format.DateTimeFormat

import scala.annotation.tailrec

class MomentProcessImpl(
  val momentProcessConfig: MomentProcessConfig,
  val persistenceServices: PersistenceServices,
  val wifiServices: WifiServices)
  extends MomentProcess
  with ImplicitsMomentException
  with ImplicitsPersistenceServiceExceptions
  with MomentConversions {

  override def getMoments = (persistenceServices.fetchMoments map toMomentSeq).resolve[MomentException]

  override def getMomentByType(momentType: NineCardsMoment) =
    (persistenceServices.getMomentByType(momentType.name) map toMoment).resolve[MomentException]

  override def fetchMomentByType(momentType: NineCardsMoment) =
    (persistenceServices.fetchMomentByType(momentType.name) map (moment => moment map toMoment)).resolve[MomentException]

  override def createMoments(implicit context: ContextSupport) =
    (for {
      collections <- persistenceServices.fetchCollections //TODO - Issue #394 - Change this service's call for a new one to be created that returns the number of created collections
      length = collections.length
      servicesApp <- persistenceServices.fetchApps(OrderByName, ascending = true)
      apps = servicesApp map (_.toData)
      collections = moments.zipWithIndex map {
        case (moment, index) =>
          generateAddCollection(filterAppsByMoment(apps, moment), moment, length + index)
      }
      moments <- persistenceServices.addCollections(collections)
    } yield moments map toCollection).resolve[MomentException]

  def createMomentWithoutCollection(nineCardsMoment: NineCardsMoment)(implicit context: ContextSupport) =
    (for {
      moment <- persistenceServices.addMoment(toAddMomentRequest(None, nineCardsMoment))
    } yield toMoment(moment)).resolve[MomentException]

  override def updateMoment(item: UpdateMomentRequest)(implicit context: ContextSupport) =
    (for {
      _ <- persistenceServices.updateMoment(toServiceUpdateMomentRequest(item))
    } yield ()).resolve[MomentException]

  override def saveMoments(items: Seq[SaveMomentRequest])(implicit context: ContextSupport) =
    (for {
      moments <- persistenceServices.addMoments(items map toAddMomentRequest)
    } yield moments map toMoment).resolve[MomentException]

  override def generatePrivateMoments(apps: Seq[ApplicationData], position: Int)(implicit context: ContextSupport) = TaskService {
      CatchAll[MomentException] {
        generatePrivateMomentsCollections(apps, moments, Seq.empty, position)
    }
  }

  override def deleteAllMoments() =
    (for {
      _ <- persistenceServices.deleteAllMoments()
    } yield ()).resolve[MomentException]

  override def getBestAvailableMoment(implicit context: ContextSupport) =
    (for {
      serviceMoments <- persistenceServices.fetchMoments
      collections <- persistenceServices.fetchCollections
      wifi <- wifiServices.getCurrentSSID
      moments = serviceMoments map toMoment
      momentsPrior = moments sortWith((m1, m2) => prioritizedMoments(m1, m2, wifi))
    } yield momentsPrior.headOption).resolve[MomentException]

  override def getAvailableMoments(implicit context: ContextSupport) =
    (for {
      serviceMoments <- persistenceServices.fetchMoments
      serviceCollections <- persistenceServices.fetchCollections
      collections = serviceCollections map toCollection
      moments = serviceMoments map toMoment
      momentWithCollection = moments flatMap {
        case moment @ Moment(_, Some(collectionId), _, _, _, _) =>
          collections find (_.id == collectionId) match {
            case Some(collection: Collection) =>
              Some(toMomentWithCollection(moment, collection))
            case _ => None
          }
        case _ => None
      }
    } yield momentWithCollection).resolve[MomentException]

  private[this] def prioritizedMoments(moment1: Moment, moment2: Moment, wifi: Option[String]): Boolean = {

    val now = getNowDateTime

    def prioritizedByTime(): Boolean = {
      val sum1 = (moment1.timeslot map { slot =>
        val (fromSlot, toSlot) = toDateTime(now, slot)
        toSlot.getMillis - fromSlot.getMillis
      }).sum
      val sum2 = (moment2.timeslot map { slot =>
        val (fromSlot, toSlot) = toDateTime(now, slot)
        toSlot.getMillis - fromSlot.getMillis
      }).sum
      sum1 < sum2
    }

    (isHappening(moment1, now), isHappening(moment2, now), wifi) match {
      case (h1, h2, Some(w)) if h1 == h2 && moment1.wifi.contains(w) => true
      case (h1, h2, Some(w)) if h1 == h2 && moment2.wifi.contains(w) => false
      case (h1, h2, None) if h1 == h2 && moment1.wifi.isEmpty && moment2.wifi.nonEmpty => true
      case (h1, h2, None) if h1 == h2 && moment1.wifi.nonEmpty && moment2.wifi.isEmpty => false
      case (h1, h2, Some(w)) if h1 == h2 && moment1.wifi.isEmpty && moment2.wifi.nonEmpty => true
      case (h1, h2, Some(w)) if h1 == h2 && moment1.wifi.nonEmpty && moment2.wifi.isEmpty => false
      case (true, false, _) => true
      case (false, true, _) => false
      case (h1, h2, _) if h1 == h2 => prioritizedByTime()
      case _ => false
    }
  }

  private[this] def isHappening(moment: Moment, now: DateTime): Boolean = moment.timeslot exists { slot =>
    val (fromSlot, toSlot) = toDateTime(now, slot)
    fromSlot.isBefore(now) && toSlot.isAfter(now) && slot.days.lift(getDayOfWeek(now)).contains(1)
  }

  protected def getNowDateTime = DateTime.now()

  protected def getDayOfWeek(now: DateTime) =
    now.getDayOfWeek match {
      case SUNDAY => 0
      case MONDAY => 1
      case TUESDAY => 2
      case WEDNESDAY => 3
      case THURSDAY => 4
      case FRIDAY => 5
      case SATURDAY => 6
    }

  private[this] def toDateTime(now: DateTime, timeslot: MomentTimeSlot): (DateTime, DateTime) = {

    val formatter = DateTimeFormat.forPattern("HH:mm")

    val from = formatter.parseDateTime(timeslot.from)
    val to = formatter.parseDateTime(timeslot.to)

    val fromDT = now.withTime(from.getHourOfDay, from.getMinuteOfHour, 0, 0)
    val toDT = now.withTime(to.getHourOfDay, to.getMinuteOfHour, 0, 0)

    (fromDT, toDT)
  }

  private[this] def filterAppsByMoment(apps: Seq[ApplicationData], moment: NineCardsMoment): Seq[ApplicationData] =
    apps.filter { app =>
      moment match {
        case HomeMorningMoment => homeApps.contains(app.packageName)
        case WorkMoment => workApps.contains(app.packageName)
        case HomeNightMoment => nightApps.contains(app.packageName)
        case TransitMoment => transitApps.contains(app.packageName)
      }
    }.take(numSpaces)

  private[this] def generateAddCollection(items: Seq[ApplicationData], moment: NineCardsMoment, position: Int): AddCollectionRequest = {
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = momentProcessConfig.namesMoments.getOrElse(moment, moment.getStringResource),
      collectionType = MomentCollectionType.name,
      icon = moment.getIconResource,
      themedColorIndex = themeIndex,
      appsCategory = None,
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestSeq(items),
      moment = Option(toAddMomentRequest(None, moment)))
  }

  @tailrec
  private[this] def generatePrivateMomentsCollections(
    items: Seq[ApplicationData],
    moments: Seq[NineCardsMoment],
    acc: Seq[PrivateCollection],
    position: Int): Seq[PrivateCollection] = moments match {
    case Nil => acc
    case h :: t =>
      val insert = generatePrivateMomentsCollection(items, h, acc.length + position + 1)
      val a = if (insert.cards.nonEmpty) acc :+ insert else acc
      generatePrivateMomentsCollections(items, t, a, position)
  }

  private[this] def generatePrivateMomentsCollection(items: Seq[ApplicationData], moment: NineCardsMoment, position: Int): PrivateCollection = {
    val appsByMoment = filterAppsByMoment(items, moment)
    val themeIndex = if (position >= numSpaces) position % numSpaces else position

    PrivateCollection(
      name = momentProcessConfig.namesMoments.getOrElse(moment, moment.getStringResource),
      collectionType = MomentCollectionType,
      icon = moment.getIconResource,
      themedColorIndex = themeIndex,
      appsCategory = None,
      cards = appsByMoment map toPrivateCard,
      moment = Some(moment)
    )
  }


}
