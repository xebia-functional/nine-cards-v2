package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.device.{ImplicitsDeviceException, DeviceConversions, WidgetException}

trait WidgetsDeviceProcessImpl {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException =>

  def getWidgets(implicit context: ContextSupport) =
    (for {
      widgets <- widgetsServices.getWidgets
    } yield widgets map toWidget).resolve[WidgetException]

}
