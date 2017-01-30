/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.widgets.impl

import android.os.Build
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.models.Conversions
import cards.nine.services.widgets.utils.AppWidgetManagerCompat
import cards.nine.services.widgets.utils.impl.{
  AppWidgetManagerImplDefault,
  AppWidgetManagerImplLollipop
}
import cards.nine.services.widgets.{
  ImplicitsWidgetsExceptions,
  WidgetServicesException,
  WidgetsServices
}

class WidgetsServicesImpl extends WidgetsServices with ImplicitsWidgetsExceptions {

  override def getWidgets(implicit context: ContextSupport) =
    TaskService {
      CatchAll[WidgetServicesException] {
        val appWidgetManager = getAppWidgetManager
        appWidgetManager.getAllProviders

      }
    }

  protected def getAppWidgetManager(
      implicit context: ContextSupport): AppWidgetManagerCompat with Conversions = {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) new AppWidgetManagerImplLollipop
    else new AppWidgetManagerImplDefault
  }
}
