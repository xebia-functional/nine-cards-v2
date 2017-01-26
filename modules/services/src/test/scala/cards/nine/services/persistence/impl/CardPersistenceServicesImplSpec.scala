/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.CardTestData
import cards.nine.commons.test.data.CardValues._
import cards.nine.models.Card
import cards.nine.repository.RepositoryException
import cards.nine.repository.provider.CardEntity
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.services.persistence.data.CardPersistenceServicesData
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

trait CardPersistenceServicesDataSpecification extends Specification with DisjunctionMatchers {

  trait CardServicesScope
      extends RepositoryServicesScope
      with CardTestData
      with CardPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class CardPersistenceServicesImplSpec extends CardPersistenceServicesDataSpecification {

  "addCard" should {

    "return a Card value for a valid request" in new CardServicesScope {

      mockCardRepository.addCard(any, any) returns TaskService(Task(Either.right(repoCard)))
      val result = persistenceServices.addCard(cardCollectionId, cardData).value.run

      result must beLike {
        case Right(card) =>
          card.id shouldEqual cardId
          card.cardType.name shouldEqual cardType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.addCard(any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addCard(cardCollectionId, cardData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAllCards" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(
        Task(Either.right(seqRepoCard.size)))
      val result = persistenceServices.deleteAllCards().value.run
      result shouldEqual Right(seqRepoCard.size)
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
        mockCardRepository.deleteCard(cardCollectionId, repoCard.id) returns TaskService(
          Task(Either.right(deletedCard)))
      }

      val result = persistenceServices.deleteCard(cardCollectionId, card.id).value.run
      result shouldEqual Right(deletedCard)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      seqRepoCard foreach { repoCard =>
        mockCardRepository.deleteCard(cardCollectionId, repoCard.id) returns TaskService(
          Task(Either.left(exception)))
      }

      val result = persistenceServices.deleteCard(cardCollectionId, card.id).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteCards" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(
        Task(Either.right(deletedCards)))
      val result = persistenceServices.deleteCards(cardCollectionId, Seq(card.id)).value.run
      result shouldEqual Right(deletedCards)

    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteCards(cardCollectionId, Seq(card.id)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteCardsByCollection" should {

    "return the number of elements deleted for a valid request" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(
        Task(Either.right(deletedCards)))
      val result = persistenceServices.deleteCardsByCollection(cardCollectionId).value.run
      result shouldEqual Right(deletedCards)
      there was one(mockCardRepository)
        .deleteCards(None, where = s"${CardEntity.collectionId} = $cardCollectionId")
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteCardsByCollection(cardCollectionId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
      there was one(mockCardRepository)
        .deleteCards(None, where = s"${CardEntity.collectionId} = $cardCollectionId")
    }
  }

  "fetchCardsByCollection" should {

    "return a list of Card elements for a valid request" in new CardServicesScope {

      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(cardCollectionId + index) returns TaskService(
          Task(Either.right(seqRepoCard)))
      }

      val result = persistenceServices.fetchCardsByCollection(cardCollectionId).value.run

      result must beLike {
        case Right(cards) => cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(cardCollectionId + index) returns TaskService(
          Task(Either.left(exception)))
      }

      val result = persistenceServices.fetchCardsByCollection(cardCollectionId).value.run
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

      mockCardRepository.findCardById(cardId) returns TaskService(
        Task(Either.right(Option(repoCard))))
      val result = persistenceServices.findCardById(cardId).value.run

      result must beLike {
        case Right(maybeCard) =>
          maybeCard must beSome[Card].which { card =>
            card.cardType.name shouldEqual cardType
          }
      }
    }

    "return None when a non-existent id is given" in new CardServicesScope {

      mockCardRepository.findCardById(nonExistentCardId) returns TaskService(
        Task(Either.right(None)))
      val result = persistenceServices.findCardById(nonExistentCardId).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.findCardById(cardId) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findCardById(cardId).value.run

      result must beLike {
        case Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCard" should {

    "return the number of elements updated for a valid request" in new CardServicesScope {

      mockCardRepository.updateCard(any) returns TaskService(Task(Either.right(deletedCard)))
      val result = persistenceServices.updateCard(card).value.run
      result shouldEqual Right(deletedCard)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.updateCard(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateCard(card).value.run

      result must beLike {
        case Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCards" should {

    "return the sequence with the number of elements updated for a valid request" in new CardServicesScope {

      mockCardRepository.updateCards(any) returns TaskService(
        Task(Either.right(deletedCard to deletedCards)))
      val result = persistenceServices.updateCards(seqCard).value.run
      result shouldEqual Right(deletedCard to deletedCards)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CardServicesScope {

      mockCardRepository.updateCards(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateCards(seqCard).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

}
