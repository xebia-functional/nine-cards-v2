package cards.nine.services.permissions

sealed trait PermissionStatus

case object PermissionGranted extends PermissionStatus

case object PermissionDenied extends PermissionStatus