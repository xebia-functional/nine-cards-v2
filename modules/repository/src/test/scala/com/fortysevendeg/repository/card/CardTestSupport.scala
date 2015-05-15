package com.fortysevendeg.repository.card

import com.fortysevendeg.ninecardslauncher.commons.CardUri
import com.fortysevendeg.ninecardslauncher.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils._
import com.fortysevendeg.ninecardslauncher.provider.{CardEntity, CardEntityData, NineCardsSqlHelper}
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{Card, CardData}
import com.fortysevendeg.ninecardslauncher.repository.repositories.CardRepositoryClient
import com.fortysevendeg.repository._
import org.mockito.Mockito._
import org.specs2.specification.Scope

import scala.util.Random

trait CardTestSupport
    extends BaseTestSupport
    with CardRepositoryClient
    with CardTestData
    with MockContentResolverWrapper
    with Scope

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

  def createInsertCardValues = Map[String, Any](
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

  def createUpdateCardValues = Map[String, Any](
    Position -> position,
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

trait CardMockCursor extends MockCursor with CardTestData with Scope {

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

trait EmptyCardMockCursor extends MockCursor with CardTestData with Scope {

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

trait AddCardSupport extends CardTestSupport {

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

  when(contentResolverWrapper.insert(CardUri, createInsertCardValues)).thenReturn(cardId)
}

trait DeleteCardSupport extends CardTestSupport {

  def createDeleteCardRequest = DeleteCardRequest(card = card)

  when(contentResolverWrapper.deleteById(CardUri, cardId)).thenReturn(1)
}

trait FindCardByIdSupport extends CardTestSupport {

  def createFindCardByIdRequest(id: Int) = FindCardByIdRequest(id = id)

  when(contentResolverWrapper.findById(
    nineCardsUri = CardUri,
    id = cardId,
    projection = AllFields)(
        f = getEntityFromCursor(cardEntityFromCursor))).thenReturn(Some(cardEntity))

  when(contentResolverWrapper.findById(
    nineCardsUri = CardUri,
    id = nonExistingCardId,
    projection = AllFields)(
        f = getEntityFromCursor(cardEntityFromCursor))).thenReturn(None)
}

trait FetchCardsByCollectionSupport extends CardTestSupport {

  def createFetchCardsByCollectionRequest(collectionId: Int) = FetchCardsByCollectionRequest(collectionId = collectionId)

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CardUri,
    projection = AllFields,
    where = s"$CollectionId = ?",
    whereParams = Seq(collectionId.toString))(
        f = getListFromCursor(cardEntityFromCursor))).thenReturn(cardEntitySeq)

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CardUri,
    projection = AllFields,
    where = s"$CollectionId = ?",
    whereParams = Seq(nonExistingCollectionId.toString))(
        f = getListFromCursor(cardEntityFromCursor))).thenReturn(Seq.empty)
}

trait UpdateCardSupport extends CardTestSupport {

  def createUpdateCardRequest = UpdateCardRequest(card = card)

  when(contentResolverWrapper.updateById(nineCardsUri = CardUri, id = card.id, values = createUpdateCardValues)).thenReturn(1)
}