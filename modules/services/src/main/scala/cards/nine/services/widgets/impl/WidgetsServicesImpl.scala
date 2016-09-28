package cards.nine.services.widgets.impl

import android.os.Build
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.services.widgets.models.Conversions
import cards.nine.services.widgets.utils.AppWidgetManagerCompat
import cards.nine.services.widgets.utils.impl.{AppWidgetManagerImplDefault, AppWidgetManagerImplLollipop}
import cards.nine.services.widgets.{ImplicitsWidgetsExceptions, WidgetServicesException, WidgetsServices}

class WidgetsServicesImpl
  extends WidgetsServices
  with ImplicitsWidgetsExceptions {

  override def getWidgets(implicit context: ContextSupport) =
    TaskService {
      CatchAll[WidgetServicesException] {
        val appWidgetManager = getAppWidgetManager
        appWidgetManager.getAllProviders

      }
    }

  protected def getAppWidgetManager(implicit context: ContextSupport): AppWidgetManagerCompat with Conversions = {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) new AppWidgetManagerImplLollipop
    else new AppWidgetManagerImplDefault
  }
}
