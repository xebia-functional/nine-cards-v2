package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
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

  trait CardServicesScope
    extends RepositoryServicesScope
      with PersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class CardPersistenceServicesImplSpec extends CardPersistenceServicesDataSpecification {

  "addCard" should {

    "return a Card value for a valid request" in new CardServicesScope {

      mockCardRepository.addCard(collectionId, repoCardData) returns TaskService(Task(Xor.right(repoCard)))
      val result = persistenceServices.addCard(createAddCardRequest()).value.run

      result must beLike {
        case Xor.Right(card) =>
          card.id shouldEqual cardId
          card.cardType shouldEqual cardType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.addCard(collectionId, repoCardData) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.addCard(createAddCardRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteAllCards" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      mockCardRepository.deleteCards() returns TaskService(Task(Xor.right(items)))
      val result = persistenceServices.deleteAllCards().value.run
      result shouldEqual Xor.Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.deleteCards() returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteAllCards().value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteCard" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      seqRepoCard foreach { repoCard =>
        mockCardRepository.deleteCard(collectionId, repoCard.id) returns TaskService(Task(Xor.right(item)))
      }

      val result = persistenceServices.deleteCard(collectionId, card.id).value.run
      result shouldEqual Xor.Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      seqRepoCard foreach { repoCard =>
        mockCardRepository.deleteCard(collectionId, repoCard.id) returns TaskService(Task(Xor.left(exception)))
      }

      val result = persistenceServices.deleteCard(collectionId, card.id).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteCardsByCollection" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns TaskService(Task(Xor.right(items)))
      val result = persistenceServices.deleteCardsByCollection(collectionId).value.run
      result shouldEqual Xor.Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteCardsByCollection(collectionId).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchCardsByCollection" should {

    "return a list of Card elements for a valid request" in new CardServicesScope {

      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Xor.right(seqRepoCard)))
      }

      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).value.run

      result must beLike {
        case Xor.Right(cards) => cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Xor.left(exception)))
      }

      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchCards" should {

    "return a list of Card elements for a valid request" in new CardServicesScope {
      mockCardRepository.fetchCards returns TaskService(Task(Xor.right(seqRepoCard)))

      val result = persistenceServices.fetchCards.value.run

      result must beLike {
        case Xor.Right(cards) => cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.fetchCards returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchCards.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findCardById" should {

    "return a Card for a valid request" in new CardServicesScope {

      mockCardRepository.findCardById(cardId) returns TaskService(Task(Xor.right(Option(repoCard))))
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).value.run

      result must beLike {
        case Xor.Right(maybeCard) =>
          maybeCard must beSome[Card].which { card =>
            card.cardType shouldEqual cardType
          }
      }
    }

    "return None when a non-existent id is given" in new CardServicesScope {

      mockCardRepository.findCardById(nonExistentCardId) returns TaskService(Task(Xor.right(None)))
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = nonExistentCardId)).value.run
      result shouldEqual Xor.Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.findCardById(cardId) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCard" should {

    "return the number of elements updated for a valid request" in new CardServicesScope {

      mockCardRepository.updateCard(repoCard) returns TaskService(Task(Xor.right(item)))
      val result = persistenceServices.updateCard(createUpdateCardRequest()).value.run
      result shouldEqual Xor.Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.updateCard(repoCard) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.updateCard(createUpdateCardRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCards" should {

    "return the sequence with the number of elements updated for a valid request" in new CardServicesScope {

      mockCardRepository.updateCards(seqRepoCard) returns TaskService(Task(Xor.right(item to items)))
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).value.run
      result shouldEqual Xor.Right(item to items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.updateCards(seqRepoCard) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

}
