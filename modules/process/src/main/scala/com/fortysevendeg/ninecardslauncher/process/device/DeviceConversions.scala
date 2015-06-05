package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.process.device.models.AppItem
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

trait DeviceConversions {

  def toAppItemSeq(apps: Seq[Application]) = apps map toAppItem

  def toAppItem(app: Application) = {
    // TODO We need refactor NineCardIntent from Process Layer
//    val intent = new NineCardIntent(NineCardIntentExtras(
//      package_name = Some(app.packageName),
//      class_name = Some(app.className)
//    ))
//    intent.setAction(OpenApp)
    AppItem(
      name = app.name,
      packageName = app.packageName,
      imagePath = "image", // TODO create image
      intent = "{\"intentExtras\":{\"class_name\":\"com.moistrue.zombiesmasher.ZombieSmasherActivity\",\"package_name\":\"com.moistrue.zombiesmasher\"},\"action\":\"com.fortysevendeg.ninecardslauncher.OPEN_APP\",\"extras\":{\"classLoader\":{\"parent\":null},\"pairValue\":\"com.moistrue.zombiesmasher\",\"empty\":false,\"parcelled\":false},\"flags\":0.0,\"excludingStopped\":false}")
  }

}
