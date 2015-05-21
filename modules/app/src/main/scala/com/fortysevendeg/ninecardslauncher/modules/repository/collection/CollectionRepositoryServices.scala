package com.fortysevendeg.ninecardslauncher.modules.repository.collection

import android.content.ContentResolver
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.models.Card
import com.fortysevendeg.ninecardslauncher.modules.repository.card.{AddCardRequest, CardItem, Conversions => CardConversions, DeleteCardRequest}
import com.fortysevendeg.ninecardslauncher.repository.{FetchCardsByCollectionRequest, FetchSortedCollectionsRequest, NineCardRepositoryClient}

import scala.concurrent.{ExecutionContext, Future, Promise}


trait CollectionRepositoryServices {
  def addCollection: Service[AddCollectionRequest, AddCollectionResponse]
  def deleteCollection: Service[DeleteCollectionRequest, DeleteCollectionResponse]
  def fetchCollections: Service[FetchCollectionsRequest, FetchCollectionsResponse]
  def fetchCollectionByOriginalSharedCollection: Service[FetchCollectionByOriginalSharedCollectionRequest, FetchCollectionByOriginalSharedCollectionResponse]
  def fetchCollectionByPosition: Service[FetchCollectionByPositionRequest, FetchCollectionByPositionResponse]
  def findCollectionById: Service[FindCollectionByIdRequest, FindCollectionByIdResponse]
  def updateCollection: Service[UpdateCollectionRequest, UpdateCollectionResponse]
}

trait CollectionRepositoryServicesComponent {
  val collectionRepositoryServices: CollectionRepositoryServices
}

trait CollectionRepositoryServicesComponentImpl
    extends CollectionRepositoryServicesComponent {

  self: ContextWrapperProvider =>

  lazy val collectionRepositoryServices = new CollectionRepositoryServicesImpl

  class CollectionRepositoryServicesImpl
      extends CollectionRepositoryServices
      with Conversions
      with CardConversions
      with NineCardRepositoryClient {

    override implicit val contentResolver: ContentResolver = contextProvider.application.getContentResolver
    override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    override def addCollection: Service[AddCollectionRequest, AddCollectionResponse] =
      request => {
        val result = for {
          addCollectionResponse <- repoAddCollection(toRepositoryAddCollectionRequest(request))
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
          deleteCollectionResponse <- repoDeleteCollection(toRepositoryDeleteCollectionRequest(request))
        } yield DeleteCollectionResponse(deleted = deleteCollectionResponse.deleted)

        result recover {
          case _ => DeleteCollectionResponse(deleted = 0)
        }
      }

    override def fetchCollections: Service[FetchCollectionsRequest, FetchCollectionsResponse] =
      request => {
        val promise = Promise[FetchCollectionsResponse]()
        repoFetchSortedCollections(FetchSortedCollectionsRequest()) map {
          response =>
            val futures = toCollectionSeq(response.collections) map {
              collection =>
                repoFetchCardsByCollection(FetchCardsByCollectionRequest(collection.id)) map {
                  cardResponse =>
                    collection.copy(cards = cardResponse.cards map toCard)
                }
            }
            Future.sequence(futures) map {
              collections =>
                promise.success(FetchCollectionsResponse(collections))
            } recover {
              case _ => promise.success(FetchCollectionsResponse(Seq.empty))
            }
        } recover {
          case _ => promise.success(FetchCollectionsResponse(Seq.empty))
        }
        promise.future
      }

    override def fetchCollectionByOriginalSharedCollection: Service[FetchCollectionByOriginalSharedCollectionRequest, FetchCollectionByOriginalSharedCollectionResponse] =
      request => {
        val result = for {
          collectionResponse <- repoFetchCollectionByOriginalSharedCollectionId(toRepositoryFetchCollectionByOriginalSharedCollectionRequest(request))
          Some(repoCollection) = collectionResponse.collection
          collection = toCollection(repoCollection)
          cardsResponse <- repoFetchCardsByCollection(FetchCardsByCollectionRequest(collection.id))
        } yield FetchCollectionByOriginalSharedCollectionResponse(Option(collection.copy(cards = cardsResponse.cards map toCard)))

        result recover {
          case _ => FetchCollectionByOriginalSharedCollectionResponse(None)
        }
      }

    override def fetchCollectionByPosition: Service[FetchCollectionByPositionRequest, FetchCollectionByPositionResponse] =
      request => {
        val result = for {
          collectionResponse <- repoFetchCollectionByPosition(toRepositoryFetchCollectionByPositionRequest(request))
          Some(repoCollection) = collectionResponse.collection
          collection = toCollection(repoCollection)
          cardsResponse <- repoFetchCardsByCollection(FetchCardsByCollectionRequest(collection.id))
        } yield FetchCollectionByPositionResponse(Option(collection.copy(cards = cardsResponse.cards map toCard)))

        result recover {
          case _ => FetchCollectionByPositionResponse(None)
        }
      }

    override def findCollectionById: Service[FindCollectionByIdRequest, FindCollectionByIdResponse] =
      request => {
        val result = for {
          collectionResponse <- repoFindCollectionById(toRepositoryFindCollectionByIdRequest(request))
          Some(repoCollection) = collectionResponse.collection
          collection = toCollection(repoCollection)
          cardsResponse <- repoFetchCardsByCollection(FetchCardsByCollectionRequest(collection.id))
        } yield FindCollectionByIdResponse(Option(collection.copy(cards = cardsResponse.cards map toCard)))

        result recover {
          case _ => FindCollectionByIdResponse(None)
        }
      }

    override def updateCollection: Service[UpdateCollectionRequest, UpdateCollectionResponse] =
      request =>
        repoUpdateCollection(toRepositoryUpdateCollectionRequest(request)) map {
          response =>
            UpdateCollectionResponse(updated = response.updated)
        }

    private def addCards(collectionId: Int, cards: Seq[CardItem]) =
      Future.sequence(
        cards map {
          card =>
            repoAddCard(toRepositoryAddCardRequest(AddCardRequest(collectionId, card)))
        }
      )

    private def deleteCards(cards: Seq[Card]) =
      Future.sequence(
        cards map {
          card =>
            repoDeleteCard(toRepositoryDeleteCardRequest(DeleteCardRequest(card)))
        }
      )
  }

}