package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account

trait WizardPresenterData {

  val accountName = "device@47deg.com"

  val accountType = "com.google"

  val account = new Account(accountName, accountType)

}
