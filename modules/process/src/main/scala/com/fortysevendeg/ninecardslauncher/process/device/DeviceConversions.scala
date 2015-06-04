package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.process.device.models.AppItem
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

trait DeviceConversions {

  def toAppItemSeq(apps: Seq[Application]) = apps map toAppItem

  def toAppItem(app: Application) = {
//    val intent = new NineCardIntent(NineCardIntentExtras(
//      package_name = Some(app.packageName),
//      class_name = Some(app.className)
//    ))
//    intent.setAction(OpenApp)
    AppItem(
      name = app.name,
      packageName = app.packageName,
      imagePath = "image", // TODO create image
      intent = "")
  }

}
