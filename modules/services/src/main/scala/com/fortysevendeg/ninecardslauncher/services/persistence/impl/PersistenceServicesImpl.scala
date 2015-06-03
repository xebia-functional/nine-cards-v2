package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.Service
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import com.fortysevendeg.ninecardslauncher.{repository => repo}

import scala.concurrent.{ExecutionContext, Future}

class PersistenceServicesImpl(
  cacheCategoryRepository: CacheCategoryRepository,
  cardRepository: CardRepository,
  collectionRepository: CollectionRepository,
  geoInfoRepository: GeoInfoRepository
  )
  extends PersistenceServices
  with Conversions {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def addCacheCategory: Service[AddCacheCategoryRequest, AddCacheCategoryResponse] =
    request => {
      cacheCategoryRepository.addCacheCategory(toAddCacheCategoryRequest(request)) map {
        response =>
          AddCacheCategoryResponse(toCacheCategory(response.cacheCategory))
      }
    }

  override def deleteCacheCategory: Service[DeleteCacheCategoryRequest, DeleteCacheCategoryResponse] =
    request => {
      cacheCategoryRepository.deleteCacheCategory(toRepositoryDeleteCacheCategoryRequest(request)) map {
        response =>
          DeleteCacheCategoryResponse(response.deleted)
      }
    }

  override def deleteCacheCategoryByPackage: Service[DeleteCacheCategoryByPackageRequest, DeleteCacheCategoryByPackageResponse] =
    request => {
      cacheCategoryRepository.deleteCacheCategoryByPackage(toRepositoryDeleteCacheCategoryByPackageRequest(request)) map {
        response =>
          DeleteCacheCategoryByPackageResponse(response.deleted)
      }
    }

  override def fetchCacheCategoryByPackage: Service[FetchCacheCategoryByPackageRequest, FetchCacheCategoryByPackageResponse] =
    request => {
      cacheCategoryRepository.fetchCacheCategoryByPackage(toRepositoryFetchCacheCategoryByPackageRequest(request)) map {
        response =>
          FetchCacheCategoryByPackageResponse(response.cacheCategory map toCacheCategory)
      }
    }

  override def fetchCacheCategories: Service[FetchCacheCategoriesRequest, FetchCacheCategoriesResponse] =
    request => {
      cacheCategoryRepository.fetchCacheCategories(repo.FetchCacheCategoriesRequest()) map {
        response =>
          FetchCacheCategoriesResponse(toCacheCategorySeq(response.cacheCategories))
      }
    }

  override def findCacheCategoryById: Service[FindCacheCategoryByIdRequest, FindCacheCategoryByIdResponse] =
    request => {
      cacheCategoryRepository.findCacheCategoryById(toRepositoryFindCacheCategoryByIdRequest(request)) map {
        response =>
          FindCacheCategoryByIdResponse(response.cacheCategory map toCacheCategory)
      }
    }

  override def updateCacheCategory: Service[UpdateCacheCategoryRequest, UpdateCacheCategoryResponse] =
    request =>
      cacheCategoryRepository.updateCacheCategory(toRepositoryUpdateCacheCategoryRequest(request)) map {
        response =>
          UpdateCacheCategoryResponse(updated = response.updated)
      }

  override def addCard: Service[AddCardRequest, AddCardResponse] =
    request =>
      cardRepository.addCard(toRepositoryAddCardRequest(request)) map {
        response =>
          AddCardResponse(card = toCard(response.card))
      }

  override def deleteCard: Service[DeleteCardRequest, DeleteCardResponse] =
    request =>
      cardRepository.deleteCard(toRepositoryDeleteCardRequest(request)) map {
        response =>
          DeleteCardResponse(deleted = response.deleted)
      }

  override def fetchCardsByCollection: Service[FetchCardsByCollectionRequest, FetchCardsByCollectionResponse] =
    request =>
      cardRepository.fetchCardsByCollection(toRepositoryFetchCardsByCollectionRequest(request)) map {
        response =>
          FetchCardsByCollectionResponse(cards = response.cards map toCard)
      }

  override def findCardById: Service[FindCardByIdRequest, FindCardByIdResponse] =
    request =>
      cardRepository.findCardById(toRepositoryFindCardByIdRequest(request)) map {
        response =>
          FindCardByIdResponse(card = response.card map toCard)
      }

  override def updateCard: Service[UpdateCardRequest, UpdateCardResponse] =
    request =>
      cardRepository.updateCard(toRepositoryUpdateCardRequest(request)) map {
        response =>
          UpdateCardResponse(updated = response.updated)
      }

  override def addCollection: Service[AddCollectionRequest, AddCollectionResponse] =
    request => {
      val result = for {
        addCollectionResponse <- collectionRepository.addCollection(toRepositoryAddCollectionRequest(request))
        addCardsResponse <- addCards(addCollectionResponse.collection.id, request.cards)
      } yield AddCollectionResponse(success = true)

      result recover {
        case _ => AddCollectionResponse(success = false)
      }
    }

  override def deleteCollection: Service[DeleteCollectionRequest, DeleteCollectionResponse] =
    request => {
      val result = for {
        deleteCardsResponse <- deleteCards(request.collection.cards)
        deleteCollectionResponse <- collectionRepository.deleteCollection(toRepositoryDeleteCollectionRequest(request))
      } yield DeleteCollectionResponse(deleted = deleteCollectionResponse.deleted)

      result recover {
        case _ => DeleteCollectionResponse(deleted = 0)
      }
    }

  override def fetchCollections: Service[FetchCollectionsRequest, FetchCollectionsResponse] =
    request => {
      val result = for {
        collectionsResponse <- collectionRepository.fetchSortedCollections(repo.FetchSortedCollectionsRequest())
        collectionWithCards <- fetchCards(collectionsResponse.collections map toCollection)
      } yield FetchCollectionsResponse(collectionWithCards)

      result recover {
        case _ => FetchCollectionsResponse(Seq.empty)
      }
    }

  override def fetchCollectionBySharedCollection: Service[FetchCollectionBySharedCollectionRequest, FetchCollectionBySharedCollectionResponse] =
    request => {
      val result = for {
        collectionResponse <- collectionRepository.fetchCollectionByOriginalSharedCollectionId(toRepositoryFetchCollectionBySharedCollectionRequest(request))
        Some(repoCollection) = collectionResponse.collection
        collection = toCollection(repoCollection)
        cardsResponse <- cardRepository.fetchCardsByCollection(repo.FetchCardsByCollectionRequest(collection.id))
      } yield FetchCollectionBySharedCollectionResponse(Option(collection.copy(cards = cardsResponse.cards map toCard)))

      result recover {
        case _ => FetchCollectionBySharedCollectionResponse(None)
      }
    }

  override def fetchCollectionByPosition: Service[FetchCollectionByPositionRequest, FetchCollectionByPositionResponse] =
    request => {
      val result = for {
        collectionResponse <- collectionRepository.fetchCollectionByPosition(toRepositoryFetchCollectionByPositionRequest(request))
        Some(repoCollection) = collectionResponse.collection
        collection = toCollection(repoCollection)
        cardsResponse <- cardRepository.fetchCardsByCollection(repo.FetchCardsByCollectionRequest(collection.id))
      } yield FetchCollectionByPositionResponse(Option(collection.copy(cards = cardsResponse.cards map toCard)))

      result recover {
        case _ => FetchCollectionByPositionResponse(None)
      }
    }

  override def findCollectionById: Service[FindCollectionByIdRequest, FindCollectionByIdResponse] =
    request => {
      val result = for {
        collectionResponse <- collectionRepository.findCollectionById(toRepositoryFindCollectionByIdRequest(request))
        Some(repoCollection) = collectionResponse.collection
        collection = toCollection(repoCollection)
        cardsResponse <- cardRepository.fetchCardsByCollection(repo.FetchCardsByCollectionRequest(collection.id))
      } yield FindCollectionByIdResponse(Option(collection.copy(cards = cardsResponse.cards map toCard)))

      result recover {
        case _ => FindCollectionByIdResponse(None)
      }
    }

  override def updateCollection: Service[UpdateCollectionRequest, UpdateCollectionResponse] =
    request =>
      collectionRepository.updateCollection(toRepositoryUpdateCollectionRequest(request)) map {
        response =>
          UpdateCollectionResponse(updated = response.updated)
      }

  override def addGeoInfo: Service[AddGeoInfoRequest, AddGeoInfoResponse] =
    request =>
      geoInfoRepository.addGeoInfo(toRepositoryAddGeoInfoRequest(request)) map {
        response =>
          AddGeoInfoResponse(geoInfo = toGeoInfo(response.geoInfo))
      }

  override def deleteGeoInfo: Service[DeleteGeoInfoRequest, DeleteGeoInfoResponse] =
    request =>
      geoInfoRepository.deleteGeoInfo(toRepositoryDeleteGeoInfoRequest(request)) map {
        response =>
          DeleteGeoInfoResponse(deleted = response.deleted)
      }

  override def fetchGeoInfoByConstrain: Service[FetchGeoInfoByConstrainRequest, FetchGeoInfoByConstrainResponse] =
    request =>
      geoInfoRepository.fetchGeoInfoByConstrain(toRepositoryFetchGeoInfoByConstrainRequest(request)) map {
        response =>
          FetchGeoInfoByConstrainResponse(geoInfo = response.geoInfo map toGeoInfo)
      }

  override def fetchGeoInfoItems: Service[FetchGeoInfoItemsRequest, FetchGeoInfoItemsResponse] =
    request =>
      geoInfoRepository.fetchGeoInfoItems(repo.FetchGeoInfoItemsRequest()) map {
        response =>
          FetchGeoInfoItemsResponse(geoInfoItems = toGeoInfoSeq(response.geoInfoItems))
      }

  override def findGeoInfoById: Service[FindGeoInfoByIdRequest, FindGeoInfoByIdResponse] =
    request =>
      geoInfoRepository.findGeoInfoById(toRepositoryFindGeoInfoByIdRequest(request)) map {
        response =>
          FindGeoInfoByIdResponse(geoInfo = response.geoInfo map toGeoInfo)
      }

  override def updateGeoInfo: Service[UpdateGeoInfoRequest, UpdateGeoInfoResponse] =
    request =>
      geoInfoRepository.updateGeoInfo(toRepositoryUpdateGeoInfoRequest(request)) map {
        response =>
          UpdateGeoInfoResponse(updated = response.updated)
      }

  private[this] def addCards(collectionId: Int, cards: Seq[CardItem]) =
    Future.sequence(
      cards map {
        card =>
          cardRepository.addCard(toRepositoryAddCardRequest(AddCardRequest(collectionId, card)))
      }
    )

  private[this] def deleteCards(cards: Seq[Card]) =
    Future.sequence(
      cards map {
        card =>
          cardRepository.deleteCard(toRepositoryDeleteCardRequest(DeleteCardRequest(card)))
      }
    )

  private[this] def fetchCards(collections: Seq[Collection]): Future[Seq[Collection]] = {
    val result = collections map {
      collection =>
        cardRepository.fetchCardsByCollection(repo.FetchCardsByCollectionRequest(collection.id)) map {
          response =>
            collection.copy(cards = response.cards map toCard)
        }
    }

    Future.sequence(result)
  }
}
