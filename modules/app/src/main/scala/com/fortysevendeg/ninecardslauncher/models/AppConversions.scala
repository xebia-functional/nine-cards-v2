package com.fortysevendeg.ninecardslauncher.models

import com.fortysevendeg.ninecardslauncher.modules.repository.{InsertCollectionRequest, CardItem}
import com.fortysevendeg.ninecardslauncher.ui.commons.CardType

trait AppConversions {

  def toCardItem(appItem: AppItem) =
    CardItem(
      position = 0,
      packageName = Some(appItem.packageName),
      term = appItem.name,
      imagePath = appItem.imagePath,
      intent = appItem.intent,
      `type` = CardType.App
    )

  def toCollection(userConfigCollection: UserConfigCollection): InsertCollectionRequest =
    InsertCollectionRequest(
      position = 0,
      name = userConfigCollection.name,
      `type` = userConfigCollection.collectionType,
      icon = userConfigCollection.icon,
      themedColorIndex = 0,
      appsCategory = userConfigCollection.category,
      constrains = None,
      originalSharedCollectionId = userConfigCollection.originalSharedCollectionId,
      sharedCollectionId = userConfigCollection.sharedCollectionId,
      sharedCollectionSubscribed = userConfigCollection.sharedCollectionSubscribed,
      cards = Seq.empty
    )

}
