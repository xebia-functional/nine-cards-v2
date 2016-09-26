package com.fortysevendeg.ninecardslauncher.process.accounts.impl

trait UserAccountsProcessImplData {

  val accountName1 = "name1"

  val accountName2 = "name2"

  val accountType = "com.google"

  val androidAccount1 = new android.accounts.Account(accountName1, accountType)

  val androidAccount2 = new android.accounts.Account(accountName2, accountType)

  val scope = "fake-scope"

  val authToken = "fake-auth-token"

  val permissionCode= 100

}
