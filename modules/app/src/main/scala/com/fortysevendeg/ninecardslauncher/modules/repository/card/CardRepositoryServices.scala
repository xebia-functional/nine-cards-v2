package com.fortysevendeg.ninecardslauncher.modules.repository.card

import android.content.ContentResolver
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.repository.NineCardRepositoryClient

import scala.concurrent.ExecutionContext

trait CardRepositoryServices {
  def addCard: Service[AddCardRequest, AddCardResponse]
  def deleteCard: Service[DeleteCardRequest, DeleteCardResponse]
  def fetchCardsByCollection: Service[FetchCardsByCollectionRequest, FetchCardsByCollectionResponse]
  def findCardById: Service[FindCardByIdRequest, FindCardByIdResponse]
  def updateCard: Service[UpdateCardRequest, UpdateCardResponse]
}

trait CardRepositoryServicesComponent {
  val cardRepositoryServices: CardRepositoryServices
}

trait CardRepositoryServicesComponentImpl
    extends CardRepositoryServicesComponent {

  self: ContextWrapperProvider =>

  lazy val cardRepositoryServices = new CardRepositoryServicesImpl

  class CardRepositoryServicesImpl
      extends CardRepositoryServices
      with Conversions
      with NineCardRepositoryClient {

    override implicit val contentResolver: ContentResolver = contextProvider.application.getContentResolver
    override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    override def addCard: Service[AddCardRequest, AddCardResponse] =
      request =>
        repoAddCard(toRepositoryAddCardRequest(request)) map {
          response =>
            AddCardResponse(card = toCard(response.card))
        }

    override def deleteCard: Service[DeleteCardRequest, DeleteCardResponse] =
      request =>
        repoDeleteCard(toRepositoryDeleteCardRequest(request)) map {
          response =>
            DeleteCardResponse(deleted = response.deleted)
        }

    override def fetchCardsByCollection: Service[FetchCardsByCollectionRequest, FetchCardsByCollectionResponse] =
      request =>
        repoFetchCardsByCollection(toRepositoryFetchCardsByCollectionRequest(request)) map {
          response =>
            FetchCardsByCollectionResponse(cards = response.cards map toCard)
        }

    override def findCardById: Service[FindCardByIdRequest, FindCardByIdResponse] =
      request =>
        repoFindCardById(toRepositoryFindCardByIdRequest(request)) map {
          response =>
            FindCardByIdResponse(card = response.card map toCard)
        }

    override def updateCard: Service[UpdateCardRequest, UpdateCardResponse] =
      request =>
        repoUpdateCard(toRepositoryUpdateCardRequest(request)) map {
          response =>
            UpdateCardResponse(updated = response.updated)
        }
  }

}
