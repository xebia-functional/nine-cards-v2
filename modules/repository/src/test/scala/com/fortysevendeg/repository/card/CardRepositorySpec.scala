package com.fortysevendeg.repository.card

import android.net.Uri
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.repository.model.Card
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import scala.language.postfixOps

trait CardRepositorySpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait CardRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val cardRepository = new CardRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]
  }

  trait ValidCardRepositoryResponses
    extends CardRepositoryTestData {

    self: CardRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createInsertCardValues,
      notificationUri = Some(mockUri)) returns testCardId

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testCardId,
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testCardId,
      projection = allFields)(
      f = getEntityFromCursor(cardEntityFromCursor)) returns Some(cardEntity)

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testNonExistingCardId,
      projection = allFields)(
      f = getEntityFromCursor(cardEntityFromCursor)) returns None

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$collectionId = ?",
      whereParams = Seq(testCollectionId.toString),
      orderBy = s"${CardEntity.position} asc")(
      f = getListFromCursor(cardEntityFromCursor)) returns cardEntitySeq

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$collectionId = ?",
      whereParams = Seq(testNonExistingCollectionId.toString),
      orderBy = s"${CardEntity.position} asc")(
      f = getListFromCursor(cardEntityFromCursor)) returns Seq.empty

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = card.id,
      values = createUpdateCardValues,
      notificationUri = Some(mockUri)) returns 1
  }

  trait ValidAllCardsRepositoryResponses
    extends ValidCardRepositoryResponses {

    self: CardRepositoryScope =>

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields)(
      f = getListFromCursor(cardEntityFromCursor)) returns cardEntitySeq
  }

  trait ErrorCardRepositoryResponses
    extends CardRepositoryTestData {

    self: CardRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createInsertCardValues,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testCardId,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testCardId,
      projection = allFields)(
      f = getEntityFromCursor(cardEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$collectionId = ?",
      whereParams = Seq(testCollectionId.toString),
      orderBy = s"${CardEntity.position} asc")(
      f = getListFromCursor(cardEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = card.id,
      values = createUpdateCardValues,
      notificationUri = Some(mockUri)) throws contentResolverException
  }

  trait ErrorAllCardsRepositoryResponses
    extends ErrorCardRepositoryResponses {

    self: CardRepositoryScope =>

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields)(
      f = getListFromCursor(cardEntityFromCursor)) throws contentResolverException
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
    (imagePath, 7, cardSeq map (_.data.imagePath), StringDataType),
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
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.addCard(collectionId = testCollectionId, data = createCardData).value.run

          result must beLike {
            case Xor.Right(cardResult) =>
              cardResult.id shouldEqual testCardId
              cardResult.data.intent shouldEqual testIntent
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.addCard(collectionId = testCollectionId, data = createCardData).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }



    "deleteCards" should {

      "return a successful result when all the cards are deleted" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.deleteCards().value.run

          result must beLike {
            case Xor.Right(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.deleteCards().value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "deleteCard" should {

      "return a successful result when a valid cache category id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.deleteCard(card = card).value.run

          result must beLike {
            case Xor.Right(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.deleteCard(card = card).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "findCardById" should {

      "return a Card object when a existent id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.findCardById(id = testCardId).value.run

          result must beLike {
            case Xor.Right(maybeCard) =>
              maybeCard must beSome[Card].which { card =>
                card.id shouldEqual testCardId
                card.data.intent shouldEqual testIntent
              }
          }
        }

      "return None when a non-existent id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.findCardById(id = testNonExistingCardId).value.run

          result must beLike {
            case Xor.Right(maybeCard) =>
              maybeCard must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.findCardById(id = testCardId).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "fetchCardsByCollection" should {

      "return a Card sequence when a existent collection id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.fetchCardsByCollection(collectionId = testCollectionId).value.run

          result must beLike {
            case Xor.Right(cards) =>
              cards shouldEqual cardSeq
          }
        }

      "fetchCardsByCollection should return an empty sequence when a non-existent collection id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.fetchCardsByCollection(collectionId = testNonExistingCollectionId).value.run

          result must beLike {
            case Xor.Right(cards) =>
              cards should beEmpty
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.fetchCardsByCollection(collectionId = testCollectionId).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "fetchCards" should {

      "return all Cards" in
        new CardRepositoryScope
          with ValidAllCardsRepositoryResponses {

          val result = cardRepository.fetchCards.value.run

          result must beLike {
            case Xor.Right(cards) =>
              cards shouldEqual cardSeq
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorAllCardsRepositoryResponses {

          val result = cardRepository.fetchCards.value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "updateCard" should {

      "return a successful result when the card is updated" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.updateCard(card = card).value.run

          result must beLike {
            case Xor.Right(updated) =>
              updated shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.updateCard(card = card).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
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
