package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.{Activity, Dialog}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View}
import android.widget.{LinearLayout, TextView}
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher.process.types.{SmsCardType, EmailCardType, PhoneCardType, CardType}
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
      generateItemsViews(info.phones map (_.number), Seq.empty, PhoneCardType, R.string.phones) ++
        generateItemsViews(info.emails map (_.address), Seq.empty, EmailCardType, R.string.emails) ++
        generateItemsViews(info.phones map (_.number), Seq.empty, SmsCardType, R.string.sms)
    } getOrElse Seq.empty

    runUi(rootView <~ vgAddViews(views))

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  private[this] def createViewCategory(res: Int) = {
    val view = LayoutInflater.from(getActivity).inflate(R.layout.contact_info_category_dialog, javaNull)
    view.findViewById(R.id.contact_dialog_category_text) match {
      case t: TextView => t.setText(res)
      case _ =>
    }
    view
  }

  private[this] def createViewItem(data: String, cardType: CardType) = {
    val view = LayoutInflater.from(getActivity).inflate(R.layout.contact_info_item_dialog, javaNull)
    view.findViewById(R.id.contact_dialog_item_text) match {
      case t: TextView => t.setText(data)
      case _ =>
    }
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val maybeIntent: Option[NineCardIntent] = cardType match {
          case EmailCardType => Some(emailToNineCardIntent(data))
          case SmsCardType => Some(smsToNineCardIntent(data))
          case PhoneCardType => Some(phoneToNineCardIntent(data))
          case _ => None
        }
        maybeIntent foreach { intent =>
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
        }
        dismiss()
      }
    })
    view
  }

  @tailrec
  private[this] def generateItemsViews(
    items: Seq[String],
    acc: Seq[View],
    cardType: CardType,
    resHead: Int): Seq[View] = items match {
    case Nil => acc
    case h :: t =>
      val maybeViewCategory: Option[View] = if (acc.isEmpty) {
        Option(createViewCategory(resHead))
      } else None
      val viewItem = createViewItem(h, cardType)
      val newAcc = maybeViewCategory map { viewCategory =>
        acc ++ Seq(viewCategory, viewItem)
      } getOrElse acc :+ viewItem
      generateItemsViews(t, newAcc, cardType, resHead)
  }

}
