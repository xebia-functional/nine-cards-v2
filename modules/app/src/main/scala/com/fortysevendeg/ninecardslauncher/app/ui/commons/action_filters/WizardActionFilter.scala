package com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters

sealed trait WizardActionFilter {
  val action: String
}

case object WizardStateActionFilter
  extends WizardActionFilter {
  override val action: String = "wizard-state-action-filter"
}

case object WizardAskActionFilter
  extends WizardActionFilter {
  override val action: String = "wizard-ask-state-action-filter"
}

case object WizardAnswerActionFilter
  extends WizardActionFilter {
  override val action: String = "wizard-answer-state-action-filter"
}

object WizardActionFilter {

  val cases = Seq(WizardStateActionFilter, WizardAskActionFilter, WizardAnswerActionFilter)

  def apply(action: String): WizardActionFilter = cases find (_.action == action) getOrElse
    (throw new IllegalArgumentException(s"$action not found"))

}