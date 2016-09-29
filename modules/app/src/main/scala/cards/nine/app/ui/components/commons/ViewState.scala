package cards.nine.app.ui.components.commons

sealed trait ViewState

case object Stopped extends ViewState

case object Scrolling extends ViewState
