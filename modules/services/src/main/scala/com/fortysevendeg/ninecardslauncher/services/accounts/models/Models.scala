package com.fortysevendeg.ninecardslauncher.services.accounts.models

sealed trait AccountType {
  def value: String
}

case object GoogleAccount extends AccountType {
  override def value: String = "com.google"
}

case class Account(accountType: String, accountName: String)