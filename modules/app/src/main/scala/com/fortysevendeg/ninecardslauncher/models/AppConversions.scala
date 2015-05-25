package com.fortysevendeg.ninecardslauncher.models

import com.fortysevendeg.ninecardslauncher.modules.image.ImageServices
import com.fortysevendeg.ninecardslauncher.modules.repository.{CardItem, InsertCollectionRequest}
import com.fortysevendeg.ninecardslauncher.ui.commons.CardType._
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import play.api.libs.json._

class AppConversions(imageServices: ImageServices) {

  def toCartItemFromAppItemSeq(items: Seq[AppItem]): Seq[CardItem] =
    items.zipWithIndex.map (zipped => toCardItem(zipped._1, zipped._2))

  def toCardItem(appItem: AppItem, pos: Int) =
    CardItem(
      position = pos,
      packageName = Some(appItem.packageName),
      term = appItem.name,
      imagePath = appItem.imagePath,
      intent = appItem.intent,
      `type` = App)

  def toInsertCollectionRequestFromUserConfigSeq(items: Seq[UserConfigCollection], packagesNotInstalled: Seq[String]): Seq[InsertCollectionRequest] =
    items.zipWithIndex.map (zipped => toInsertCollectionRequest(zipped._1, zipped._2, packagesNotInstalled))

  def toInsertCollectionRequest(userConfigCollection: UserConfigCollection, index: Int, packagesNotInstalled: Seq[String]): InsertCollectionRequest = {
    val color = if (index >= NumSpaces) index % NumSpaces else index
    InsertCollectionRequest(
      position = index,
      name = userConfigCollection.name,
      `type` = userConfigCollection.collectionType,
      icon = userConfigCollection.icon,
      themedColorIndex = color,
      appsCategory = userConfigCollection.category,
      constrains = None,
      originalSharedCollectionId = userConfigCollection.originalSharedCollectionId,
      sharedCollectionId = userConfigCollection.sharedCollectionId,
      sharedCollectionSubscribed = userConfigCollection.sharedCollectionSubscribed,
      cards = toCartItemFromUserConfigSeq(userConfigCollection.items, packagesNotInstalled))
  }

  def toCartItemFromUserConfigSeq(items: Seq[UserConfigCollectionItem], packagesNotInstalled: Seq[String]): Seq[CardItem] =
    items.zipWithIndex.flatMap(zipped => toCardItem(zipped._1, zipped._2, packagesNotInstalled))

  def toCardItem(item: UserConfigCollectionItem, pos: Int, packagesNotInstalled: Seq[String]): Option[CardItem] = {
    // TODO We only are working with apps for now
    item.itemType match {
      case App =>
        for {
          packageName <- item.metadata.extractPackageName()
          className <- item.metadata.extractClassName()
        } yield {
          import com.fortysevendeg.ninecardslauncher.models.NineCardIntentImplicits._
          val imagePath = packagesNotInstalled find(_ == packageName) map
            imageServices.getPath getOrElse
            imageServices.getImagePath(packageName, className)
          CardItem(
            position = pos,
            packageName = Option(packageName),
            term = item.title,
            imagePath = imagePath,
            intent = Json.toJson(item.metadata).toString(),
            `type` = App)
        }
      case _ => None
    }
  }

}
