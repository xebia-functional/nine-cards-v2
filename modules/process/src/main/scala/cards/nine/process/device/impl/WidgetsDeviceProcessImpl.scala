package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.OrderByName
import cards.nine.models.{AppWidget, Application}
import cards.nine.models.AppsWithWidgets
import cards.nine.process.device.{DeviceProcess, ImplicitsDeviceException, WidgetException}

trait WidgetsDeviceProcessImpl extends DeviceProcess {

  self: DeviceProcessDependencies
    with ImplicitsDeviceException =>

  def getWidgets(implicit context: ContextSupport) = {

    def toAppsWithWidgets(apps: Seq[Application], widgets: Seq[AppWidget]): Seq[AppsWithWidgets] = apps map { app =>
      AppsWithWidgets(
        packageName = app.packageName,
        name = app.name,
        widgets = widgets filter(_.packageName == app.packageName)
      )
    }

    (for {
      widgets <- widgetsServices.getWidgets
      widgetsSorted = widgets sortBy(_.label)
      apps <- persistenceServices.fetchApps(OrderByName)
      packageNames = widgetsSorted.map(_.packageName).distinct
      appsWithWidgets = apps filter (app => packageNames.contains(app.packageName))
    } yield toAppsWithWidgets(appsWithWidgets, widgetsSorted)).resolve[WidgetException]
  }

}
