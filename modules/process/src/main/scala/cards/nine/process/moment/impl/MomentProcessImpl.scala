package cards.nine.process.moment.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{types, _}
import cards.nine.models.types._
import cards.nine.process.moment._
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.persistence._
import cards.nine.services.wifi.WifiServices
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants._
import org.joda.time.format.DateTimeFormat

class MomentProcessImpl(
  val persistenceServices: PersistenceServices,
  val wifiServices: WifiServices,
  val awarenessServices: AwarenessServices)
  extends MomentProcess
  with ImplicitsMomentException
  with ImplicitsPersistenceServiceExceptions {

  override def getMoments = persistenceServices.fetchMoments.resolve[MomentException]

  override def getMomentByType(momentType: NineCardsMoment) =
    persistenceServices.getMomentByType(momentType).resolve[MomentException]

  override def fetchMomentByType(momentType: NineCardsMoment) =
    persistenceServices.fetchMomentByType(momentType.name).resolve[MomentException]

  override def findMoment(momentId: Int) =
    persistenceServices.fetchMomentById(momentId).resolve[MomentException]

  def createMomentWithoutCollection(nineCardsMoment: NineCardsMoment)(implicit context: ContextSupport) = {

    def toMomentData(moment: NineCardsMoment): TaskService[MomentData] = {

      def toServicesMomentTimeSlotSeq(moment: NineCardsMoment): TaskService[Seq[MomentTimeSlot]] =
        moment match {
          case HomeMorningMoment => TaskService.right(Seq(MomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1))))
          case WorkMoment => TaskService.right(Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0))))
          case HomeNightMoment => TaskService.right(Seq(MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), MomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1))))
          case StudyMoment => TaskService.right(Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0))))
          case MusicMoment => TaskService.right(Seq.empty)
          case CarMoment => TaskService.right(Seq.empty)
          case SportsMoment => TaskService.right(Seq.empty)
          case OutAndAboutMoment => TaskService.right(Seq.empty)
          case UnknownMoment(value) => TaskService.left(MomentException(s"Invalid moment type '$value'"))
        }

      toServicesMomentTimeSlotSeq(moment).map { timeSlot =>
        MomentData(
          collectionId = None,
          timeslot = timeSlot,
          wifi = Seq.empty,
          headphone = moment == MusicMoment,
          momentType = moment,
          widgets = None)
      }
    }

    for {
      momentData <- toMomentData(nineCardsMoment)
      moment <- persistenceServices.addMoment(momentData).resolve[MomentException]
    } yield moment

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

  def getBestAvailableMoment(
    maybeHeadphones: Option[Boolean] = None,
    maybeActivity: Option[KindActivity] = None)(implicit context: ContextSupport) = {

    val now = getNowDateTime

    def isHappening(moment: Moment): Boolean = moment.timeslot exists { slot =>
      val (fromSlot, toSlot) = toDateTime(now, slot)
      fromSlot.isBefore(now) && toSlot.isAfter(now) && slot.days.lift(getDayOfWeek(now)).contains(1)
    }

    def prioritizedMomentsByTime(moment1: Moment, moment2: Moment): Boolean = {

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

      (isHappening(moment1), isHappening(moment2)) match {
        case (true, false) => true
        case (false, true) => false
        case (h1, h2) if h1 == h2 => prioritizedByTime()
        case _ => false
      }
    }

    def headphonesMoment(moments: Seq[Moment]): TaskService[Option[Moment]] =
      (moments.find(_.momentType == MusicMoment), maybeHeadphones) match {
        case (Some(m), Some(hp)) => TaskService.right(if (hp) Some(m) else None)
        case (Some(m), None) =>
          awarenessServices.getHeadphonesState
            .resolveLeftTo(Headphones(false))
            .map(headphones => if (headphones.connected) Some(m) else None)
        case (None, _) => TaskService.right(None)
      }

    def wifiMoment(moments: Seq[Moment]): TaskService[Option[Moment]] =
      wifiServices.getCurrentSSID.map {
        case Some(ssid) => (moments filter(_.wifi.contains(ssid)) sortWith prioritizedMomentsByTime).headOption
        case None => None
      }

    def activityMoment(moments: Seq[Moment]): TaskService[Option[Moment]] = {

      def activityMatch(momentType: NineCardsMoment, activity: KindActivity): Boolean =
        momentType == CarMoment && activity == InVehicleActivity

      val activityMoments = moments
        .map(moment => (moment.momentType, moment))
        .filter(tuple => NineCardsMoment.activityMoments.contains(tuple._1))

      (activityMoments.isEmpty, maybeActivity) match {
        case (true, _) => TaskService.right(None)
        case (false, Some(activity)) =>
          TaskService.right(activityMoments.find(tuple => activityMatch(tuple._1, activity)).map(_._2))
        case _ =>
          awarenessServices.getTypeActivity
            .resolveLeftTo(ProbablyActivity(UnknownActivity))
            .map { activity =>
              activityMoments.find(tuple => activityMatch(tuple._1, activity.activityType)).map(_._2)
            }
      }
    }

    def hourMoment(moments: Seq[Moment]): TaskService[Option[Moment]] = TaskService.right {
      (moments.filter { moment =>
        moment.wifi.isEmpty &&
          NineCardsMoment.hourlyMoments.contains(moment.momentType) &&
          isHappening(moment)
      } sortWith prioritizedMomentsByTime).headOption
    }

    def defaultMoment(moments: Seq[Moment]): TaskService[Moment] = {
      moments.find(_.momentType.isDefault) match {
        case Some(moment) => TaskService.right(moment)
        case _ =>
          val momentData = MomentData(
            collectionId = None,
            timeslot = Seq.empty,
            wifi = Seq.empty,
            headphone = false,
            momentType = NineCardsMoment.defaultMoment)
          persistenceServices.addMoment(momentData)
      }
    }

    def bestChoice(moments: Seq[Moment]): TaskService[Option[Moment]] = {
      val momentsToEvaluate = moments.filterNot(_.momentType.isDefault)
      Seq(headphonesMoment(_), wifiMoment(_), activityMoment(_), hourMoment(_))
        .foldLeft[TaskService[Option[Moment]]](TaskService.right(None)) { (s1, s2) =>
        s1.flatMap(maybeMoment => if (maybeMoment.isDefined) TaskService.right(maybeMoment) else s2(momentsToEvaluate))
      }
    }

    def checkEmptyMoments(moments: Seq[Moment]): TaskService[Option[Moment]] =
      if (moments.nonEmpty) {
        for {
          maybeBestMoment <- bestChoice(moments)
          moment <- maybeBestMoment map TaskService.right getOrElse defaultMoment(moments)
        } yield Option(moment)
      } else TaskService.right(None)

    for {
      moments <- persistenceServices.fetchMoments
      maybeMoment <- checkEmptyMoments(moments)
    } yield maybeMoment
  }


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
