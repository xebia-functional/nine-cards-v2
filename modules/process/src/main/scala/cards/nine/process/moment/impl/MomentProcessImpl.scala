package cards.nine.process.moment.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models._
import cards.nine.models.types._
import cards.nine.process.moment._
import cards.nine.services.persistence._
import cards.nine.services.wifi.WifiServices
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants._
import org.joda.time.format.DateTimeFormat

class MomentProcessImpl(
  val persistenceServices: PersistenceServices,
  val wifiServices: WifiServices)
  extends MomentProcess
  with ImplicitsMomentException
  with ImplicitsPersistenceServiceExceptions {

  override def getMoments = persistenceServices.fetchMoments.resolve[MomentException]

  override def getMomentByType(momentType: NineCardsMoment) =
    persistenceServices.getMomentByType(momentType).resolve[MomentException]

  override def fetchMomentByType(momentType: NineCardsMoment) =
    persistenceServices.fetchMomentByType(momentType.name).resolve[MomentException]

  def createMomentWithoutCollection(nineCardsMoment: NineCardsMoment)(implicit context: ContextSupport) = {

    def toMomentData(collectionId: Option[Int], moment: NineCardsMoment): MomentData = {

      def toServicesMomentTimeSlotSeq(moment: NineCardsMoment): Seq[MomentTimeSlot] =
        moment match {
          case HomeMorningMoment => Seq(MomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
          case WorkMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
          case HomeNightMoment => Seq(MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), MomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
          case StudyMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
          case MusicMoment => Seq.empty
          case CarMoment => Seq.empty
          case RunningMoment => Seq.empty
          case BikeMoment => Seq.empty
          case WalkMoment => Seq.empty
        }

      MomentData(
        collectionId = collectionId,
        timeslot = toServicesMomentTimeSlotSeq(moment),
        wifi = Seq.empty,
        headphone = moment == MusicMoment,
        momentType = Option(moment),
        widgets = None)
    }

    (for {
      moment <- persistenceServices.addMoment(toMomentData(None, nineCardsMoment))
    } yield moment).resolve[MomentException]

  }

  override def updateMoment(moment: Moment)(implicit context: ContextSupport) =
    (for {
      _ <- persistenceServices.updateMoment(moment)
    } yield ()).resolve[MomentException]

  override def saveMoments(moments: Seq[MomentData])(implicit context: ContextSupport) =
    (for {
      moments <- persistenceServices.addMoments(moments)
    } yield moments).resolve[MomentException]

  override def deleteMoment(momentId: Int): TaskService[Unit] =
  (for {
    _ <- persistenceServices.deleteMoment(momentId)
  } yield ()).resolve[MomentException]

  override def deleteAllMoments() =
    (for {
      _ <- persistenceServices.deleteAllMoments()
    } yield ()).resolve[MomentException]

  override def getBestAvailableMoment(implicit context: ContextSupport) =
    (for {
      serviceMoments <- persistenceServices.fetchMoments
      collections <- persistenceServices.fetchCollections
      wifi <- wifiServices.getCurrentSSID
      moments = serviceMoments
      momentsPrior = moments sortWith((m1, m2) => prioritizedMoments(m1, m2, wifi))
    } yield momentsPrior.headOption).resolve[MomentException]

  override def getAvailableMoments(implicit context: ContextSupport) =
    (for {
      moments <- persistenceServices.fetchMoments
      collections <- persistenceServices.fetchCollections
      momentWithCollection = moments flatMap {
        case moment @ Moment(_, Some(collectionId), _, _, _, _, _) =>
          collections find (_.id == collectionId) match {
            case Some(collection: Collection) =>
              Some((moment, collection))
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

}
