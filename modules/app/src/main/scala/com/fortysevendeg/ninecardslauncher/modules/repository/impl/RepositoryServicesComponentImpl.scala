package com.fortysevendeg.ninecardslauncher.modules.repository.impl

import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.AppItem
import com.fortysevendeg.ninecardslauncher.modules.repository._

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RepositoryServicesComponentImpl
  extends RepositoryServicesComponent {

  lazy val repositoryServices = new RepositoryServicesImpl

  class RepositoryServicesImpl
    extends RepositoryServices {

    // TODO  We are using this implementation until the repository component will be finished
    var nCol = 0

    def getNameCollection(col: Int) = col match {
      case 0 => "Contacts"
      case 1 => "Games"
      case 2 => "Home"
      case 3 => "Media"
      case 4 => "Music"
      case 5 => "Night"
      case 6 => "Productivity"
      case 7 => "Social"
      case _ => "Work"
    }

    def getIconCollection(col: Int) = col match {
      case 0 => "icon_collection_contacts"
      case 1 => "icon_collection_games"
      case 2 => "icon_collection_home"
      case 3 => "icon_collection_media"
      case 4 => "icon_collection_music"
      case 5 => "icon_collection_night"
      case 6 => "icon_collection_productivity"
      case 7 => "icon_collection_social"
      case _ => "icon_collection_work"
    }

    def getTypeCollection(col: Int) = col match {
      case 0 => "CONTACTS"
      case 1 => "APPS"
      case 2 => "HOME_MORNING"
      case 3 => "APPS"
      case 4 => "APPS"
      case 5 => "HOME_NIGHT"
      case 6 => "APPS"
      case 7 => "APPS"
      case _ => "WORK"
    }

    def createNewCollection(): Collection = {
      val col = nCol % 9
      val c = Collection(
        id = nCol,
        position = nCol,
        name = getNameCollection(col),
        `type` = getTypeCollection(col),
        icon = getIconCollection(col),
        themedColorIndex = col,
        sharedCollectionSubscribed = false,
        cards = Seq.empty)
      nCol = nCol + 1
      c
    }

    def toCard(app: AppItem) =
      Card(
        id = 1,
        position = 1,
        term = app.name,
        packageName = Some(app.packageName),
        `type` = "APP",
        intent = "",
        imagePath = app.imagePath
      )

    @tailrec
    private def getCollection(apps: Seq[AppItem], collections: Seq[Collection], newCollection: Collection): Seq[Collection] = {
      apps match {
        case Nil if newCollection.cards.length > 0 => collections :+ newCollection
        case Nil => collections
        case h :: t if collections.length == 2 && newCollection.cards.length > 4 => getCollection(t, collections :+ newCollection, createNewCollection())
        case h :: t if newCollection.cards.length > 13 => getCollection(t, collections :+ newCollection, createNewCollection())
        case h :: t => {
          val col = newCollection.copy(cards = newCollection.cards :+ toCard(h))
          getCollection(t, collections, col)
        }
      }
    }

    override def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse] =
      request =>
        Future {
          GetCollectionsResponse(getCollection(request.apps, Seq.empty, createNewCollection()))
        }

  }

}
