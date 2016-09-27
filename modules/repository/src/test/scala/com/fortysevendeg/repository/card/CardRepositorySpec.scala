package com.fortysevendeg.repository.card

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.Card
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity.allFields
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity.position
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.language.postfixOps
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.test.repository.{IntDataType, MockCursor, StringDataType}

trait CardRepositorySpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait CardRepositoryScope
    extends Scope
      with CardRepositoryTestData {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val cardRepository = new CardRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]

    lazy val mockUriBuilt = mock[Uri]

    lazy val mockUriBuilder = mock[Uri.Builder]

    uriCreator.parse(any) returns mockUri

    uriCreator.withAppendedPath(any, any) returns mockUriBuilt

    val contentResolverException = new RuntimeException("Irrelevant message")
  }

}

trait CardMockCursor
  extends MockCursor
    with CardRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, cardSeq map (_.id), IntDataType),
    (position, 1, cardSeq map (_.data.position), IntDataType),
    (collectionId, 2, cardSeq map (_ => testCollectionId), IntDataType),
    (term, 3, cardSeq map (_.data.term), StringDataType),
    (packageName, 4, cardSeq map (_.data.packageName orNull), StringDataType),
    (cardType, 5, cardSeq map (_.data.cardType), StringDataType),
    (intent, 6, cardSeq map (_.data.intent), StringDataType),
    (imagePath, 7, cardSeq map (_.data.imagePath orNull), StringDataType),
    (notification, 11, cardSeq map (_.data.notification orNull), StringDataType)
  )

  prepareCursor[Card](cardSeq.size, cursorData)
}

trait EmptyCardMockCursor
  extends MockCursor
    with CardRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (position, 1, Seq.empty, IntDataType),
    (collectionId, 2, Seq.empty, IntDataType),
    (term, 3, Seq.empty, StringDataType),
    (packageName, 4, Seq.empty, StringDataType),
    (cardType, 5, Seq.empty, StringDataType),
    (intent, 6, Seq.empty, StringDataType),
    (imagePath, 7, Seq.empty, StringDataType),
    (notification, 11, Seq.empty, StringDataType)
  )

  prepareCursor[Card](0, cursorData)
}

class CardRepositorySpec
  extends CardRepositorySpecification {

  "CardRepositoryClient component" should {

    "addCard" should {

      "return a Card object with a valid request" in
        new CardRepositoryScope {

          contentResolverWrapper.insert(any, any, any) returns testCardId
          val result = cardRepository.addCard(collectionId = testCollectionId, data = createCardData).value.run

          result must beLike {
            case Right(cardResult) =>
              cardResult.id shouldEqual testCardId
              cardResult.data.intent shouldEqual testIntent
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.insert(any, any, any) throws contentResolverException
          val result = cardRepository.addCard(collectionId = testCollectionId, data = createCardData).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "addCards" should {

      "return a sequence of addCard objects with a valid request" in
        new CardRepositoryScope {

          contentResolverWrapper.inserts(any,any,any,any) returns cardIdSeq
          val result = cardRepository.addCards(datas = cardsWithCollectionIdSeq).value.run

          result must beLike{
            case Right(cards) =>
              cards map (_.id) shouldEqual cardIdSeq
              cards map (_.data.packageName) shouldEqual (cardDataSeq map (_.packageName))
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.inserts(any, any, any, any) throws contentResolverException
          val result = cardRepository.addCards(datas = cardsWithCollectionIdSeq).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }

    }


    "deleteCards" should {

      "return a successful result when all the cards are deleted" in
        new CardRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) returns 1
          val result = cardRepository.deleteCards().value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) throws contentResolverException
          val result = cardRepository.deleteCards().value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "deleteCard" should {

      "return a successful result when a valid cache category id is given" in
        new CardRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) returns 1
          val result = cardRepository.deleteCard(testCollectionId, card.id).value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) throws contentResolverException
          val result = cardRepository.deleteCard(testCollectionId, card.id).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "findCardById" should {

      "return a Card object when a existent id is given" in
        new CardRepositoryScope {

          contentResolverWrapper.findById[CardEntity](any, any, any, any, any, any)(any) returns Some(cardEntity)
          val result = cardRepository.findCardById(id = testCardId).value.run

          result must beLike {
            case Right(maybeCard) =>
              maybeCard must beSome[Card].which { card =>
                card.id shouldEqual testCardId
                card.data.intent shouldEqual testIntent
              }
          }
        }

      "return None when a non-existent id is given" in
        new CardRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) returns None
          val result = cardRepository.findCardById(id = testNonExistingCardId).value.run
          result shouldEqual Right(None)
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) throws contentResolverException
          val result = cardRepository.findCardById(id = testCardId).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchCardsByCollection" should {

      "return a Card sequence when a existent collection id is given" in
        new CardRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$collectionId = ?",
            whereParams = Seq(testCollectionId.toString),
            orderBy = s"${CardEntity.position} asc")(
            f = getListFromCursor(cardEntityFromCursor)) returns cardEntitySeq

          val result = cardRepository.fetchCardsByCollection(collectionId = testCollectionId).value.run
          result shouldEqual Right(cardSeq)

        }

      "fetchCardsByCollection should return an empty sequence when a non-existent collection id is given" in
        new CardRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$collectionId = ?",
            whereParams = Seq(testNonExistingCollectionId.toString),
            orderBy = s"${CardEntity.position} asc")(
            f = getListFromCursor(cardEntityFromCursor)) returns Seq.empty

          val result = cardRepository.fetchCardsByCollection(collectionId = testNonExistingCollectionId).value.run
          result shouldEqual Right(Seq.empty)

        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$collectionId = ?",
            whereParams = Seq(testCollectionId.toString),
            orderBy = s"${CardEntity.position} asc")(
            f = getListFromCursor(cardEntityFromCursor)) throws contentResolverException

          val result = cardRepository.fetchCardsByCollection(collectionId = testCollectionId).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchCards" should {

      "return all Cards" in
        new CardRepositoryScope {
          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields)(
            f = getListFromCursor(cardEntityFromCursor)) returns cardEntitySeq

          val result = cardRepository.fetchCards.value.run
          result shouldEqual Right(cardSeq)

        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields)(
            f = getListFromCursor(cardEntityFromCursor)) throws contentResolverException

          val result = cardRepository.fetchCards.value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchIterableCards" should {

      "return an IterableCursor[Card] " in
        new CardMockCursor with CardRepositoryScope {

          contentResolverWrapper.getCursor(any, any, any, any, any) returns mockCursor

          val result = cardRepository.fetchIterableCards(where = testMockWhere).value.run

          result must beLike {
            case Right(iterator) =>
              toSeq(iterator) shouldEqual cardSeq
          }

          there was one(contentResolverWrapper).getCursor(
            mockUri,
            AppEntity.allFields,
            testMockWhere,
            Seq.empty,
            "")
        }

      "return an a RepositoryException when a exception is thrown " in
        new CardMockCursor with CardRepositoryScope {

          contentResolverWrapper.getCursor(any, any, any, any, any) throws contentResolverException

          val result = cardRepository.fetchIterableCards(where = testMockWhere).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "updateCard" should {

      "return a successful result when the card is updated" in
        new CardRepositoryScope {

          contentResolverWrapper.updateById(
            uri = mockUri,
            id = card.id,
            values = createUpdateCardValues,
            notificationUris = Seq(mockUri)) returns 1

          val result = cardRepository.updateCard(card = card).value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.updateById(
            uri = mockUri,
            id = card.id,
            values = createUpdateCardValues,
            notificationUris = Seq(mockUri)) throws contentResolverException

          val result = cardRepository.updateCard(card = card).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }


    "updateCards" should {

      "return a successful result when the updateCard are updated" in
        new CardRepositoryScope {

          contentResolverWrapper.updateByIds(any, any, any, any) returns Seq(5)
          val result = cardRepository.updateCards(cards = cardSeq).value.run
          result shouldEqual Right(Seq(5))
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope {

          contentResolverWrapper.updateByIds(any, any, any, any) throws contentResolverException
          val result = cardRepository.updateCards(cards = cardSeq).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "getEntityFromCursor" should {

      "return None when an empty cursor is given" in
        new EmptyCardMockCursor
          with CardRepositoryScope {

          val result = getEntityFromCursor(cardEntityFromCursor)(mockCursor)

          result must beNone
        }

      "return a Card object when a cursor with data is given" in
        new CardMockCursor
          with CardRepositoryScope {

          val result = getEntityFromCursor(cardEntityFromCursor)(mockCursor)

          result must beSome[CardEntity].which {
            card =>
              card.id shouldEqual cardEntity.id
              card.data shouldEqual cardEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyCardMockCursor
          with CardRepositoryScope {

          val result = getListFromCursor(cardEntityFromCursor)(mockCursor)

          result should beEmpty
        }

      "return a Card sequence when a cursor with data is given" in
        new CardMockCursor
          with CardRepositoryScope {

          val result = getListFromCursor(cardEntityFromCursor)(mockCursor)

          result shouldEqual cardEntitySeq
        }
    }
  }

}
