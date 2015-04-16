package com.fortysevendeg.ninecardslauncher.ui.wizard

import android.accounts.{Account, AccountManager}
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.ActionBarActivity
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import macroid.{AppContext, Contexts}
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._
import macroid.FullDsl._

class WizardActivity
  extends ActionBarActivity
  with Contexts[FragmentActivity]
  with Layout
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = AppContext(getApplicationContext)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(layout)
    loadUsers()
  }

  def loadUsers(): Unit = {
    val accountManager: AccountManager = AccountManager.get(this)
    val accounts: Seq[Account] = accountManager.getAccountsByType(AccountType).toSeq
    runUi(addUsersToRadioGroup(accounts))
  }

}
