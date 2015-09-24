package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import java.io.File

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.commons.utils.FileUtils
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import com.fortysevendeg.ninecardslauncher.{repository => repo}
import rapture.core.{Answer, Result}

import scala.util.{Failure, Success}
import scalaz.Scalaz._
import scalaz.concurrent.Task

class PersistenceServicesImpl(
  appRepository: AppRepository,
  cacheCategoryRepository: CacheCategoryRepository,
  cardRepository: CardRepository,
  collectionRepository: CollectionRepository,
  geoInfoRepository: GeoInfoRepository)
  extends PersistenceServices
  with Conversions
  with ImplicitsPersistenceServiceExceptions {

  val fileUtils = new FileUtils()

  // TODO These contants don't should be here

  val AccountType = "com.google"

  val AndroidId = "android_id"

  val ContentGServices = "content://com.google.android.gsf.gservices"

  val FilenameUser = "__user_entity__"

  val FilenameInstallation = "__installation_entity__"

  override def getApp(packageName: String) =
    (for {
      app <- appRepository.fetchAppByPackage(packageName)
    } yield app map toApp).resolve[PersistenceServiceException]

  override def addApp(request: AddAppRequest) =
    (for {
      app <- appRepository.addApp(toRepositoryAppData(request))
    } yield toApp(app)).resolve[PersistenceServiceException]

  override def deleteApp(request: DeleteAppRequest) =
    (for {
      deleted <- appRepository.deleteApp(toRepositoryApp(request))
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteAppByPackage(packageName: String) =
    (for {
      deleted <- appRepository.deleteAppByPackage(packageName)
    } yield deleted).resolve[PersistenceServiceException]

  override def updateApp(request: UpdateAppRequest) =
    (for {
      updated <- appRepository.updateApp(toRepositoryApp(request))
    } yield updated).resolve[PersistenceServiceException]

  override def addCacheCategory(request: AddCacheCategoryRequest) =
    (for {
      cacheCategory <- cacheCategoryRepository.addCacheCategory(toRepositoryCacheCategoryData(request))
    } yield toCacheCategory(cacheCategory)).resolve[PersistenceServiceException]

  override def deleteCacheCategory(request: DeleteCacheCategoryRequest) =
    (for {
      deleted <- cacheCategoryRepository.deleteCacheCategory(toRepositoryCacheCategory(request.cacheCategory))
    } yield deleted).resolve[PersistenceServiceException]

  override def deleteCacheCategoryByPackage(request: DeleteCacheCategoryByPackageRequest) =
    (for {
      deleted <- cacheCategoryRepository.deleteCacheCategoryByPackage(request.packageName)
    } yield deleted).resolve[PersistenceServiceException]

  override def fetchCacheCategoryByPackage(request: FetchCacheCategoryByPackageRequest) =
    (for {
      maybeCacheCategory <- cacheCategoryRepository.fetchCacheCategoryByPackage(request.packageName)
    } yield maybeCacheCategory map toCacheCategory).resolve[PersistenceServiceException]

  override def fetchCacheCategories =
    (for {
      cacheCategories <- cacheCategoryRepository.fetchCacheCategories
    } yield cacheCategories map toCacheCategory).resolve[PersistenceServiceException]

  override def findCacheCategoryById(request: FindCacheCategoryByIdRequest) =
    (for {
      maybeCacheCategory <- cacheCategoryRepository.findCacheCategoryById(request.id)
    } yield maybeCacheCategory map toCacheCategory).resolve[PersistenceServiceException]

  override def updateCacheCategory(request: UpdateCacheCategoryRequest) =
    (for {
      updated <- cacheCategoryRepository.updateCacheCategory(toRepositoryCacheCategory(request))
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

  override def deleteCard(request: DeleteCardRequest) =
    (for {
      deleted <- cardRepository.deleteCard(toRepositoryCard(request.card))
    } yield deleted).resolve[PersistenceServiceException]

  override def fetchCardsByCollection(request: FetchCardsByCollectionRequest) =
    (for {
      cards <- cardRepository.fetchCardsByCollection(request.collectionId)
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

  override def getUser(implicit context: ContextSupport) =
    Service(
      Task(fileUtils.loadFile[User](getFileUser) match {
        case Success(user) => Result.answer(user)
        case Failure(ex) => Result.errata(UserNotFoundException(message = "User not found", cause = ex.some))
      }))

  override def saveUser(user: User)(implicit context: ContextSupport) =
    Service {
      Task {
        fileUtils.writeFile[User](getFileUser, user) match {
          case Success(result) => Result.answer(result)
          case Failure(ex) => Result.errata(PersistenceServiceException(message = "User not saved", cause = ex.some))
        }
      }
    }

  override def resetUser(implicit context: ContextSupport) =
    Service {
      Task {
        CatchAll[PersistenceServiceException] {
          val fileUser = getFileUser
          fileUser.exists() && fileUser.delete()
        }
      }
    }

  override def getAndroidId(implicit context: ContextSupport) =
    Service {
      Task {
        val cursor = Option(context.getContentResolver.query(Uri.parse(ContentGServices), null, null, Array(AndroidId), null))
        val result = cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)

        result map {
          Result.answer[String, AndroidIdNotFoundException]
        } getOrElse Result.errata[String, AndroidIdNotFoundException](AndroidIdNotFoundException(message = "Android Id not found"))
      }
    }

  override def getInstallation(implicit context: ContextSupport) =
    Service(
      Task(
        fileUtils.loadFile[Installation](getFileInstallation) match {
          case Success(installation) => Result.answer(installation)
          case Failure(ex) => Result.errata(InstallationNotFoundException(message = "Installation not found", cause = ex.some))
        }))

  override def existsInstallation(implicit context: ContextSupport) =
    Service {
      Task {
        CatchAll[PersistenceServiceException](getFileInstallation.exists())
      }
    }

  override def saveInstallation(installation: Installation)(implicit context: ContextSupport) =
    Service {
      Task {
        fileUtils.writeFile[Installation](getFileInstallation, installation) match {
          case Success(result) => Result.answer(result)
          case Failure(ex) => Result.errata(PersistenceServiceException(message = "Installation not saved", cause = ex.some))
        }
      }
    }

  private def getFileInstallation(implicit context: ContextSupport) = new File(context.getFilesDir, FilenameInstallation)

  private def getFileUser(implicit context: ContextSupport) = new File(context.getFilesDir, FilenameUser)
}
