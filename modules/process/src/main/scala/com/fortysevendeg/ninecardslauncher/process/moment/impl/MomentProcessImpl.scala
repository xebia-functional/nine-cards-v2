package com.fortysevendeg.ninecardslauncher.process.moment.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Moment, MomentTimeSlot, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment._
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.moment.DefaultApps._
import com.fortysevendeg.ninecardslauncher.process.moment._
import com.fortysevendeg.ninecardslauncher.process.moment.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.wifi.WifiServices
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants._
import org.joda.time.format.DateTimeFormat
import rapture.core.Answer

import scala.annotation.tailrec
import scalaz.concurrent.Task

class MomentProcessImpl(
  val momentProcessConfig: MomentProcessConfig,
  val persistenceServices: PersistenceServices,
  val wifiServices: WifiServices)
  extends MomentProcess
  with ImplicitsMomentException
  with ImplicitsPersistenceServiceExceptions
  with MomentConversions {

  override def getMoments: ServiceDef2[Seq[Moment], MomentException] = (persistenceServices.fetchMoments map toMomentSeq).resolve[MomentException]

  override def createMoments(implicit context: ContextSupport) =
    (for {
      collections <- persistenceServices.fetchCollections //TODO - Issue #394 - Change this service's call for a new one to be created that returns the number of created collections
      position = collections.length
      servicesApp <- persistenceServices.fetchApps(OrderByName, ascending = true)
      moments <- createMoments(servicesApp map toApp, position)
    } yield moments).resolve[MomentException]

  override def saveMoments(items: Seq[Moment])(implicit context: ContextSupport) = Service {
      val tasks = items map (item => persistenceServices.addMoment(toAddMomentRequest(item)).run)
      Task.gatherUnordered(tasks) map (c => CatchAll[MomentException](c.collect { case Answer(m) => toMoment(m)}))
    }

  override def generatePrivateMoments(apps: Seq[App], position: Int)(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[MomentException] {
        generatePrivateMomentsCollections(apps, moments, Seq.empty, position)
      }
    }
  }

  override def deleteAllMoments() =
    (for {
      _ <- persistenceServices.deleteAllMoments()
    } yield ()).resolve[MomentException]

  override def getBestAvailableMoment(implicit context: ContextSupport) =
    (for {
      serviceMoments <- persistenceServices.fetchMoments
      wifi <- wifiServices.getCurrentSSID
      moments = serviceMoments map toMoment
      momentsPrior = moments sortWith((m1, m2) => prioritizedMoments(m1, m2, wifi))
    } yield momentsPrior.headOption).resolve[MomentException]

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

  private[this] def filterAppsByMoment(apps: Seq[App], moment: NineCardsMoment) =
    apps.filter { app =>
      moment match {
        case HomeMorningMoment => homeApps.contains(app.packageName)
        case WorkMoment => workApps.contains(app.packageName)
        case HomeNightMoment => nightApps.contains(app.packageName)
      }
    }.take(numSpaces)

  private[this] def createMoments(apps: Seq[App], position: Int) = Service {
    val tasks = moments.indices map (item => createMoment(filterAppsByMoment(apps, moments(item)),  moments(item), position + item).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[MomentException](c.collect { case Answer(r) => r}))
  }

  private[this] def createMoment(apps: Seq[App], moment: NineCardsMoment, position: Int) =
    (for {
      collection <- persistenceServices.addCollection(generateAddCollection(apps, moment, position))
    } yield toCollection(collection)).resolve[MomentException]

  private[this] def generateAddCollection(items: Seq[App], moment: NineCardsMoment, position: Int): AddCollectionRequest = {
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
    items: Seq[App],
    moments: Seq[NineCardsMoment],
    acc: Seq[PrivateCollection],
    position: Int): Seq[PrivateCollection] = moments match {
    case Nil => acc
    case h :: t =>
      val insert = generatePrivateMomentsCollection(items, h, acc.length + position + 1)
      val a = if (insert.cards.nonEmpty) acc :+ insert else acc
      generatePrivateMomentsCollections(items, t, a, position)
  }

  private[this] def generatePrivateMomentsCollection(items: Seq[App], moment: NineCardsMoment, position: Int): PrivateCollection = {
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
