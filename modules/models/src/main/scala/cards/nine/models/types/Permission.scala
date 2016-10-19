package cards.nine.models.types

sealed trait PermissionStatus

case object PermissionGranted extends PermissionStatus

case object PermissionDenied extends PermissionStatus