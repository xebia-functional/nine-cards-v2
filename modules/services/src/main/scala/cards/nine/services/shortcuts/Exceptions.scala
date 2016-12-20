package cards.nine.services.shortcuts

import cards.nine.commons.services.TaskService.NineCardException

case class ShortcutServicesException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsShortcutsExceptions {
  implicit def shortcutServicesException =
    (t: Throwable) => ShortcutServicesException(t.getMessage, Option(t))
}
