package com.fortysevendeg.ninecardslauncher.ui.wizard

import android.accounts.{Account, AccountManager}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.ActionBarActivity
import android.widget.RadioButton
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.googleconnector.RequestTokenRequest
import com.fortysevendeg.ninecardslauncher.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{AppContext, Contexts, Transformer, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class WizardActivity
  extends ActionBarActivity
  with Contexts[FragmentActivity]
  with Layout
  with ComponentRegistryImpl {

  self =>

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
            runUi(Ui {
              val intent = new Intent(self, classOf[CreateCollectionService])
              self.startService(intent)
            })
          } else {
            val resString = if (response.canceled) R.string.canceledGooglePermission else R.string.errorConnectingGoogle
            runUi(uiShortToast(resString))
          }
      } recover {
        case _ => runUi(uiShortToast(R.string.errorConnectingGoogle))
      }
    }
  }

}
