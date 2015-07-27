package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import java.io.File

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.{repository => repo}
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import com.fortysevendeg.ninecardslauncher.services.utils.FileUtils

import scala.util.{Failure, Success}
import scalaz._
import Scalaz._
import EitherT._
import scalaz.concurrent.Task
import Service._

class PersistenceServicesImpl(
  cacheCategoryRepository: CacheCategoryRepository,
  cardRepository: CardRepository,
  collectionRepository: CollectionRepository,
  geoInfoRepository: GeoInfoRepository)
  extends PersistenceServices
  with Conversions
  with FileUtils {

  // TODO These contants don't should be here

  val AccountType = "com.google"

  val AndroidId = "android_id"

  val ContentGServices = "content://com.google.android.gsf.gservices"

  val FilenameUser = "__user_entity__"

  val FilenameInstallation = "__installation_entity__"

  override def addCacheCategory(request: AddCacheCategoryRequest): Task[NineCardsException \/ CacheCategory] =
    cacheCategoryRepository.addCacheCategory(toRepositoryCacheCategoryData(request)) ▹ eitherT map toCacheCategory

  override def deleteCacheCategory(request: DeleteCacheCategoryRequest): Task[NineCardsException \/ Int] =
    cacheCategoryRepository.deleteCacheCategory(toRepositoryCacheCategory(request.cacheCategory))

  override def deleteCacheCategoryByPackage(request: DeleteCacheCategoryByPackageRequest): Task[NineCardsException \/ Int] =
    cacheCategoryRepository.deleteCacheCategoryByPackage(request.packageName)

  override def fetchCacheCategoryByPackage(request: FetchCacheCategoryByPackageRequest): Task[NineCardsException \/ Option[CacheCategory]] =
    cacheCategoryRepository.fetchCacheCategoryByPackage(request.packageName) ▹ eitherT map {
      _ map toCacheCategory
    }

  override def fetchCacheCategories: Task[NineCardsException \/ Seq[CacheCategory]] =
    cacheCategoryRepository.fetchCacheCategories ▹ eitherT map toCacheCategorySeq

  override def findCacheCategoryById(request: FindCacheCategoryByIdRequest): Task[NineCardsException \/ Option[CacheCategory]] =
    cacheCategoryRepository.findCacheCategoryById(request.id) ▹ eitherT map {
      _ map toCacheCategory
    }

  override def updateCacheCategory(request: UpdateCacheCategoryRequest): Task[NineCardsException \/ Int] =
    cacheCategoryRepository.updateCacheCategory(toRepositoryCacheCategory(request))

  override def addCard(request: AddCardRequest): Task[NineCardsException \/ Card] =
    request.collectionId match {
      case Some(collectionId) => cardRepository.addCard(collectionId, toRepositoryCardData(request)) ▹ eitherT map toCard
      case None => Task { -\/(NineCardsException("CollectionId can't be empty")) }
    }

  override def deleteCard(request: DeleteCardRequest): Task[NineCardsException \/ Int] =
    cardRepository.deleteCard(toRepositoryCard(request.card))


  override def fetchCardsByCollection(request: FetchCardsByCollectionRequest): Task[NineCardsException \/ Seq[Card]] =
    cardRepository.fetchCardsByCollection(request.collectionId) ▹ eitherT map {
      _ map toCard
    }

  override def findCardById(request: FindCardByIdRequest): Task[NineCardsException \/ Option[Card]] =
    cardRepository.findCardById(request.id) ▹ eitherT map {
      _ map toCard
    }

  override def updateCard(request: UpdateCardRequest): Task[NineCardsException \/ Int] =
    cardRepository.updateCard(toRepositoryCard(request))

  override def addCollection(request: AddCollectionRequest): Task[NineCardsException \/ Collection] =
    for {
      collection <- collectionRepository.addCollection(toRepositoryCollectionData(request)) ▹ eitherT
      addedCards <- addCards(request.cards map (_.copy(collectionId = Option(collection.id)))) ▹ eitherT
    } yield toCollection(collection).copy(cards = addedCards)

  override def deleteCollection(request: DeleteCollectionRequest): Task[NineCardsException \/ Int] = {
    for {
      deletedCards <- deleteCards(request.collection.cards) ▹ eitherT
      deletedCollection <- collectionRepository.deleteCollection(toRepositoryCollection(request.collection)) ▹ eitherT
    } yield deletedCollection
  }

  override def fetchCollections: Task[NineCardsException \/ Seq[Collection]] =
    for {
      collectionsWithoutCards <- collectionRepository.fetchSortedCollections ▹ eitherT
      collectionWithCards <- fetchCards(collectionsWithoutCards) ▹ eitherT
    } yield collectionWithCards.sortWith(_.position < _.position)

  override def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest): Task[NineCardsException \/ Option[Collection]] =
    for {
      collection <- collectionRepository.fetchCollectionBySharedCollectionId(request.sharedCollectionId) ▹ eitherT
      cards <- fetchCards(collection) ▹ eitherT
    } yield collection map (toCollection(_, cards))


  override def fetchCollectionByPosition(request: FetchCollectionByPositionRequest): Task[NineCardsException \/ Option[Collection]] =
    for {
      collection <- collectionRepository.fetchCollectionByPosition(request.position) ▹ eitherT
      cards <- fetchCards(collection) ▹ eitherT
    } yield collection map (toCollection(_, cards))

  override def findCollectionById(request: FindCollectionByIdRequest): Task[NineCardsException \/ Option[Collection]] = {
    for {
      collection <- collectionRepository.findCollectionById(request.id) ▹ eitherT
      cards <- fetchCards(collection) ▹ eitherT
    } yield collection map (toCollection(_, cards))
  }

  override def updateCollection(request: UpdateCollectionRequest): Task[NineCardsException \/ Int] =
    collectionRepository.updateCollection(toRepositoryCollection(request))

  override def addGeoInfo(request: AddGeoInfoRequest): Task[NineCardsException \/ GeoInfo] =
    geoInfoRepository.addGeoInfo(toRepositoryGeoInfoData(request)) ▹ eitherT map toGeoInfo

  override def deleteGeoInfo(request: DeleteGeoInfoRequest): Task[NineCardsException \/ Int] =
    geoInfoRepository.deleteGeoInfo(toRepositoryGeoInfo(request.geoInfo))

  override def fetchGeoInfoByConstrain(request: FetchGeoInfoByConstrainRequest): Task[NineCardsException \/ Option[GeoInfo]] =
    geoInfoRepository.fetchGeoInfoByConstrain(request.constrain) ▹ eitherT map {
      _ map toGeoInfo
    }

  override def fetchGeoInfoItems: Task[NineCardsException \/ Seq[GeoInfo]] =
    geoInfoRepository.fetchGeoInfoItems ▹ eitherT map toGeoInfoSeq

  override def findGeoInfoById(request: FindGeoInfoByIdRequest): Task[NineCardsException \/ Option[GeoInfo]] =
    geoInfoRepository.findGeoInfoById(request.id) ▹ eitherT map {
      _ map toGeoInfo
    }

  override def updateGeoInfo(request: UpdateGeoInfoRequest): Task[NineCardsException \/ Int] =
    geoInfoRepository.updateGeoInfo(toRepositoryGeoInfo(request))

  private[this] def addCards(cards: Seq[AddCardRequest]): Task[NineCardsException \/ Seq[Card]] = {
    val addedCards = cards map addCard
    Task.gatherUnordered(addedCards) map (_.collect { case \/-(card) => card }.right[NineCardsException])
  }

  private[this] def deleteCards(cards: Seq[Card]): Task[NineCardsException \/ Int] = {
    val deletedCards = cards map {
      card =>
        cardRepository.deleteCard(toRepositoryCard(card))
    }

    Task.gatherUnordered(deletedCards) map (_.collect { case \/-(value) => value }.sum.right[NineCardsException])
  }

  private[this] def fetchCards(maybeCollection: Option[repo.model.Collection]) = {
    maybeCollection match {
      case Some(collection) => cardRepository.fetchCardsByCollection(collection.id)
      case None => Task(\/-(Seq.empty))
    }
  }

  private[this] def fetchCards(collections: Seq[repo.model.Collection]): Task[NineCardsException \/ Seq[Collection]] = {
    val result = collections map { collection =>
      toDisjunctionTask {
        for {
          cards <- cardRepository.fetchCardsByCollection(collection.id) ▹ eitherT
        } yield toCollection(collection, cards)
      }
    }

    Task.gatherUnordered(result) map (_.collect { case \/-(collection) => collection }.right[NineCardsException])
  }

  override def getUser(implicit context: ContextSupport): Task[NineCardsException \/ User] =
    Task(loadFile[User](getFileUser) match {
      case Success(user) => \/-(user)
      case Failure(ex) => -\/(NineCardsException(msg = "User not found", cause = ex.some))
    })

  override def saveUser(user: User)(implicit context: ContextSupport): Task[NineCardsException \/ Unit] =
    Task {
      writeFile[User](getFileUser, user) match {
        case Success(result) => \/-(result)
        case Failure(ex) => -\/(NineCardsException(msg = "User not saved", cause = ex.some))
      }
    }

  override def resetUser(implicit context: ContextSupport): Task[NineCardsException \/ Boolean] =
    Task {
      fromTryCatchNineCardsException[Boolean] {
        val fileUser = getFileUser
        fileUser.exists() && fileUser.delete()
      }
    }

  override def getAndroidId(implicit context: ContextSupport): Task[NineCardsException \/ String] =
    Task {
      val cursor = Option(context.getContentResolver.query(Uri.parse(ContentGServices), null, null, Array(AndroidId), null))
      val result = cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
      result map (\/-(_)) getOrElse -\/(NineCardsException(msg = "Android Id not found"))
    }

  override def getInstallation(implicit context: ContextSupport): Task[NineCardsException \/ Installation] =
    Task {
      loadFile[Installation](getFileInstallation) match {
        case Success(installation) => \/-(installation)
        case Failure(ex) => -\/(NineCardsException(msg = "Installation not found", cause = ex.some))
      }
    }

  override def existsInstallation(implicit context: ContextSupport): Task[NineCardsException \/ Boolean] =
    Task {
      fromTryCatchNineCardsException[Boolean] {
        getFileInstallation.exists()
      }
    }

  override def saveInstallation(installation: Installation)(implicit context: ContextSupport): Task[NineCardsException \/ Unit] =
    Task {
      writeFile[Installation](getFileInstallation, installation) match {
        case Success(result) => \/-(result)
        case Failure(ex) => -\/(NineCardsException(msg = "Installation not saved", cause = ex.some))
      }
    }

  private def getFileInstallation(implicit context: ContextSupport) = new File(context.getFilesDir, FilenameInstallation)

  private def getFileUser(implicit context: ContextSupport) = new File(context.getFilesDir, FilenameUser)
}
