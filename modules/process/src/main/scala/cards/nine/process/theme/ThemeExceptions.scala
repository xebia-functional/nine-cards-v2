package cards.nine.process.theme

import cards.nine.commons.services.TaskService.NineCardException

case class ThemeException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}
trait ImplicitsThemeException {
  implicit def themeException = (t: Throwable) => ThemeException(t.getMessage, Option(t))
}
