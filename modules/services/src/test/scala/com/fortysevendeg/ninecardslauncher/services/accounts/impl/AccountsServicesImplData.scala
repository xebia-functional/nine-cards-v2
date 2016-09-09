package com.fortysevendeg.ninecardslauncher.services.accounts.impl

import com.fortysevendeg.ninecardslauncher.services.accounts.models.Account

trait AccountsServicesImplData {

  val account1 = Account("type1", "name1")

  val account2 = Account("type2", "name2")

  val androidAccount1 = new android.accounts.Account(account1.accountName, account1.accountType)

  val androidAccount2 = new android.accounts.Account(account2.accountName, account2.accountType)

  val scope = "fake-scope"

  val authToken = "fake-auth-token"

}
