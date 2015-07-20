package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, NineCardApp}
import com.fortysevendeg.ninecardslauncher.process.collection.{DeviceProcessConfig, CollectionProcess, Conversions}
import com.fortysevendeg.ninecardslauncher.process.commons.CollectionType
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.collection.utils.NineCardAppUtils._
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCollectionRequest, PersistenceServices}

import scala.annotation.tailrec
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

class CollectionProcessImpl(deviceProcessConfig: DeviceProcessConfig, persistenceServices: PersistenceServices)
  extends CollectionProcess
  with Conversions {

  val categories = Seq(game, booksAndReference, business, comics, communication, education,
    entertainment, finance, healthAndFitness, librariesAndDemo, lifestyle, appWallpaper,
    mediaAndVideo, medical, musicAndAudio, newsAndMagazines, personalization, photography,
    productivity, shopping, social, sports, tools, transportation, travelAndLocal, weather, appWidgets)

  override def createCollectionsFromMyDevice(apps: Seq[NineCardApp])(implicit context: ContextSupport): Task[NineCardsException \/ Seq[Collection]] = {
    val tasks = generateAddCollections(apps, categories, Seq.empty) map persistenceServices.addCollection
    Task.gatherUnordered(tasks) map (_.collect { case \/-(collection) => toCollection(collection) }.right[NineCardsException])
  }

  override def getCollections: Task[\/[NineCardsException, Seq[Collection]]] =
    persistenceServices.fetchCollections ▹ eitherT map toCollectionSeq

  @tailrec
  private[this] def generateAddCollections(
    apps: Seq[NineCardApp],
    categories: Seq[String],
    acc: Seq[AddCollectionRequest]): Seq[AddCollectionRequest] = {
    categories match {
      case Nil => acc
      case h :: t =>
        val insert = generateAddCollection(apps, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        generateAddCollections(apps, t, a)
    }
  }

  private[this] def generateAddCollection(apps: Seq[NineCardApp], category: String, index: Int): AddCollectionRequest = {
    val appsCategory = apps.filter(_.category.contains(category)).sortWith(mfIndex(_) < mfIndex(_)).take(numSpaces)
    val pos = if (index >= numSpaces) index % numSpaces else index
    AddCollectionRequest(
      position = pos,
      name = deviceProcessConfig.namesCategories.getOrElse(category.toLowerCase, category.toLowerCase),
      collectionType = CollectionType.apps,
      icon = category.toLowerCase,
      themedColorIndex = pos,
      appsCategory = Some(category),
      sharedCollectionSubscribed = Option(false),
      cards = toCardSeq(appsCategory)
    )
  }

}
