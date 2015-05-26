package com.fortysevendeg.ninecardslauncher.modules.user

case class AccountNotFoundException() extends RuntimeException("Account not found")
case class UserNotFoundException() extends RuntimeException("User not found")
case class AndroidIdNotFoundException() extends RuntimeException("Android Id not found")
case class GoogleUserNotFoundException() extends RuntimeException("Google User not found")
case class GoogleTokenNotFoundException() extends RuntimeException("Google Token not found")
case class UserUnexpectedException() extends RuntimeException("Unexpected User Exception")
