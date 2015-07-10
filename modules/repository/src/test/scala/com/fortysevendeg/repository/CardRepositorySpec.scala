package com.fortysevendeg.repository

import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.commons.{CardUri, ContentResolverWrapperImpl}
import com.fortysevendeg.ninecardslauncher.repository.model.{Card, CardData}
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity.cardEntityFromCursor
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Random

trait CardMockCursor extends MockCursor with CardTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, cardSeq map (_.id), IntDataType),
    (CardEntity.position, 1, cardSeq map (_.data.position), IntDataType),
    (CardEntity.collectionId, 2, cardSeq map (_ => collectionId), IntDataType),
    (CardEntity.term, 3, cardSeq map (_.data.term), StringDataType),
    (CardEntity.packageName, 4, cardSeq map (_.data.packageName getOrElse ""), StringDataType),
    (CardEntity.cardType, 5, cardSeq map (_.data.cardType), StringDataType),
    (CardEntity.intent, 6, cardSeq map (_.data.intent), StringDataType),
    (CardEntity.imagePath, 7, cardSeq map (_.data.imagePath), StringDataType),
    (CardEntity.starRating, 8, cardSeq map (_.data.starRating getOrElse 0.0d), DoubleDataType),
    (CardEntity.micros, 9, cardSeq map (_.data.micros), IntDataType),
    (CardEntity.numDownloads, 10, cardSeq map (_.data.numDownloads getOrElse ""), StringDataType),
    (CardEntity.notification, 11, cardSeq map (_.data.notification getOrElse ""), StringDataType)
  )

  prepareCursor[Card](cardSeq.size, cursorData)
}

trait EmptyCardMockCursor extends MockCursor with CardTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (CardEntity.position, 1, Seq.empty, IntDataType),
    (CardEntity.collectionId, 2, Seq.empty, IntDataType),
    (CardEntity.term, 3, Seq.empty, StringDataType),
    (CardEntity.packageName, 4, Seq.empty, StringDataType),
    (CardEntity.cardType, 5, Seq.empty, StringDataType),
    (CardEntity.intent, 6, Seq.empty, StringDataType),
    (CardEntity.imagePath, 7, Seq.empty, StringDataType),
    (CardEntity.starRating, 8, Seq.empty, DoubleDataType),
    (CardEntity.micros, 9, Seq.empty, IntDataType),
    (CardEntity.numDownloads, 10, Seq.empty, StringDataType),
    (CardEntity.notification, 11, Seq.empty, StringDataType)
  )

  prepareCursor[Card](0, cursorData)
}

trait CardTestData {
  val cardId = Random.nextInt(10)
  val nonExistingCardId = 15
  val position = Random.nextInt(10)
  val collectionId = Random.nextInt(10)
  val nonExistingCollectionId = 15
  val term = Random.nextString(5)
  val packageName = Random.nextString(5)
  val `type` = Random.nextString(5)
  val intent = Random.nextString(5)
  val imagePath = Random.nextString(5)
  val starRating = Random.nextDouble()
  val micros = Random.nextInt(5)
  val numDownloads = Random.nextString(10)
  val notification = Random.nextString(10)
  val packageNameOption = Option(packageName)
  val starRatingOption = Option(starRating)
  val numDownloadsOption = Option(numDownloads)
  val notificationOption = Option(notification)

  val cardEntitySeq = createCardEntitySeq(5)
  val cardEntity = cardEntitySeq.head
  val cardSeq = createCardSeq(5)
  val card = cardSeq.head

  def createCardEntitySeq(num: Int) = (0 until num) map (i => CardEntity(
    id = cardId + i,
    data = CardEntityData(
      position = position,
      collectionId = collectionId,
      term = term,
      packageName = packageName,
      `type` = `type`,
      intent = intent,
      imagePath = imagePath,
      starRating = starRating,
      micros = micros,
      numDownloads = numDownloads,
      notification = notification)))

  def createCardSeq(num: Int) = (0 until num) map (i => Card(
    id = cardId + i,
    data = CardData(
      position = position,
      term = term,
      packageName = packageNameOption,
      cardType = `type`,
      intent = intent,
      imagePath = imagePath,
      starRating = starRatingOption,
      micros = micros,
      numDownloads = numDownloadsOption,
      notification = notificationOption)))

  def createInsertCardValues = Map[String, Any](
    CardEntity.position -> position,
    CardEntity.collectionId -> collectionId,
    CardEntity.term -> term,
    CardEntity.packageName -> (packageNameOption getOrElse ""),
    CardEntity.cardType -> `type`,
    CardEntity.intent -> intent,
    CardEntity.imagePath -> imagePath,
    CardEntity.starRating -> (starRatingOption getOrElse 0.0d),
    CardEntity.micros -> micros,
    CardEntity.numDownloads -> (numDownloadsOption getOrElse ""),
    CardEntity.notification -> (notificationOption getOrElse ""))

  def createUpdateCardValues = Map[String, Any](
    CardEntity.position -> position,
    CardEntity.term -> term,
    CardEntity.packageName -> (packageNameOption getOrElse ""),
    CardEntity.cardType -> `type`,
    CardEntity.intent -> intent,
    CardEntity.imagePath -> imagePath,
    CardEntity.starRating -> (starRatingOption getOrElse 0.0d),
    CardEntity.micros -> micros,
    CardEntity.numDownloads -> (numDownloadsOption getOrElse ""),
    CardEntity.notification -> (notificationOption getOrElse ""))
}

trait CardTestSupport
  extends BaseTestSupport
  with CardTestData
  with DBUtils
  with Mockito {

  lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]
  lazy val cardRepository = new CardRepository(contentResolverWrapper)

  def createAddCardRequest = AddCardRequest(collectionId = collectionId, CardData(
    position = position,
    term = term,
    packageName = packageNameOption,
    cardType = `type`,
    intent = intent,
    imagePath = imagePath,
    starRating = starRatingOption,
    micros = micros,
    numDownloads = numDownloadsOption,
    notification = notificationOption))

  def createDeleteCardRequest = DeleteCardRequest(card = card)

  def createGetCardByIdRequest(id: Int) = FindCardByIdRequest(id = id)

  def createGetCardByCollectionRequest(collectionId: Int) = FetchCardsByCollectionRequest(collectionId = collectionId)

  def createUpdateCardRequest = UpdateCardRequest(card = card)

  when(contentResolverWrapper.insert(CardUri, createInsertCardValues)).thenReturn(cardId)

  when(contentResolverWrapper.deleteById(CardUri, cardId)).thenReturn(1)

  when(contentResolverWrapper.findById(
    nineCardsUri = CardUri,
    id = cardId,
    projection = CardEntity.allFields)(
      f = getEntityFromCursor(cardEntityFromCursor))).thenReturn(Some(cardEntity))

  when(contentResolverWrapper.findById(
    nineCardsUri = CardUri,
    id = nonExistingCardId,
    projection = CardEntity.allFields)(
      f = getEntityFromCursor(cardEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CardUri,
    projection = CardEntity.allFields,
    where = s"${CardEntity.collectionId} = ?",
    whereParams = Seq(collectionId.toString))(
      f = getListFromCursor(cardEntityFromCursor))).thenReturn(cardEntitySeq)

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CardUri,
    projection = CardEntity.allFields,
    where = s"${CardEntity.collectionId} = ?",
    whereParams = Seq(nonExistingCollectionId.toString))(
      f = getListFromCursor(cardEntityFromCursor))).thenReturn(Seq.empty)

  when(contentResolverWrapper.updateById(nineCardsUri = CardUri, id = card.id, values = createUpdateCardValues)).thenReturn(1)
}

class CardRepositorySpec
  extends Specification
  with Mockito
  with Scope
  with CardTestSupport {

  "CardRepositoryClient component" should {

    "addCard should return a valid Card object" in {

      val response = await(cardRepository.addCard(createAddCardRequest))

      response.card.id shouldEqual cardId
      response.card.data.intent shouldEqual intent
    }

    "deleteCard should return a successful response when a valid cache category id is given" in {
      val response = await(cardRepository.deleteCard(createDeleteCardRequest))

      response.deleted shouldEqual 1
    }

    "findCardById should return a Card object when a existing id is given" in {
      val response = await(cardRepository.findCardById(createGetCardByIdRequest(id = cardId)))

      response.card must beSome[Card].which { card =>
        card.id shouldEqual cardId
        card.data.intent shouldEqual intent
      }
    }

    "findCardById should return None when a non-existing id is given" in {
      val response = await(cardRepository.findCardById(createGetCardByIdRequest(id = nonExistingCardId)))

      response.card must beNone
    }

    "getCardByCollection should return a Card sequence when a existing collection id is given" in {
      val response = await(cardRepository.fetchCardsByCollection(createGetCardByCollectionRequest(collectionId = collectionId)))

      response.cards shouldEqual cardSeq
    }

    "getCardByCollection should return an empty sequence when a non-existing collection id is given" in {
      val response = await(cardRepository.fetchCardsByCollection(createGetCardByCollectionRequest(collectionId = nonExistingCollectionId)))

      response.cards shouldEqual Seq.empty
    }

    "updateCard should return a successful response when the card is updated" in {
      val response = await(cardRepository.updateCard(createUpdateCardRequest))

      response.updated shouldEqual 1
    }

    "getEntityFromCursor should return None when an empty cursor is given" in
      new EmptyCardMockCursor
        with Scope {
        val result = getEntityFromCursor(cardEntityFromCursor)(mockCursor)

        result must beNone
      }

    "getEntityFromCursor should return a Card object when a cursor with data is given" in
      new CardMockCursor
        with Scope {
        val result = getEntityFromCursor(cardEntityFromCursor)(mockCursor)

        result must beSome[CardEntity].which { card =>
          card.id shouldEqual cardEntity.id
          card.data shouldEqual cardEntity.data
        }
      }

    "getListFromCursor should return an empty sequence when an empty cursor is given" in
      new EmptyCardMockCursor
        with Scope {
        val result = getListFromCursor(cardEntityFromCursor)(mockCursor)

        result shouldEqual Seq.empty
      }

    "getListFromCursor should return a Card sequence when a cursor with data is given" in
      new CardMockCursor
        with Scope {
        val result = getListFromCursor(cardEntityFromCursor)(mockCursor)

        result shouldEqual cardEntitySeq
      }
  }
}
