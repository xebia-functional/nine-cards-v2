package com.fortysevendeg.ninecardslauncher.ui.wizard

import android.accounts.{Account, AccountManager}
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.ActionBarActivity
import android.widget.RadioButton
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.googleconnector.RequestTokenRequest
import macroid.{Transformer, Ui, AppContext, Contexts}
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._
import com.fortysevendeg.macroid.extras.ActionsExtras._
import macroid.FullDsl._

import scala.concurrent.ExecutionContext.Implicits.global

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
    runUi(
      addUsersToRadioGroup(accounts) ~
        (action <~ On.click {
          usersGroup <~ selectUser
        })
    )
  }

  private def selectUser = Transformer {
    case i: RadioButton if i.isChecked => Ui {
      googleConnectorServices.requestToken(activityActivityContext)(RequestTokenRequest(i.getText.toString)) map {
        response =>
          if (response.success) {
            aShortToast("esto va bien!")
          } else {
            aShortToast("esto va mal!")
          }
      } recover {
        case _ => aShortToast("esto va mal en recover!")
      }
    }
  }

}
