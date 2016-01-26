package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.repository.model._
import com.fortysevendeg.ninecardslauncher.repository.provider._

object Conversions {

  def toApp(app: AppEntity): App = App(
    id = app.id,
    data = AppData(
      name = app.data.name,
      packageName = app.data.packageName,
      className = app.data.className,
      category = app.data.category,
      imagePath = app.data.imagePath,
      colorPrimary = app.data.colorPrimary,
      dateInstalled = app.data.dateInstalled,
      dateUpdate = app.data.dateUpdate,
      version = app.data.version,
      installedFromGooglePlay = app.data.installedFromGooglePlay))

  def toCard(cardEntity: CardEntity): Card = Card(
    id = cardEntity.id,
    data = CardData(
      position = cardEntity.data.position,
      micros = cardEntity.data.micros,
      term = cardEntity.data.term,
      packageName = Option[String](cardEntity.data.packageName),
      cardType = cardEntity.data.`type`,
      intent = cardEntity.data.intent,
      imagePath = cardEntity.data.imagePath,
      starRating = Option[Double](cardEntity.data.starRating),
      numDownloads = Option[String](cardEntity.data.numDownloads),
      notification = Option[String](cardEntity.data.notification)))

  def toCollection(collectionEntity: CollectionEntity): Collection = Collection(
    id = collectionEntity.id,
    data = CollectionData(
      position = collectionEntity.data.position,
      name = collectionEntity.data.name,
      collectionType = collectionEntity.data.`type`,
      icon = collectionEntity.data.icon,
      themedColorIndex = collectionEntity.data.themedColorIndex,
      appsCategory = Option[String](collectionEntity.data.appsCategory),
      constrains = Option[String](collectionEntity.data.constrains),
      originalSharedCollectionId = Option[String](collectionEntity.data.originalSharedCollectionId),
      sharedCollectionId = Option[String](collectionEntity.data.sharedCollectionId),
      sharedCollectionSubscribed = Option[Boolean](collectionEntity.data.sharedCollectionSubscribed)))

  def toUser(userEntity: UserEntity): User = User(
    id = userEntity.id,
    data = UserData(
      userId = Option[String](userEntity.data.userId),
      email = Option[String](userEntity.data.email),
      sessionToken = Option[String](userEntity.data.sessionToken),
      installationId = Option[String](userEntity.data.installationId),
      deviceToken = Option[String](userEntity.data.deviceToken),
      androidToken = Option[String](userEntity.data.androidToken)))

  def toDockApp(dockAppEntity: DockAppEntity): DockApp = DockApp(
    id = dockAppEntity.id,
    data = DockAppData(
      name = dockAppEntity.data.name,
      dockType = dockAppEntity.data.dockType,
      intent = dockAppEntity.data.intent,
      imagePath = dockAppEntity.data.imagePath,
      position = dockAppEntity.data.position))
}
