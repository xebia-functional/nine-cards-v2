package com.fortysevendeg.ninecardslauncher.modules.googleconnector

case class GoogleUnexpectedException() extends RuntimeException("Google Connector error unexcepted")
case class GoogleOperationCanceledException() extends RuntimeException("Google Operation Canceled")
