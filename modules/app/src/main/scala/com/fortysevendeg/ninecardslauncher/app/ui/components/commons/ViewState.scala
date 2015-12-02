package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

sealed trait ViewState

case object Stopped extends ViewState

case object Scrolling extends ViewState
