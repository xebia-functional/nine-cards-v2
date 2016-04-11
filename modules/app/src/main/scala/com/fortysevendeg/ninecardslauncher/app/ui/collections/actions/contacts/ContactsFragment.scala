package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsPagerPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher2.R

class ContactsFragment(implicit collectionsPagerPresenter: CollectionsPagerPresenter)
  extends BaseActionFragment
  with ContactsIuActionsImpl
  with NineCardIntentConversions { self =>

  override lazy val presenter = new ContactsPresenter(self)

  override val collectionsPresenter: CollectionsPagerPresenter = collectionsPagerPresenter

  override def getLayoutId: Int = R.layout.list_action_with_scroller_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    presenter.initialize()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)

    def readExtras = Option(data) flatMap (d => Option(d.getExtras))

    def readExtraProperty(extras: Bundle, extra: String): Option[AnyRef] =
      if (extras.containsKey(ContactsFragment.addCardRequest)) {
        Option(extras.get(ContactsFragment.addCardRequest))
      } else {
        None
      }

    (requestCode, resultCode) match {
      case (RequestCodes.selectInfoContact, Activity.RESULT_OK) =>
        val maybeRequest = readExtras flatMap { extras =>
          readExtraProperty(extras, ContactsFragment.addCardRequest) match {
            case Some(card: AddCardRequest) => Some(card)
            case _ => None
          }
        }
        presenter.addContact(maybeRequest)
      case _ =>
    }
  }

}

object ContactsFragment {
  val addCardRequest = "add-card-request"
}


