package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.{Activity, Dialog}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.{LayoutInflater, View}
import android.widget.{ScrollView, LinearLayout}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher.process.commons.types.{CardType, EmailCardType, PhoneCardType, SmsCardType}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

import scala.annotation.tailrec

case class SelectInfoContactDialogFragment(contact: Contact)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val scrollView = new ScrollView(getActivity)
    val rootView = new LinearLayout(getActivity)
    rootView.setOrientation(LinearLayout.VERTICAL)

    val views = contact.info map { info =>
      generatePhoneViews(info.phones map (phone => (phone.number, phone.category)), Seq.empty) ++
        generateItemsViews(info.emails map (_.address), Seq.empty, EmailCardType, R.string.emails) ++
        generateItemsViews(info.phones map (_.number), Seq.empty, SmsCardType, R.string.sms)
    } getOrElse Seq.empty

    runUi((rootView <~ vgAddViews(views)) ~ (scrollView <~ vgAddView(rootView)))

    new AlertDialog.Builder(getActivity).setView(scrollView).create()
  }

  class CategoryView(res: Int)
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_category_dialog, this)

//    val text = Option(findView(TR.contact_dialog_category_text))
//
//    runUi(text <~ tvText(res))
  }

  class PhoneView(data: (String, String))
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    val (phone, category) = data

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_phone_dialog, this)

    lazy val phoneContent = Option(findView(TR.contact_dialog_phone_content))
    lazy val phoneNumber = Option(findView(TR.contact_dialog_phone_number))
    lazy val phoneCategory = Option(findView(TR.contact_dialog_phone_category))
    lazy val phoneSms = Option(findView(TR.contact_dialog_sms_icon))

    runUi(
      phoneNumber <~
        tvText(phone),
      phoneCategory <~
        tvText(category),
      phoneContent <~
        On.click {
          Ui {
            generateIntent(phone, PhoneCardType)
          }
        },
      phoneSms <~
        On.click {
          Ui {
            generateIntent(phone, SmsCardType)
          }
        }
    )
  }

  @tailrec
  private[this] def generatePhoneViews(
    items: Seq[(String, String)],
    acc: Seq[View]): Seq[View] = items match {
    case Nil => acc
    case h :: t =>
      val viewItem = new PhoneView(h)
      val newAcc = acc :+ viewItem
      generatePhoneViews(t, newAcc)
  }

  private[this] def generateIntent(data: String, cardType: CardType) ={
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

  class ItemView(data: String, cardType: CardType)
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_item_dialog, this)

    lazy val text = Option(findView(TR.contact_dialog_item_text))

    runUi(
      text <~
        tvText(data) <~
        On.click {
          Ui {
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
        }
    )

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
        Option(new CategoryView(resHead))
      } else None
      val viewItem = new ItemView(h, cardType)
      val newAcc = maybeViewCategory map { viewCategory =>
        acc ++ Seq(viewCategory, viewItem)
      } getOrElse acc :+ viewItem
      generateItemsViews(t, newAcc, cardType, resHead)
  }

}
