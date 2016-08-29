package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.device.{DeviceConversions, DeviceProcess, ImplicitsDeviceException, WidgetException}
import com.fortysevendeg.ninecardslauncher.services.persistence.OrderByName
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._


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
