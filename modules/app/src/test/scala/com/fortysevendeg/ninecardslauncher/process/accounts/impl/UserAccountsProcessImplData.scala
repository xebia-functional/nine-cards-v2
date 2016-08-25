package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import com.fortysevendeg.ninecardslauncher.services.accounts.models.{Account, GoogleAccount}

trait UserAccountsProcessImplData {

  val account1 = Account(GoogleAccount.value, "name1")

  val account2 = Account(GoogleAccount.value, "name2")

  val accounts = Seq(account1, account2)

  val scope = "fake-process-scope"

  val authToken = "fake-process-auth-token"

}
