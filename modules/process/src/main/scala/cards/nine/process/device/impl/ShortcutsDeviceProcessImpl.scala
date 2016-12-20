package cards.nine.process.device.impl

import android.content.Intent.ShortcutIconResource
import android.graphics.Bitmap
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.IconResize
import cards.nine.process.device.{DeviceProcess, ImplicitsDeviceException, ShortcutException}

trait ShortcutsDeviceProcessImpl extends DeviceProcess {

  self: DeviceProcessDependencies with ImplicitsDeviceException =>

  def getAvailableShortcuts(implicit context: ContextSupport) =
    (for {
      shortcuts <- shortcutsServices.getShortcuts
    } yield shortcuts).resolve[ShortcutException]

  def saveShortcutIcon(bitmap: Bitmap, iconResize: Option[IconResize] = None)(
      implicit context: ContextSupport) =
    (for {
      bitmapPath <- imageServices.saveBitmap(
        bitmap,
        iconResize map (_.width),
        iconResize map (_.height))
    } yield bitmapPath.path).resolve[ShortcutException]

  def decodeShortcutIcon(resource: ShortcutIconResource)(
      implicit context: ContextSupport): TaskService[Bitmap] =
    imageServices.decodeShortcutIconResource(resource).resolve[ShortcutException]

}
