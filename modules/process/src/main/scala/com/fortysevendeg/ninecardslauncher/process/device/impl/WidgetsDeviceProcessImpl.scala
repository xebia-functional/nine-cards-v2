package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.process.device.{DeviceConversions, DeviceProcess, ImplicitsDeviceException, WidgetException}
import cards.nine.services.persistence.OrderByName
import cards.nine.commons.services.TaskService._


trait WidgetsDeviceProcessImpl extends DeviceProcess {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException =>

  def getWidgets(implicit context: ContextSupport) =
    (for {
      widgets <- widgetsServices.getWidgets
      widgetsSorted = widgets sortBy(_.label)
      apps <- persistenceServices.fetchApps(OrderByName)
      packageNames = widgetsSorted.map(_.packageName).distinct
      appsWithWidgets = apps filter (app => packageNames.contains(app.packageName))
    } yield toAppsWithWidgets(appsWithWidgets, widgetsSorted)).resolve[WidgetException]

}
