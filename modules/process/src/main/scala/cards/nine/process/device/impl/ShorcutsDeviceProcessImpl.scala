package cards.nine.process.device.impl

import android.graphics.Bitmap
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.SaveBitmap
import cards.nine.process.device.{DeviceConversions, DeviceProcess, IconResize, ImplicitsDeviceException, ShortcutException}

trait ShorcutsDeviceProcessImpl extends DeviceProcess {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException =>

  def getAvailableShortcuts(implicit context: ContextSupport) =
    (for {
      shortcuts <- shortcutsServices.getShortcuts
    } yield toShortcutSeq(shortcuts)).resolve[ShortcutException]

  def saveShortcutIcon(bitmap: Bitmap, iconResize: Option[IconResize] = None)(implicit context: ContextSupport) =
    (for {
      saveBitmapPath <- imageServices.saveBitmap(SaveBitmap(bitmap, iconResize map toBitmapResize))
    } yield saveBitmapPath.path).resolve[ShortcutException]

}
