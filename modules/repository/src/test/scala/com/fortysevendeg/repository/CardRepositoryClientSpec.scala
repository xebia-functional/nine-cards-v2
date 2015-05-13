package com.fortysevendeg.repository

import com.fortysevendeg.ninecardslauncher.commons.CardUri
import com.fortysevendeg.ninecardslauncher.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.provider._
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{Card, CardData}
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Random

trait CardMockCursor extends MockCursor with CardTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.Id, 0, cardSeq map (_.id), IntDataType),
    (Position, 1, cardSeq map (_.data.position), IntDataType),
    (CollectionId, 2, cardSeq map (_ => collectionId), IntDataType),
    (Term, 3, cardSeq map (_.data.term), StringDataType),
    (PackageName, 4, cardSeq map (_.data.packageName getOrElse ""), StringDataType),
    (Type, 5, cardSeq map (_.data.`type`), StringDataType),
    (Intent, 6, cardSeq map (_.data.intent), StringDataType),
    (ImagePath, 7, cardSeq map (_.data.imagePath), StringDataType),
    (StarRating, 8, cardSeq map (_.data.starRating getOrElse 0.0d), DoubleDataType),
    (Micros, 9, cardSeq map (_.data.micros), IntDataType),
    (NumDownloads, 10, cardSeq map (_.data.numDownloads getOrElse ""), StringDataType),
    (Notification, 11, cardSeq map (_.data.notification getOrElse ""), StringDataType)
  )

  prepareCursor[Card](cardSeq.size, cursorData)
}

trait EmptyCardMockCursor extends MockCursor with CardTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.Id, 0, Seq.empty, IntDataType),
    (Position, 1, Seq.empty, IntDataType),
    (CollectionId, 2, Seq.empty, IntDataType),
    (Term, 3, Seq.empty, StringDataType),
    (PackageName, 4, Seq.empty, StringDataType),
    (Type, 5, Seq.empty, StringDataType),
    (Intent, 6, Seq.empty, StringDataType),
    (ImagePath, 7, Seq.empty, StringDataType),
    (StarRating, 8, Seq.empty, DoubleDataType),
    (Micros, 9, Seq.empty, IntDataType),
    (NumDownloads, 10, Seq.empty, StringDataType),
    (Notification, 11, Seq.empty, StringDataType)
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
      `type` = `type`,
      intent = intent,
      imagePath = imagePath,
      starRating = starRatingOption,
      micros = micros,
      numDownloads = numDownloadsOption,
      notification = notificationOption)))

  def createCardValues = Map[String, Any](
    Position -> position,
    CollectionId -> collectionId,
    Term -> term,
    PackageName -> (packageNameOption getOrElse ""),
    Type -> `type`,
    Intent -> intent,
    ImagePath -> imagePath,
    StarRating -> (starRatingOption getOrElse 0.0d),
    Micros -> micros,
    NumDownloads -> (numDownloadsOption getOrElse ""),
    Notification -> (notificationOption getOrElse ""))
}

trait CardTestSupport
    extends BaseTestSupport
    with MockContentResolverWrapper
    with CardTestData
    with DBUtils {

  def createAddCardRequest = AddCardRequest(collectionId = collectionId, CardData(
    position = position,
    term = term,
    packageName = packageNameOption,
    `type` = `type`,
    intent = intent,
    imagePath = imagePath,
    starRating = starRatingOption,
    micros = micros,
    numDownloads = numDownloadsOption,
    notification = notificationOption))

  def createDeleteCardRequest = DeleteCardRequest(card = card)

  def createGetCardByIdRequest(id: Int) = GetCardByIdRequest(id = id)

  def createGetCardByCollectionRequest(collectionId: Int) = GetAllCardsByCollectionRequest(collectionId = collectionId)

  def createUpdateCardRequest = UpdateCardRequest(card = card)

  when(contentResolverWrapper.insert(CardUri, createCardValues)).thenReturn(cardId)

  when(contentResolverWrapper.deleteById(CardUri, cardId)).thenReturn(1)

  when(contentResolverWrapper.queryById(
    nineCardsUri = CardUri,
    id = cardId,
    projection = AllFields)(
        f = getEntityFromCursor(cardEntityFromCursor),
        defaultValue = None)).thenReturn(Some(cardEntity))

  when(contentResolverWrapper.queryById(
    nineCardsUri = CardUri,
    id = nonExistingCardId,
    projection = AllFields)(
        f = getEntityFromCursor(cardEntityFromCursor),
        defaultValue = None)).thenReturn(None)

  when(contentResolverWrapper.query(
    nineCardsUri = CardUri,
    projection = AllFields,
    where = s"$CollectionId = ?",
    whereParams = Seq(collectionId.toString))(
        f = getListFromCursor(cardEntityFromCursor),
        defaultValue = Seq.empty)).thenReturn(cardEntitySeq)

  when(contentResolverWrapper.query(
    nineCardsUri = CardUri,
    projection = AllFields,
    where = s"$CollectionId = ?",
    whereParams = Seq(nonExistingCollectionId.toString))(
        f = getListFromCursor(cardEntityFromCursor),
        defaultValue = Seq.empty)).thenReturn(Seq.empty)

  when(contentResolverWrapper.update(CardUri, createCardValues)).thenReturn(1)
}

class CardRepositoryClientSpec
    extends Specification
    with Mockito
    with Scope
    with CardTestSupport
    with CardRepositoryClient {

  "CardRepositoryClient component" should {

    "addCard should return a valid Card object" in {

      val response = await(addCard(createAddCardRequest))

      response.card.get.id shouldEqual cardId
      response.card.get.data.intent shouldEqual intent
    }

    "deleteCard should return a successful response when a valid cache category id is given" in {
      val response = await(deleteCard(createDeleteCardRequest))

      response.success shouldEqual true
    }

    "getCardById should return a Card object when a existing id is given" in {
      val response = await(getCardById(createGetCardByIdRequest(id = cardId)))

      response.result.get.id shouldEqual cardId
      response.result.get.data.intent shouldEqual intent
    }

    "getCardById should return None when a non-existing id is given" in {
      val response = await(getCardById(createGetCardByIdRequest(id = nonExistingCardId)))

      response.result shouldEqual None
    }

    "getCardByCollection should return a Card sequence when a existing collection id is given" in {
      val response = await(getCardByCollection(createGetCardByCollectionRequest(collectionId = collectionId)))

      response.result shouldEqual cardSeq
    }

    "getCardByCollection should return an empty sequence when a non-existing collection id is given" in {
      val response = await(getCardByCollection(createGetCardByCollectionRequest(collectionId = nonExistingCollectionId)))

      response.result shouldEqual Seq.empty
    }

    "updateCard should return a successful response when the card is updated" in {
      val response = await(updateCard(createUpdateCardRequest))

      response.success shouldEqual true
    }

    "getEntityFromCursor should return None when an empty cursor is given" in
        new EmptyCardMockCursor
            with Scope {
          val result = getEntityFromCursor(cardEntityFromCursor)(mockCursor)

          result shouldEqual None
        }

    "getEntityFromCursor should return a Card object when a cursor with data is given" in
        new CardMockCursor
            with Scope {
          val result = getEntityFromCursor(cardEntityFromCursor)(mockCursor)

          result shouldEqual Some(cardEntity)
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
