package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class ResetException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class AppException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class CreateBitmapException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class ShortcutException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class ContactException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class ContactPermissionException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class WidgetException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class CallException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class DeviceException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

case class DockAppException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsDeviceException {
  implicit def resetException = (t: Throwable) => ResetException(t.getMessage, Option(t))

  implicit def appException = (t: Throwable) => AppException(t.getMessage, Option(t))

  implicit def createBitmapException = (t: Throwable) => CreateBitmapException(t.getMessage, Option(t))

  implicit def shortcutException = (t: Throwable) => ShortcutException(t.getMessage, Option(t))

  implicit def contactException = (t: Throwable) => ContactException(t.getMessage, Option(t))

  implicit def widgetException = (t: Throwable) => WidgetException(t.getMessage, Option(t))

  implicit def callException = (t: Throwable) => CallException(t.getMessage, Option(t))

  implicit def dockAppException = (t: Throwable) => DockAppException(t.getMessage, Option(t))

  implicit def deviceException = (t: Throwable) => DeviceException(t.getMessage, Option(t))
}