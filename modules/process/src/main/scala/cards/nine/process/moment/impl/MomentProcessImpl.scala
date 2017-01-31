/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.process.moment.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models._
import cards.nine.models.types._
import cards.nine.process.moment._
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.persistence._
import cards.nine.services.connectivity.ConnectivityServices
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants._
import org.joda.time.format.DateTimeFormat

class MomentProcessImpl(
    val persistenceServices: PersistenceServices,
    val connectivityServices: ConnectivityServices,
    val awarenessServices: AwarenessServices)
    extends MomentProcess
    with ImplicitsMomentException
    with ImplicitsPersistenceServiceExceptions {

  override def getMoments = persistenceServices.fetchMoments.resolve[MomentException]

  override def getMomentByCollectionId(collectionId: Int) =
    persistenceServices.getMomentByCollectionId(collectionId).resolve[MomentException]

  override def getMomentByType(momentType: NineCardsMoment) =
    persistenceServices.getMomentByType(momentType).resolve[MomentException]

  override def fetchMomentByType(momentType: NineCardsMoment) =
    persistenceServices.fetchMomentByType(momentType.name).resolve[MomentException]

  override def findMoment(momentId: Int) =
    persistenceServices.fetchMomentById(momentId).resolve[MomentException]

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
      fromSlot.isBefore(now) && toSlot
        .isAfter(now) && slot.days.lift(getDayOfWeek(now)).contains(1)
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
        case (true, false)        => true
        case (false, true)        => false
        case (h1, h2) if h1 == h2 => prioritizedByTime()
        case _                    => false
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
      connectivityServices.getCurrentSSID.map {
        case Some(ssid) =>
          (moments filter (_.wifi.contains(ssid)) sortWith prioritizedMomentsByTime).headOption
        case None => None
      }

    def bluetoothMoment(moments: Seq[Moment]): TaskService[Option[Moment]] =
      connectivityServices.getBluetoothConnected.map { devices =>
        (moments filter (_.bluetooth
          .exists(b => devices.contains(b))) sortWith prioritizedMomentsByTime).headOption
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
          TaskService.right(
            activityMoments.find(tuple => activityMatch(tuple._1, activity)).map(_._2))
        case _ =>
          awarenessServices.getTypeActivity.resolveLeftTo(ProbablyActivity(UnknownActivity)).map {
            activity =>
              activityMoments
                .find(tuple => activityMatch(tuple._1, activity.activityType))
                .map(_._2)
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
            bluetooth = Seq.empty,
            headphone = false,
            momentType = NineCardsMoment.defaultMoment)
          persistenceServices.addMoment(momentData)
      }
    }

    def bestChoice(moments: Seq[Moment]): TaskService[Option[Moment]] = {
      val momentsToEvaluate = moments.filterNot(_.momentType.isDefault)
      Seq(headphonesMoment(_), bluetoothMoment(_), wifiMoment(_), activityMoment(_), hourMoment(_))
        .foldLeft[TaskService[Option[Moment]]](TaskService.right(None)) { (s1, s2) =>
          s1.flatMap(maybeMoment =>
            if (maybeMoment.isDefined) TaskService.right(maybeMoment) else s2(momentsToEvaluate))
        }
    }

    def checkEmptyMoments(moments: Seq[Moment]): TaskService[Option[Moment]] =
      if (moments.nonEmpty) {
        for {
          maybeBestMoment <- bestChoice(moments)
          moment          <- maybeBestMoment map TaskService.right getOrElse defaultMoment(moments)
        } yield Option(moment)
      } else TaskService.right(None)

    for {
      moments     <- persistenceServices.fetchMoments
      maybeMoment <- checkEmptyMoments(moments)
    } yield maybeMoment
  }

  protected def getNowDateTime = DateTime.now()

  protected def getDayOfWeek(now: DateTime) =
    now.getDayOfWeek match {
      case SUNDAY    => 0
      case MONDAY    => 1
      case TUESDAY   => 2
      case WEDNESDAY => 3
      case THURSDAY  => 4
      case FRIDAY    => 5
      case SATURDAY  => 6
    }

  private[this] def toDateTime(now: DateTime, timeslot: MomentTimeSlot): (DateTime, DateTime) = {

    val formatter = DateTimeFormat.forPattern("HH:mm")

    val from = formatter.parseDateTime(timeslot.from)
    val to   = formatter.parseDateTime(timeslot.to)

    val fromDT = now.withTime(from.getHourOfDay, from.getMinuteOfHour, 0, 0)
    val toDT   = now.withTime(to.getHourOfDay, to.getMinuteOfHour, 0, 0)

    (fromDT, toDT)
  }

}
