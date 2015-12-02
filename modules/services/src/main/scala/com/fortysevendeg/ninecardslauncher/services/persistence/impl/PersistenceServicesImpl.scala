package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.{CardEntity, AppEntity}
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import com.fortysevendeg.ninecardslauncher.{repository => repo}
import rapture.core.{Answer, Result}

import scalaz.concurrent.Task

class PersistenceServicesImpl(
  appRepository: AppRepository,
  cardRepository: CardRepository,
  collectionRepository: CollectionRepository,
  dockAppRepository: DockAppRepository,
  geoInfoRepository: GeoInfoRepository,
  userRepository: UserRepository)
  extends PersistenceServices
  with Conversions
  with ImplicitsPersistenceServiceExceptions {

  val androidId = "android_id"

  val contentGServices = "content://com.google.android.gsf.gservices"

  override def fetchApps(orderBy: FetchAppOrder, ascending: Boolean = true) = {
    val orderByString = s"${toStringOrderBy(orderBy)} ${toStringDirection(ascending)} ${toSecondaryOrderBy(orderBy)}"

    val appSeq = for {
      apps <- appRepository.fetchApps(orderByString)
    } yield apps map toApp

    appSeq.resolve[PersistenceServiceException]
  }

  override def fetchIterableApps(orderBy: FetchAppOrder, ascending: Boolean = true) = {
    val orderByString = s"${toStringOrderBy(orderBy)} ${toStringDirection(ascending)} ${toSecondaryOrderBy(orderBy)}"

    val appSeq = for {
      iter <- appRepository.fetchIterableApps(orderBy = orderByString)
    } yield new IterableApps(iter)

    appSeq.resolve[PersistenceServiceException]
  }

  override def fetchIterableAppsByKeyword(keyword: String, orderBy: FetchAppOrder, ascending: Boolean = true) = {
    val orderByString = s"${toStringOrderBy(orderBy)} ${toStringDirection(ascending)} ${toSecondaryOrderBy(orderBy)}"

    val appSeq = for {
      iter <- appRepository.fetchIterableApps(
        where = toStringWhere(keyword),
        whereParams = Seq(keyword),
        orderBy = orderByString)
    } yield new IterableApps(iter)

    appSeq.resolve[PersistenceServiceException]
  }

  private[this] def toStringWhere(keyword: String): String = s"${AppEntity.name} LIKE '%$keyword%' "

  private[this] def toStringOrderBy(orderBy: FetchAppOrder): String = orderBy match {
    case OrderByName => s"${AppEntity.name} COLLATE NOCASE"
    case OrderByInstallDate => AppEntity.dateInstalled
    case OrderByCategory => AppEntity.category
  }

  private[this] def toStringDirection(ascending: Boolean): String =
    if (ascending) "ASC" else "DESC"

  private[this] def toSecondaryOrderBy(orderBy: FetchAppOrder): String = orderBy match {
    case OrderByName => ""
    case _ => s", ${AppEntity.name} COLLATE NOCASE ASC"
  }

  override def findAppByPackage(packageName: String) =
    (for {
      app <- appRepository.fetchAppByPackage(packageName)
    } yield app map toApp).resolve[PersistenceServiceException]

  override def addApp(request: AddAppRequest) =
    (for {
      app <- appRepository.addApp(toRepositoryAppData(request))
    } yield toApp(app)).resolve[PersistenceServiceException]

  override def deleteAllApps() =
    (for {
      deleted <- appRepository.deleteApps()
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteAppByPackage(packageName: String) =
    (for {
      deleted <- appRepository.deleteAppByPackage(packageName)
    } yield deleted).resolve[PersistenceServiceException]

  override def updateApp(request: UpdateAppRequest) =
    (for {
      updated <- appRepository.updateApp(toRepositoryApp(request))
    } yield updated).resolve[PersistenceServiceException]

  override def addCard(request: AddCardRequest) =
    request.collectionId match {
      case Some(collectionId) =>
        (for {
          card <- cardRepository.addCard(collectionId, toRepositoryCardData(request))
        } yield toCard(card)).resolve[PersistenceServiceException]
      case None =>
        Service(Task(Result.errata(PersistenceServiceException("CollectionId can't be empty"))))
    }

  override def deleteAllCards() =
    (for {
      deleted <- cardRepository.deleteCards()
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteCard(request: DeleteCardRequest) =
    (for {
      deleted <- cardRepository.deleteCard(toRepositoryCard(request.card))
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteCardsByCollection(collectionId: Int) =
    (for {
      deleted <- cardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId")
    } yield deleted).resolve[PersistenceServiceException]

  override def fetchCardsByCollection(request: FetchCardsByCollectionRequest) =
    (for {
      cards <- cardRepository.fetchCardsByCollection(request.collectionId)
    } yield cards map toCard).resolve[PersistenceServiceException]

  override def fetchCards =
    (for {
      cards <- cardRepository.fetchCards
    } yield cards map toCard).resolve[PersistenceServiceException]

  override def findCardById(request: FindCardByIdRequest) =
    (for {
      maybeCard <- cardRepository.findCardById(request.id)
    } yield maybeCard map toCard).resolve[PersistenceServiceException]

  override def updateCard(request: UpdateCardRequest) =
    (for {
      updated <- cardRepository.updateCard(toRepositoryCard(request))
    } yield updated).resolve[PersistenceServiceException]

  override def addCollection(request: AddCollectionRequest) =
    (for {
      collection <- collectionRepository.addCollection(toRepositoryCollectionData(request))
      addedCards <- addCards(request.cards map (_.copy(collectionId = Option(collection.id))))
    } yield toCollection(collection).copy(cards = addedCards)).resolve[PersistenceServiceException]

  override def deleteAllCollections() =
    (for {
      deleted <- collectionRepository.deleteCollections()
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteCollection(request: DeleteCollectionRequest) = {
    (for {
      deletedCards <- deleteCards(request.collection.cards)
      deletedCollection <- collectionRepository.deleteCollection(toRepositoryCollection(request.collection))
    } yield deletedCollection).resolve[PersistenceServiceException]
  }

  override def fetchCollections =
    (for {
      collectionsWithoutCards <- collectionRepository.fetchSortedCollections
      collectionWithCards <- fetchCards(collectionsWithoutCards)
    } yield collectionWithCards.sortWith(_.position < _.position)).resolve[PersistenceServiceException]

  override def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest) =
    (for {
      collection <- collectionRepository.fetchCollectionBySharedCollectionId(request.sharedCollectionId)
      cards <- fetchCards(collection)
    } yield collection map (toCollection(_, cards))).resolve[PersistenceServiceException]


  override def fetchCollectionByPosition(request: FetchCollectionByPositionRequest) =
    (for {
      collection <- collectionRepository.fetchCollectionByPosition(request.position)
      cards <- fetchCards(collection)
    } yield collection map (toCollection(_, cards))).resolve[PersistenceServiceException]

  override def findCollectionById(request: FindCollectionByIdRequest) =
    (for {
      collection <- collectionRepository.findCollectionById(request.id)
      cards <- fetchCards(collection)
    } yield collection map (toCollection(_, cards))).resolve[PersistenceServiceException]

  override def updateCollection(request: UpdateCollectionRequest) =
    (for {
      updated <- collectionRepository.updateCollection(toRepositoryCollection(request))
    } yield updated).resolve[PersistenceServiceException]

  override def addGeoInfo(request: AddGeoInfoRequest) =
    (for {
      geoInfo <- geoInfoRepository.addGeoInfo(toRepositoryGeoInfoData(request))
    } yield toGeoInfo(geoInfo)).resolve[PersistenceServiceException]

  override def deleteAllGeoInfoItems() =
    (for {
      deleted <- geoInfoRepository.deleteGeoInfoItems()
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteGeoInfo(request: DeleteGeoInfoRequest) =
    (for {
      deleted <- geoInfoRepository.deleteGeoInfo(toRepositoryGeoInfo(request.geoInfo))
    } yield deleted).resolve[PersistenceServiceException]

  override def fetchGeoInfoByConstrain(request: FetchGeoInfoByConstrainRequest) =
    (for {
      maybeGeoInfo <- geoInfoRepository.fetchGeoInfoByConstrain(request.constrain)
    } yield maybeGeoInfo map toGeoInfo).resolve[PersistenceServiceException]

  override def fetchGeoInfoItems =
    (for {
      geoInfoItems <- geoInfoRepository.fetchGeoInfoItems
    } yield geoInfoItems map toGeoInfo).resolve[PersistenceServiceException]

  override def findGeoInfoById(request: FindGeoInfoByIdRequest) =
    (for {
      maybeGeoInfo <- geoInfoRepository.findGeoInfoById(request.id)
    } yield maybeGeoInfo map toGeoInfo).resolve[PersistenceServiceException]

  override def updateGeoInfo(request: UpdateGeoInfoRequest) =
    (for {
      updated <- geoInfoRepository.updateGeoInfo(toRepositoryGeoInfo(request))
    } yield updated).resolve[PersistenceServiceException]

  override def addUser(request: AddUserRequest) =
    (for {
      user <- userRepository.addUser(toRepositoryUserData(request))
    } yield toUser(user)).resolve[PersistenceServiceException]

  override def deleteAllUsers() =
    (for {
      deleted <- userRepository.deleteUsers()
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteUser(request: DeleteUserRequest) =
    (for {
      deleted <- userRepository.deleteUser(toRepositoryUser(request.user))
    } yield deleted).resolve[PersistenceServiceException]

  override def fetchUsers =
    (for {
      userItems <- userRepository.fetchUsers
    } yield userItems map toUser).resolve[PersistenceServiceException]

  override def findUserById(request: FindUserByIdRequest) =
    (for {
      maybeUser <- userRepository.findUserById(request.id)
    } yield maybeUser map toUser).resolve[PersistenceServiceException]

  override def updateUser(request: UpdateUserRequest) =
    (for {
      updated <- userRepository.updateUser(toRepositoryUser(request))
    } yield updated).resolve[PersistenceServiceException]

  override def getAndroidId(implicit context: ContextSupport) =
    Service {
      Task {
        val cursor = Option(context.getContentResolver.query(Uri.parse(contentGServices), null, null, Array(androidId), null))
        val result = cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
        cursor foreach (_.close())
        result map {
          Result.answer[String, AndroidIdNotFoundException]
        } getOrElse Result.errata[String, AndroidIdNotFoundException](AndroidIdNotFoundException(message = "Android Id not found"))
      }
    }

  override def addDockApp(request: AddDockAppRequest) =
    (for {
      dockApp <- dockAppRepository.addDockApp(toRepositoryDockAppData(request))
    } yield toDockApp(dockApp)).resolve[PersistenceServiceException]

  override def deleteAllDockApps() =
    (for {
      deleted <- dockAppRepository.deleteDockApps()
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteDockApp(request: DeleteDockAppRequest) =
    (for {
      deleted <- dockAppRepository.deleteDockApp(toRepositoryDockApp(request.dockApp))
    } yield deleted).resolve[PersistenceServiceException]

  override def fetchDockApps =
    (for {
      dockAppItems <- dockAppRepository.fetchDockApps
    } yield dockAppItems map toDockApp).resolve[PersistenceServiceException]

  override def fetchIterableDockApps =
    (for {
      iter <- dockAppRepository.fetchIterableDockApps()
    } yield new IterableDockApps(iter)).resolve[PersistenceServiceException]

  override def findDockAppById(request: FindDockAppByIdRequest) =
    (for {
      maybeDockApp <- dockAppRepository.findDockAppById(request.id)
    } yield maybeDockApp map toDockApp).resolve[PersistenceServiceException]

  override def updateDockApp(request: UpdateDockAppRequest) =
    (for {
      updated <- dockAppRepository.updateDockApp(toRepositoryDockApp(request))
    } yield updated).resolve[PersistenceServiceException]

  private[this] def addCards(cards: Seq[AddCardRequest]): ServiceDef2[Seq[Card], PersistenceServiceException] = {
    val addedCards = cards map {
      addCard(_).run
    }

    Service(
      Task.gatherUnordered(addedCards) map (
        list =>
          CatchAll[PersistenceServiceException](list.collect { case Answer(card) => card })))
  }

  private[this] def deleteCards(cards: Seq[Card]): ServiceDef2[Int, PersistenceServiceException] = {
    val deletedCards = cards map {
      card =>
        cardRepository.deleteCard(toRepositoryCard(card)).run
    }

    Service(
      Task.gatherUnordered(deletedCards) map (
        list =>
          CatchAll[PersistenceServiceException](list.collect { case Answer(value) => value }.sum)))
  }

  private[this] def fetchCards(maybeCollection: Option[repo.model.Collection]): ServiceDef2[Seq[repo.model.Card], RepositoryException] = {
    maybeCollection match {
      case Some(collection) => cardRepository.fetchCardsByCollection(collection.id)
      case None => Service(Task(Result.answer[Seq[repo.model.Card], RepositoryException](Seq.empty)))
    }
  }

  private[this] def fetchCards(collections: Seq[repo.model.Collection]): ServiceDef2[Seq[Collection], PersistenceServiceException] = {
    val result = collections map {
      collection =>
        (for {
          cards <- cardRepository.fetchCardsByCollection(collection.id)
        } yield toCollection(collection, cards)).run
    }

    Service(
      Task.gatherUnordered(result) map (
        list =>
          CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => collection })))
  }

}
