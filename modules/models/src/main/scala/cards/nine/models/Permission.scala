package cards.nine.models

sealed trait PermissionStatus

case object PermissionGranted extends PermissionStatus

case object PermissionDenied extends PermissionStatus