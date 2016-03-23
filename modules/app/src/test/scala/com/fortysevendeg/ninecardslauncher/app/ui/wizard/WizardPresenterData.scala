package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserPermissions

trait WizardPresenterData {

  val accountName = "device@47deg.com"

  val accountType = "com.google"

  val account = new Account(accountName, accountType)

  val token = "fake-token"

  val permissions = Seq.empty

  val userPermission = UserPermissions(token, permissions)

}
