package cards.nine.app.ui.wizard

import cards.nine.commons.services.TaskService.NineCardException

case class WizardMarketTokenRequestCancelledException(
    message: String,
    cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class WizardGoogleTokenRequestCancelledException(
    message: String,
    cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class WizardNoCollectionsSelectedException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}
