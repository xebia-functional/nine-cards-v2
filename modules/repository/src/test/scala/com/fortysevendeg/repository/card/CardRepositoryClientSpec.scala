package com.fortysevendeg.repository.card

import com.fortysevendeg.ninecardslauncher.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils._
import com.fortysevendeg.ninecardslauncher.repository.model.Card
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class CardRepositoryClientSpec
    extends Specification
    with Mockito
    with Scope {

  "CardRepositoryClient component" should {

    "addCard should return a valid Card object" in
        new AddCardSupport {

          val response = await(repoAddCard(createAddCardRequest))

          response.card.id shouldEqual cardId
          response.card.data.intent shouldEqual intent
        }

    "deleteCard should return a successful response when a valid cache category id is given" in
        new DeleteCardSupport {
          val response = await(repoDeleteCard(createDeleteCardRequest))

          response.deleted shouldEqual 1
        }

    "findCardById should return a Card object when a existing id is given" in
        new FindCardByIdSupport {
          val response = await(repoFindCardById(createFindCardByIdRequest(id = cardId)))

          response.card should beSome[Card].which(_.id shouldEqual cardId)
        }

    "findCardById should return None when a non-existing id is given" in
        new FindCardByIdSupport {
          val response = await(repoFindCardById(createFindCardByIdRequest(id = nonExistingCardId)))

          response.card shouldEqual None
        }

    "fetchCardsByCollection should return a Card sequence when a existing collection id is given" in
        new FetchCardsByCollectionSupport {
          val response = await(repoFetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId = collectionId)))

          response.cards shouldEqual cardSeq
        }

    "fetchCardsByCollection should return an empty sequence when a non-existing collection id is given" in
        new FetchCardsByCollectionSupport {
          val response = await(repoFetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId = nonExistingCollectionId)))

          response.cards shouldEqual Seq.empty
        }

    "updateCard should return a successful response when the card is updated" in
        new UpdateCardSupport {
          val response = await(repoUpdateCard(createUpdateCardRequest))

          response.updated shouldEqual 1
        }

    "getEntityFromCursor should return None when an empty cursor is given" in
        new EmptyCardMockCursor {
          val result = getEntityFromCursor(cardEntityFromCursor)(mockCursor)

          result shouldEqual None
        }

    "getEntityFromCursor should return a Card object when a cursor with data is given" in
        new CardMockCursor {
          val result = getEntityFromCursor(cardEntityFromCursor)(mockCursor)

          result shouldEqual Some(cardEntity)
        }

    "getListFromCursor should return an empty sequence when an empty cursor is given" in
        new EmptyCardMockCursor {
          val result = getListFromCursor(cardEntityFromCursor)(mockCursor)

          result shouldEqual Seq.empty
        }

    "getListFromCursor should return a Card sequence when a cursor with data is given" in
        new CardMockCursor {
          val result = getListFromCursor(cardEntityFromCursor)(mockCursor)

          result shouldEqual cardEntitySeq
        }
  }
}
