package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Card
import rapture.core.Result

import scalaz.concurrent.Task

trait CardPersistenceServicesImpl {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addCard(request: AddCardRequest) =
    request.collectionId match {
      case Some(collectionId) =>
        (for {
          card <- cardRepository.addCard(collectionId, toRepositoryCardData(request))
        } yield toCard(card)).resolve[PersistenceServiceException]
      case None =>
        Service(Task(Result.errata[Card, PersistenceServiceException](PersistenceServiceException("CollectionId can't be empty"))))
    }

  def addCards(collectionId: Int, request: Seq[AddCardRequest]) =
    (for {
      cards <- cardRepository.addCards(collectionId, request map toRepositoryCardData)
    } yield cards map toCard).resolve[PersistenceServiceException]

  def deleteAllCards() =
    (for {
      deleted <- cardRepository.deleteCards()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteCard(request: DeleteCardRequest) =
    (for {
      deleted <- cardRepository.deleteCard(toRepositoryCard(request.card))
    } yield deleted).resolve[PersistenceServiceException]

  def deleteCardsByCollection(collectionId: Int) =
    (for {
      deleted <- cardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId")
    } yield deleted).resolve[PersistenceServiceException]

  def fetchCardsByCollection(request: FetchCardsByCollectionRequest) =
    (for {
      cards <- cardRepository.fetchCardsByCollection(request.collectionId)
    } yield cards map toCard).resolve[PersistenceServiceException]

  def fetchCards =
    (for {
      cards <- cardRepository.fetchCards
    } yield cards map toCard).resolve[PersistenceServiceException]

  def findCardById(request: FindCardByIdRequest) =
    (for {
      maybeCard <- cardRepository.findCardById(request.id)
    } yield maybeCard map toCard).resolve[PersistenceServiceException]

  def updateCard(request: UpdateCardRequest) =
    (for {
      updated <- cardRepository.updateCard(toRepositoryCard(request))
    } yield updated).resolve[PersistenceServiceException]

  def updateCards(request: UpdateCardsRequest) =
    (for {
      updated <- cardRepository.updateCards(request.updateCardRequests map toRepositoryCard)
    } yield updated).resolve[PersistenceServiceException]

}
