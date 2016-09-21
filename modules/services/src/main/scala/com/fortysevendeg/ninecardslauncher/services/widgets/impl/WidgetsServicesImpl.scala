package com.fortysevendeg.ninecardslauncher.services.widgets.impl

import android.os.Build
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.widgets.models.Conversions
import com.fortysevendeg.ninecardslauncher.services.widgets.utils.AppWidgetManagerCompat
import com.fortysevendeg.ninecardslauncher.services.widgets.utils.impl.{AppWidgetManagerImplDefault, AppWidgetManagerImplLollipop}
import com.fortysevendeg.ninecardslauncher.services.widgets.{ImplicitsWidgetsExceptions, WidgetServicesException, WidgetsServices}

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
