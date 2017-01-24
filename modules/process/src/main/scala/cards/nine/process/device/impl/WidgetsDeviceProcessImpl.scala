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

package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.OrderByName
import cards.nine.models.{AppWidget, Application}
import cards.nine.models.AppsWithWidgets
import cards.nine.process.device.{DeviceProcess, ImplicitsDeviceException, WidgetException}

trait WidgetsDeviceProcessImpl extends DeviceProcess {

  self: DeviceProcessDependencies with ImplicitsDeviceException =>

  def getWidgets(implicit context: ContextSupport) = {

    def toAppsWithWidgets(apps: Seq[Application], widgets: Seq[AppWidget]): Seq[AppsWithWidgets] =
      apps map { app =>
        AppsWithWidgets(
          packageName = app.packageName,
          name = app.name,
          widgets = widgets filter (_.packageName == app.packageName)
        )
      }

    (for {
      widgets <- widgetsServices.getWidgets
      widgetsSorted = widgets sortBy (_.label)
      apps <- persistenceServices.fetchApps(OrderByName)
      packageNames    = widgetsSorted.map(_.packageName).distinct
      appsWithWidgets = apps filter (app => packageNames.contains(app.packageName))
    } yield toAppsWithWidgets(appsWithWidgets, widgetsSorted)).resolve[WidgetException]
  }

}
