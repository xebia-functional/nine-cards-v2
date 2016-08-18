package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity
import com.fortysevendeg.ninecardslauncher.services.persistence.data._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Card
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

import scalaz.concurrent.Task


trait CardPersistenceServicesDataSpecification
  extends Specification
    with DisjunctionMatchers {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    mockCardRepository.addCard(collectionId, repoCardData) returns CatsService(Task(Xor.right(repoCard)))

    mockCardRepository.addCards(any) returns CatsService(Task(Xor.right(Seq(repoCard))))

    mockCardRepository.deleteCards() returns CatsService(Task(Xor.right(items)))

    mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns CatsService(Task(Xor.right(items)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(repoCard) returns CatsService(Task(Xor.right(item)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns CatsService(Task(Xor.right(seqRepoCard)))
    }

    mockCardRepository.fetchCards returns CatsService(Task(Xor.right(seqRepoCard)))

    mockCardRepository.findCardById(cardId) returns CatsService(Task(Xor.right(Option(repoCard))))

    mockCardRepository.findCardById(nonExistentCardId) returns CatsService(Task(Xor.right(None)))

    mockCardRepository.updateCard(repoCard) returns CatsService(Task(Xor.right(item)))

    mockCardRepository.updateCards(seqRepoCard) returns CatsService(Task(Xor.right(item to items)))

  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockCardRepository.addCard(collectionId, repoCardData) returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.deleteCards() returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns CatsService(Task(Xor.left(exception)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(repoCard) returns CatsService(Task(Xor.left(exception)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns CatsService(Task(Xor.left(exception)))
    }

    mockCardRepository.fetchCards returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.findCardById(cardId) returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.updateCard(repoCard) returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.updateCards(seqRepoCard) returns CatsService(Task(Xor.left(exception)))

  }

}

class CardPersistenceServicesImplSpec extends CardPersistenceServicesDataSpecification {

  "addCard" should {

    "return a Card value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCard(createAddCardRequest()).value.run

      result must beLike {
        case Xor.Right(card) =>
          card.id shouldEqual cardId
          card.cardType shouldEqual cardType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCard(createAddCardRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllCards" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCards().value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCards().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteCard" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card)).value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteCardsByCollection" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCardsByCollection(collectionId).value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCardsByCollection(collectionId).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCardsByCollection" should {

    "return a list of Card elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).value.run

      result must beLike {
        case Xor.Right(cards) => cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCards" should {

    "return a list of Card elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCards.value.run

      result must beLike {
        case Xor.Right(cards) => cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCards.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findCardById" should {

    "return a Card for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).value.run

      result must beLike {
        case Xor.Right(maybeCard) =>
          maybeCard must beSome[Card].which { card =>
            card.cardType shouldEqual cardType
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = nonExistentCardId)).value.run

      result must beLike {
        case Xor.Right(maybeCard) => maybeCard must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCard" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCard(createUpdateCardRequest()).value.run

      result must beLike {
        case Xor.Right(updated) => updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCard(createUpdateCardRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCards" should {

    "return the sequence with the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).value.run

      result must beLike {
        case Xor.Right(updated) => updated shouldEqual (item to items)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

}
