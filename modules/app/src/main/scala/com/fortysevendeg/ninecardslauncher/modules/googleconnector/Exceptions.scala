package com.fortysevendeg.ninecardslauncher.modules.googleconnector

case class GoogleUnexpectedException() extends RuntimeException("Unexpected Google Connector Exception")
case class GoogleOperationCanceledException() extends RuntimeException("Google Operation Canceled")
