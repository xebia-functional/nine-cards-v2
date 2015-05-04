package com.fortysevendeg.ninecardslauncher.models

import com.fortysevendeg.ninecardslauncher.modules.image.ImageServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.repository.{CardItem, InsertCollectionRequest}
import com.fortysevendeg.ninecardslauncher.ui.commons.CardType._
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsIntent._
import macroid.Logging._

trait AppConversions {

  self : ImageServicesComponent =>

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

  def toCartItemFromUserConfigSeq(items: Seq[UserConfigCollectionItem]): Seq[CardItem] =
    items.zipWithIndex.map (zipped => toCardItem(zipped._1, zipped._2)).flatten

  def toCardItem(item: UserConfigCollectionItem, pos: Int): Option[CardItem] = {
    // TODO We only are working with apps for now
    val packageName = (item.itemType match {
      case App =>
        if (item.metadata.intentExtras.contains(NineCardExtraPackageName)) {
          Option(item.metadata.intentExtras.get(NineCardExtraPackageName))
        } else {
          None
        }
      case _ => None
    }).flatten
    packageName map {
      pn =>
        CardItem(
          position = pos,
          packageName = Option(pn),
          term = item.title,
          imagePath = imageServices.getPath(pn),
          intent = item.metadata.toString,
          `type` = App)
    }
  }

  def toInsertCollectionRequestFromUserConfigSeq(items: Seq[UserConfigCollection]): Seq[InsertCollectionRequest] =
    items.zipWithIndex.map (zipped => toInsertCollectionRequest(zipped._1, zipped._2))

  def toInsertCollectionRequest(userConfigCollection: UserConfigCollection, index: Int): InsertCollectionRequest = {
    val pos = if (index >= NumSpaces) index % NumSpaces else index
    InsertCollectionRequest(
      position = pos,
      name = userConfigCollection.name,
      `type` = userConfigCollection.collectionType,
      icon = userConfigCollection.icon,
      themedColorIndex = pos,
      appsCategory = userConfigCollection.category,
      constrains = None,
      originalSharedCollectionId = userConfigCollection.originalSharedCollectionId,
      sharedCollectionId = userConfigCollection.sharedCollectionId,
      sharedCollectionSubscribed = userConfigCollection.sharedCollectionSubscribed,
      cards = toCartItemFromUserConfigSeq(userConfigCollection.items))
  }

}
