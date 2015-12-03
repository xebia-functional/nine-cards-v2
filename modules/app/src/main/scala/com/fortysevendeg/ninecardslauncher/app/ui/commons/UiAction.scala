package com.fortysevendeg.ninecardslauncher.app.ui.commons

sealed trait UiAction

case object Add extends UiAction

case object Remove extends UiAction
