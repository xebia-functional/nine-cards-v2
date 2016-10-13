package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.repository.RepositoryException
import cards.nine.repository.provider.CardEntity
import cards.nine.services.persistence.data._
import cards.nine.services.persistence.models.Card
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import cards.nine.commons.test.TaskServiceTestOps._
import cats.syntax.either._


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

      mockCardRepository.addCard(collectionId, repoCardData) returns TaskService(Task(Either.right(repoCard)))
      val result = persistenceServices.addCard(createAddCardRequest()).value.run

      result must beLike {
        case Right(card) =>
          card.id shouldEqual cardId
          card.cardType shouldEqual cardType
      }
    }
    "return a PersistenceServiceException id collectionId is empty" in new CardServicesScope {

      mockCardRepository.addCard(any, any) returns TaskService(Task(Either.right(repoCard)))
      val result = persistenceServices.addCard(createAddCardRequestWithoutCollectionId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]

    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.addCard(collectionId, repoCardData) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addCard(createAddCardRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAllCards" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.right(items)))
      val result = persistenceServices.deleteAllCards().value.run
      result shouldEqual Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAllCards().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteCard" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      seqRepoCard foreach { repoCard =>
        mockCardRepository.deleteCard(collectionId, repoCard.id) returns TaskService(Task(Either.right(item)))
      }

      val result = persistenceServices.deleteCard(collectionId, card.id).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      seqRepoCard foreach { repoCard =>
        mockCardRepository.deleteCard(collectionId, repoCard.id) returns TaskService(Task(Either.left(exception)))
      }

      val result = persistenceServices.deleteCard(collectionId, card.id).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteCards" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.right(items)))
      val result = persistenceServices.deleteCards(collectionId, Seq(card.id)).value.run
      result shouldEqual Right(items)

    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteCards(collectionId, Seq(card.id)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteCardsByCollection" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.right(items)))
      val result = persistenceServices.deleteCardsByCollection(collectionId).value.run
      result shouldEqual Right(items)
      there was one(mockCardRepository).deleteCards(None, where = s"${CardEntity.collectionId} = $collectionId")
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteCardsByCollection(collectionId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
      there was one(mockCardRepository).deleteCards(None, where = s"${CardEntity.collectionId} = $collectionId")
    }
  }

  "fetchCardsByCollection" should {

    "return a list of Card elements for a valid request" in new CardServicesScope {

      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Either.right(seqRepoCard)))
      }

      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).value.run

      result must beLike {
        case Right(cards) => cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Either.left(exception)))
      }

      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchCards" should {

    "return a list of Card elements for a valid request" in new CardServicesScope {
      mockCardRepository.fetchCards returns TaskService(Task(Either.right(seqRepoCard)))

      val result = persistenceServices.fetchCards.value.run

      result must beLike {
        case Right(cards) => cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.fetchCards returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchCards.value.run

      result must beLike {
        case Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findCardById" should {

    "return a Card for a valid request" in new CardServicesScope {

      mockCardRepository.findCardById(cardId) returns TaskService(Task(Either.right(Option(repoCard))))
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).value.run

      result must beLike {
        case Right(maybeCard) =>
          maybeCard must beSome[Card].which { card =>
            card.cardType shouldEqual cardType
          }
      }
    }

    "return None when a non-existent id is given" in new CardServicesScope {

      mockCardRepository.findCardById(nonExistentCardId) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = nonExistentCardId)).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.findCardById(cardId) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).value.run

      result must beLike {
        case Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCard" should {

    "return the number of elements updated for a valid request" in new CardServicesScope {

      mockCardRepository.updateCard(repoCard) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.updateCard(createUpdateCardRequest()).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.updateCard(repoCard) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateCard(createUpdateCardRequest()).value.run

      result must beLike {
        case Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCards" should {

    "return the sequence with the number of elements updated for a valid request" in new CardServicesScope {

      mockCardRepository.updateCards(seqRepoCard) returns TaskService(Task(Either.right(item to items)))
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).value.run
      result shouldEqual Right(item to items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.updateCards(seqRepoCard) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

}
