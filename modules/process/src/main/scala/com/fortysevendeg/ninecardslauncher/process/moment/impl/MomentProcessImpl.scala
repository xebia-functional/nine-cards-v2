package com.fortysevendeg.ninecardslauncher.process.moment.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.moment.DefaultApps._
import com.fortysevendeg.ninecardslauncher.process.moment._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.App

class MomentProcessImpl(
  val momentProcessConfig: MomentProcessConfig,
  val persistenceServices: PersistenceServices)
  extends MomentProcess
  with ImplicitsMomentException
  with ImplicitsPersistenceServiceExceptions
  with MomentConversions {

  override def createMoments(implicit context: ContextSupport) =
    (for {
      collections <- persistenceServices.fetchCollections //TODO - Issue #394 - Change this service's call for a new one to be created that returns the number of created collections
      position = collections.length
      apps <- persistenceServices.fetchApps(OrderByName, ascending = true)
      homeMoment <- createMoment(apps.filter(app => homeApps.contains(app.packageName)).take(numSpaces), HomeMorningMoment, position)
      workMoment <- createMoment(apps.filter(app => workApps.contains(app.packageName)).take(numSpaces), WorkMoment, position + 1)
      nightMoment <- createMoment(apps.filter(app => nightApps.contains(app.packageName)).take(numSpaces), HomeNightMoment, position + 2)
    } yield List(homeMoment, workMoment, nightMoment)).resolve[MomentException]

  override def deleteAllMoments() =
    (for {
      _ <- persistenceServices.deleteAllMoments()
    } yield ()).resolve[MomentException]

  private[this] def createMoment(apps: Seq[App], moment: NineCardsMoment, position: Int) =
    for {
      collection <- persistenceServices.addCollection(generateAddCollection(apps, moment, position))
      _ <- persistenceServices.addMoment(toAddMomentRequest(collection.id, moment))
    } yield toCollection(collection)

  private[this] def generateAddCollection(items: Seq[App], moment: NineCardsMoment, position: Int): AddCollectionRequest = {
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = momentProcessConfig.namesMoments.getOrElse(moment, moment.getStringResource),
      collectionType = moment.name,
      icon = moment.getIconResource,
      themedColorIndex = themeIndex,
      appsCategory = None,
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestSeq(items)
    )
  }
}
