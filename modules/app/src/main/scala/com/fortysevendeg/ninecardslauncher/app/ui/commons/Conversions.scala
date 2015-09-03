package com.fortysevendeg.ninecardslauncher.app.ui.commons

import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardsIntentExtras, NineCardIntentExtras, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized

trait NineCardIntentConversions {

  def toNineCardIntent(app: AppCategorized): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(app.packageName),
      class_name = Option(app.className)))
    intent.setAction(NineCardsIntentExtras.openApp)
    intent.setClassName(app.packageName, app.className)
    intent
  }

}
