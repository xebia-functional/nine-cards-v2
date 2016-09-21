package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class WizardGeneratingCollectionsException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}



trait ImplicitsWizardExceptions {

  implicit def wizardGeneratingCollectionsException =
    (t: Throwable) => WizardGeneratingCollectionsException(t.getMessage, Option(t))
}