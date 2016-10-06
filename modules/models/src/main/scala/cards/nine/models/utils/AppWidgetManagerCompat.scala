package cards.nine.models.utils

import cards.nine.models.Widget

trait AppWidgetManagerCompat {

  def getAllProviders: Seq[Widget]

}
