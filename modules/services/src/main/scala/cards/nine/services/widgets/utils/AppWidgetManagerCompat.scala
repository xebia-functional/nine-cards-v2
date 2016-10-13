package cards.nine.services.widgets.utils

import cards.nine.models.AppWidget

trait AppWidgetManagerCompat {

  def getAllProviders: Seq[AppWidget]

}
