package cards.nine.services.widgets.utils

import cards.nine.services.widgets.models.Widget

trait AppWidgetManagerCompat {

  def getAllProviders: Seq[Widget]

}
