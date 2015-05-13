package com.fortysevendeg.ninecardslauncher.modules.user

case class UserNotFoundException() extends RuntimeException("User not found")
case class UserUnexpectedException() extends RuntimeException("User error unexcepted")
