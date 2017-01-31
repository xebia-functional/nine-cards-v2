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

package cards.nine.services.widgets.utils.impl

import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo}
import android.content.pm.PackageManager
import android.os.Build
import cards.nine.commons.contexts.ContextSupport
import cards.nine.models.Conversions
import cards.nine.services.widgets.utils.AppWidgetManagerCompat

import scala.collection.JavaConversions._

class AppWidgetManagerImplDefault(implicit contextSupport: ContextSupport)
    extends AppWidgetManagerCompat
    with Conversions {

  lazy val packageManager: PackageManager = contextSupport.getPackageManager

  override def getAllProviders = getAppWidgetProviderInfo map { appWidgetProviderInfo =>
    val label        = getLabel(appWidgetProviderInfo)
    val userHashCode = getUser(appWidgetProviderInfo)
    toWidget(appWidgetProviderInfo, label, userHashCode)
  }

  protected def getAppWidgetProviderInfo =
    AppWidgetManager.getInstance(contextSupport.context).getInstalledProviders.toSeq

  protected def getLabel(info: AppWidgetProviderInfo) = info.label.trim

  protected def getUser(info: AppWidgetProviderInfo) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
      Option(android.os.Process.myUserHandle.hashCode)
    else None

}
