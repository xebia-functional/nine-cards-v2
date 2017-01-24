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

import android.annotation.SuppressLint
import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo}
import android.content.Context
import android.content.pm.PackageManager
import android.os.{UserHandle, UserManager}
import cards.nine.commons.contexts.ContextSupport
import cards.nine.models.{AppWidget, Conversions}
import cards.nine.services.widgets.utils.AppWidgetManagerCompat

import scala.collection.JavaConversions._

class AppWidgetManagerImplLollipop(implicit contextSupport: ContextSupport)
    extends AppWidgetManagerCompat
    with Conversions {

  lazy val packageManager: PackageManager = contextSupport.getPackageManager

  override def getAllProviders: Seq[AppWidget] = {
    for {
      userHandle            <- getUserHandle
      appWidgetProviderInfo <- getAppWidgetProviderInfo(userHandle)
    } yield {
      val label        = getLabel(appWidgetProviderInfo)
      val userHashCode = getUser(appWidgetProviderInfo)
      toWidget(appWidgetProviderInfo, label, userHashCode)
    }
  }

  @SuppressLint(Array("NewApi"))
  protected def getUserHandle =
    contextSupport.context
      .getSystemService(Context.USER_SERVICE)
      .asInstanceOf[UserManager]
      .getUserProfiles
      .toSeq

  @SuppressLint(Array("NewApi"))
  protected def getAppWidgetProviderInfo(userHandle: UserHandle) =
    AppWidgetManager
      .getInstance(contextSupport.context)
      .getInstalledProvidersForProfile(userHandle)
      .toSeq

  @SuppressLint(Array("NewApi"))
  protected def getLabel(implicit info: AppWidgetProviderInfo) = info.loadLabel(packageManager)

  @SuppressLint(Array("NewApi"))
  protected def getUser(implicit info: AppWidgetProviderInfo) =
    Option(android.os.Process.myUserHandle.hashCode)

}
