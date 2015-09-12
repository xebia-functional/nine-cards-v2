package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.{Activity, Dialog}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View}
import android.widget.{LinearLayout, TextView}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.CardType
import com.fortysevendeg.ninecardslauncher.process.device.models.ContactPhone
import com.fortysevendeg.ninecardslauncher.process.device.models.ContactEmail
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher2.R
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import macroid.ContextWrapper
import macroid.FullDsl._

import scala.annotation.tailrec

case class SelectInfoContactDialogFragment(contact: Contact)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val rootView = new LinearLayout(getActivity)
    rootView.setOrientation(LinearLayout.VERTICAL)

    val views = contact.info map { info =>
      generatePhonesViews(info.phones, Seq.empty) ++
        generateEmailsViews(info.emails, Seq.empty) ++
        generateSmsViews(info.phones, Seq.empty)
    } getOrElse Seq.empty

    runUi(rootView <~ vgAddViews(views))

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  private[this] def createViewCategory(res: Int) = {
    val view = LayoutInflater.from(getActivity).inflate(R.layout.contact_info_category_dialog, null)
    view.findViewById(R.id.contact_dialog_category_text) match {
      case t: TextView => t.setText(res)
    }
    view
  }

  private[this] def createViewItem(data: String, cardType: String) = {
    val view = LayoutInflater.from(getActivity).inflate(R.layout.contact_info_item_dialog, null)
    view.findViewById(R.id.contact_dialog_item_text) match {
      case t: TextView => t.setText(data)
    }
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val intent = cardType match {
          case CardType.email => emailToNineCardIntent(data)
          case CardType.sms => smsToNineCardIntent(data)
          case CardType.phone => phoneToNineCardIntent(data)
        }
        val card = AddCardRequest(
          term = contact.name,
          packageName = None,
          cardType = cardType,
          intent = intent,
          imagePath = contact.photoUri
        )
        val responseIntent = new Intent
        responseIntent.putExtra(ContactsFragment.addCardRequest, card)
        getTargetFragment.onActivityResult(getTargetRequestCode, Activity.RESULT_OK, responseIntent)
        dismiss()
      }
    })
    view
  }

  @tailrec
  private[this] def generateEmailsViews(emails: Seq[ContactEmail], acc: Seq[View]): Seq[View] = emails match {
    case Nil => acc
    case h :: t =>
      val maybeViewCategory: Option[View] = if (acc.isEmpty) {
        Option(createViewCategory(R.string.emails))
      } else None
      val viewItem = createViewItem(h.address, CardType.email)
      val newAcc = maybeViewCategory map { viewCategory =>
        acc ++ Seq(viewCategory, viewItem)
      } getOrElse acc :+ viewItem
      generateEmailsViews(t, newAcc)
  }

  @tailrec
  private[this] def generatePhonesViews(phones: Seq[ContactPhone], acc: Seq[View]): Seq[View] = phones match {
    case Nil => acc
    case h :: t =>
      val maybeViewCategory: Option[View] = if (acc.isEmpty) {
        Option(createViewCategory(R.string.phones))
      } else None
      val viewItem = createViewItem(h.number, CardType.phone)
      val newAcc = maybeViewCategory map { viewCategory =>
        acc ++ Seq(viewCategory, viewItem)
      } getOrElse acc :+ viewItem
      generatePhonesViews(t, newAcc)
  }

  @tailrec
  private[this] def generateSmsViews(phones: Seq[ContactPhone], acc: Seq[View]): Seq[View] = phones match {
    case Nil => acc
    case h :: t =>
      val maybeViewCategory: Option[View] = if (acc.isEmpty) {
        Option(createViewCategory(R.string.sms))
      } else None
      val viewItem = createViewItem(h.number, CardType.sms)
      val newAcc = maybeViewCategory map { viewCategory =>
        acc ++ Seq(viewCategory, viewItem)
      } getOrElse acc :+ viewItem
      generateSmsViews(t, newAcc)
  }

}
