package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserPermissions

trait WizardPresenterData {

  val accountName = "device@47deg.com"

  val nonExistingAccountName = "non-existing@47deg.com"

  val accountType = "com.google"

  val account = new Account(accountName, accountType)

  val accounts = Seq(account)

  val token = "fake-token"

  val permissions = Seq.empty

  val userPermission = UserPermissions(token, permissions)

  val nameDevice = "Nexus 47"

  val deviceId = "XXX-47"

  val cloudId = "fake-cloud-id"

  val androidMarketScopes = "androidmarket"

  val googleScopes = "fakeGoogleScope"

  val deviceName = nameDevice

  val intentKey = "intent-key"

  val momentType = Option("HOME")

}
