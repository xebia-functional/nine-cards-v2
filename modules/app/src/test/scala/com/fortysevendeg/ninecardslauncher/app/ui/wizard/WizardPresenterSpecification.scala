package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import macroid.{ActivityContextWrapper, Ui}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait WizardPresenterSpecification
  extends Specification
  with Mockito
  with WizardPresenterData {

  implicit val contextSupport = mock[ContextSupport]

  implicit val contextWrapper = mock[ActivityContextWrapper]

  trait WizardPresenterScope
    extends Scope {

    val mockActions = mock[WizardActions]
    mockActions.onResultLoadUser(account) returns Ui[Any]()

    val presenter = new WizardPresenter(mockActions) {
      override protected def getAccount(username: String): Option[Account] = Some(account)
    }

  }

}

class WizardPresenterSpec
  extends WizardPresenterSpecification {

  "loadUser" should {

    "return a successful account" in
      new WizardPresenterScope {

        presenter.loadUser(accountName)

        there was one(mockActions).onResultLoadUser(account)

      }

  }

}
