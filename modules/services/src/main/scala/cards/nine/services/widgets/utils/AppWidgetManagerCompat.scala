package cards.nine.services.widgets.utils

import cards.nine.models.Widget

trait AppWidgetManagerCompat {

  def getAllProviders: Seq[Widget]

}
