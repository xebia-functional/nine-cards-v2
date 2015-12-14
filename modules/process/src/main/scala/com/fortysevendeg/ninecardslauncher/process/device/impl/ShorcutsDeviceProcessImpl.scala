package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.device.{ImplicitsDeviceException, DeviceConversions, ShortcutException}
import com.fortysevendeg.ninecardslauncher.services.image.SaveBitmap

trait ShorcutsDeviceProcessImpl {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException =>

  def getAvailableShortcuts(implicit context: ContextSupport) =
    (for {
      shortcuts <- shortcutsServices.getShortcuts
    } yield toShortcutSeq(shortcuts)).resolve[ShortcutException]

  def saveShortcutIcon(name: String, bitmap: Bitmap)(implicit context: ContextSupport) =
    (for {
      saveBitmapPath <- imageServices.saveBitmap(SaveBitmap(name, bitmap))
    } yield saveBitmapPath.path).resolve[ShortcutException]

}
