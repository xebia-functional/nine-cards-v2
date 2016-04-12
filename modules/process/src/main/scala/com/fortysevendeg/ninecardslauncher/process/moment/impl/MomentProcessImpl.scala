package com.fortysevendeg.ninecardslauncher.process.moment.impl

import android.util.Log
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{MomentTimeSlot, Moment, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.CollectionType._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment._
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.moment.DefaultApps._
import com.fortysevendeg.ninecardslauncher.process.moment._
import com.fortysevendeg.ninecardslauncher.process.moment.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.wifi.{WifiServicesException, WifiServices}
import org.joda.time.format.DateTimeFormat
import rapture.core.Answer
import org.joda.time.DateTime

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
        generatePrivateMomentsCollections(apps, momentsCollectionTypes, Seq.empty, position)
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
    } yield {
      val moments = serviceMoments map toMoment

      val moment = wifi match {
        case Some(w) => filterMoments(moments.filter(moment => moment.wifi.contains(w)))
        case None => filterMoments(moments)
      }

      moment.headOption
    }).resolve[MomentException]

  private[this] def filterMoments(moments: Seq[Moment]) =
    moments.filter(filterMomentByDateTime)

  private[this] def filterMomentByDateTime(moment: Moment) = {

    val now = DateTime.now()

    val dayOfWeek = moment.timeslot.headOption match {
      case Some(t) => t.days(now.getDayOfWeek)
      case None => throw new MomentException("")
    }

    val timeslot = moment.timeslot map (timeslot => toDateTime(now, timeslot))

    val (from, to) = timeslot.headOption match {
      case Some((f, t)) => (f, t)
      case None => throw new MomentException("")
    }

    val (from1, to1) = timeslot.lift(1) match {
      case Some((f, t)) => (f, t)
      case None => throw new MomentException("")
    }

    (((now.isAfter(from) && now.isBefore(to))
      || (if (timeslot.size > 1) now.isAfter(from1) && now.isBefore(to1) else false))
      && dayOfWeek == 1)
  }

  private[this] def toDateTime(now: DateTime, timeslot: MomentTimeSlot) = {

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
    val collectionType = moment match{
      case HomeMorningMoment => HomeMorningCollectionType
      case WorkMoment => WorkCollectionType
      case HomeNightMoment => HomeNightCollectionType
    }
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = momentProcessConfig.namesMoments.getOrElse(moment, moment.getStringResource),
      collectionType = collectionType.name,
      icon = moment.getIconResource,
      themedColorIndex = themeIndex,
      appsCategory = None,
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestSeq(items),
      moment = Option(toAddMomentRequest(moment)))
  }

  @tailrec
  private[this] def generatePrivateMomentsCollections(
    items: Seq[App],
    collectionTypes: Seq[CollectionType],
    acc: Seq[PrivateCollection],
    position: Int): Seq[PrivateCollection] = collectionTypes match {
    case Nil => acc
    case h :: t =>
      val insert = generatePrivateMomentsCollection(items, h, acc.length + position + 1)
      val a = if (insert.cards.nonEmpty) acc :+ insert else acc
      generatePrivateMomentsCollections(items, t, a, position)
  }

  private[this] def generatePrivateMomentsCollection(items: Seq[App], collectionType: CollectionType, position: Int): PrivateCollection = {
    val moment = collectionType match {
      case HomeMorningCollectionType => HomeMorningMoment
      case WorkCollectionType => WorkMoment
      case HomeNightCollectionType => HomeNightMoment
    }
    val appsByMoment = filterAppsByMoment(items, moment)
    val themeIndex = if (position >= numSpaces) position % numSpaces else position

    PrivateCollection(
      name = momentProcessConfig.namesMoments.getOrElse(moment, moment.getStringResource),
      collectionType = collectionType,
      icon = moment.getIconResource,
      themedColorIndex = themeIndex,
      appsCategory = None,
      cards = appsByMoment map toPrivateCard
    )
  }


}
