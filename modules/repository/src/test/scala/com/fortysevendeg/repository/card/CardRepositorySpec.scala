package com.fortysevendeg.repository.card

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.commons.{CardUri, ContentResolverWrapperImpl}
import com.fortysevendeg.ninecardslauncher.repository.model.Card
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait CardRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait CardRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]
    lazy val cardRepository = new CardRepository(contentResolverWrapper)
  }

  trait ValidCardRepositoryResponses
    extends DBUtils
    with CardRepositoryTestData {

    self: CardRepositoryScope =>

    contentResolverWrapper.insert(CardUri, createInsertCardValues) returns testCardId

    contentResolverWrapper.deleteById(CardUri, testCardId) returns 1

    contentResolverWrapper.findById(
      nineCardsUri = CardUri,
      id = testCardId,
      projection = allFields)(
        f = getEntityFromCursor(cardEntityFromCursor)) returns Some(cardEntity)

    contentResolverWrapper.findById(
      nineCardsUri = CardUri,
      id = testNonExistingCardId,
      projection = allFields)(
        f = getEntityFromCursor(cardEntityFromCursor)) returns None

    contentResolverWrapper.fetchAll(
      nineCardsUri = CardUri,
      projection = allFields,
      where = s"$collectionId = ?",
      whereParams = Seq(testCollectionId.toString))(
        f = getListFromCursor(cardEntityFromCursor)) returns cardEntitySeq

    contentResolverWrapper.fetchAll(
      nineCardsUri = CardUri,
      projection = allFields,
      where = s"$collectionId = ?",
      whereParams = Seq(testNonExistingCollectionId.toString))(
        f = getListFromCursor(cardEntityFromCursor)) returns Seq.empty

    contentResolverWrapper.updateById(nineCardsUri = CardUri, id = card.id, values = createUpdateCardValues) returns 1
  }

  trait ErrorCardRepositoryResponses
    extends DBUtils
    with CardRepositoryTestData {

    self: CardRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    contentResolverWrapper.insert(CardUri, createInsertCardValues) throws contentResolverException

    contentResolverWrapper.deleteById(CardUri, testCardId) throws contentResolverException

    contentResolverWrapper.findById(
      nineCardsUri = CardUri,
      id = testCardId,
      projection = allFields)(
        f = getEntityFromCursor(cardEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      nineCardsUri = CardUri,
      projection = allFields,
      where = s"$collectionId = ?",
      whereParams = Seq(testCollectionId.toString))(
        f = getListFromCursor(cardEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(nineCardsUri = CardUri, id = card.id, values = createUpdateCardValues) throws contentResolverException
  }

}

trait CardMockCursor
  extends MockCursor
  with DBUtils
  with CardRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, cardSeq map (_.id), IntDataType),
    (position, 1, cardSeq map (_.data.position), IntDataType),
    (collectionId, 2, cardSeq map (_ => testCollectionId), IntDataType),
    (term, 3, cardSeq map (_.data.term), StringDataType),
    (packageName, 4, cardSeq map (_.data.packageName getOrElse ""), StringDataType),
    (cardType, 5, cardSeq map (_.data.cardType), StringDataType),
    (intent, 6, cardSeq map (_.data.intent), StringDataType),
    (imagePath, 7, cardSeq map (_.data.imagePath), StringDataType),
    (starRating, 8, cardSeq map (_.data.starRating getOrElse 0.0d), DoubleDataType),
    (micros, 9, cardSeq map (_.data.micros), IntDataType),
    (numDownloads, 10, cardSeq map (_.data.numDownloads getOrElse ""), StringDataType),
    (notification, 11, cardSeq map (_.data.notification getOrElse ""), StringDataType)
  )

  prepareCursor[Card](cardSeq.size, cursorData)
}

trait EmptyCardMockCursor
  extends MockCursor
  with DBUtils
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
    (starRating, 8, Seq.empty, DoubleDataType),
    (micros, 9, Seq.empty, IntDataType),
    (numDownloads, 10, Seq.empty, StringDataType),
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

          val result = cardRepository.addCard(collectionId = testCollectionId, data = createCardData).run

          result must be_\/-[Card].which {
            card =>
              card.id shouldEqual testCardId
              card.data.intent shouldEqual testIntent
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.addCard(collectionId = testCollectionId, data = createCardData).run

          result must be_-\/[NineCardsException]
        }
    }

    "deleteCard" should {

      "return a successful result when a valid cache category id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.deleteCard(card = card).run

          result must be_\/-[Int].which(_ shouldEqual 1)
        }

      "return a NineCardsException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.deleteCard(card = card).run

          result must be_-\/[NineCardsException]
        }
    }

    "findCardById" should {

      "return a Card object when a existent id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.findCardById(id = testCardId).run

          result must be_\/-[Option[Card]].which {
            maybeCard =>
              maybeCard must beSome[Card].which { card =>
                card.id shouldEqual testCardId
                card.data.intent shouldEqual testIntent
              }
          }
        }

      "return None when a non-existent id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.findCardById(id = testNonExistingCardId).run

          result must be_\/-[Option[Card]].which(_ must beNone)
        }

      "return a NineCardsException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.findCardById(id = testCardId).run

          result must be_-\/[NineCardsException]
        }
    }

    "fetchCardsByCollection" should {

      "return a Card sequence when a existent collection id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.fetchCardsByCollection(collectionId = testCollectionId).run

          result must be_\/-[Seq[Card]].which(_ shouldEqual cardSeq)
        }

      "fetchCardsByCollection should return an empty sequence when a non-existent collection id is given" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.fetchCardsByCollection(collectionId = testNonExistingCollectionId).run

          result must be_\/-[Seq[Card]].which {
            cards =>
              cards shouldEqual Seq.empty
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.fetchCardsByCollection(collectionId = testCollectionId).run

          result must be_-\/[NineCardsException]
        }
    }

    "updateCard" should {

      "return a successful result when the card is updated" in
        new CardRepositoryScope
          with ValidCardRepositoryResponses {

          val result = cardRepository.updateCard(card = card).run

          result must be_\/-[Int].which(_ shouldEqual 1)
        }

      "return a NineCardsException when a exception is thrown" in
        new CardRepositoryScope
          with ErrorCardRepositoryResponses {

          val result = cardRepository.updateCard(card = card).run

          result must be_-\/[NineCardsException]
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

          result shouldEqual Seq.empty
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
