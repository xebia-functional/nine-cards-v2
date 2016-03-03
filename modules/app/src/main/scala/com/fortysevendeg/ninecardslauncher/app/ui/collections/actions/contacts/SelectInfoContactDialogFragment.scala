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
import com.fortysevendeg.ninecardslauncher.process.theme.models.{PrimaryColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

import scala.annotation.tailrec

case class SelectInfoContactDialogFragment(contact: Contact)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
  extends DialogFragment
  with NineCardIntentConversions {

  val primaryColor = theme.get(PrimaryColor)

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val scrollView = new ScrollView(getActivity)
    val rootView = new LinearLayout(getActivity)
    rootView.setOrientation(LinearLayout.VERTICAL)

    val views = contact.info map { info =>
      generateItemViews(info.phones map (phone => (phone.number, phone.category)), Seq.empty, PhoneCardType) ++
        generateItemViews(info.emails map (email => (email.address, email.category)), Seq.empty, EmailCardType)
    } getOrElse Seq.empty

    runUi((rootView <~ vgAddViews(views)) ~ (scrollView <~ vgAddView(rootView)))

    new AlertDialog.Builder(getActivity).setView(scrollView).create()
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
      phoneContent <~ On.click(generateIntent(phone, PhoneCardType)),
      phoneSms <~ On.click(generateIntent(phone, SmsCardType))
    )
  }

  class EmailView(data: (String, String))
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    val (email, category) = data

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_email_dialog, this)

    lazy val emailContent = Option(findView(TR.contact_dialog_email_content))
    lazy val emailAddress = Option(findView(TR.contact_dialog_email_address))
    lazy val emailCategory = Option(findView(TR.contact_dialog_email_category))

    runUi(
      emailAddress <~
        tvText(email),
      emailCategory <~
        tvText(category),
      emailContent <~ On.click(generateIntent(email, EmailCardType))
    )
  }

  @tailrec
  private[this] def generateItemViews(
    items: Seq[(String, String)],
    acc: Seq[View],
    cardType: CardType): Seq[View] = items match {
    case Nil => acc
    case h :: t =>
      val viewItem = cardType match {
        case EmailCardType => new EmailView(h)
        case PhoneCardType => new PhoneView(h)
      }
      val newAcc = acc :+ viewItem
      generateItemViews(t, newAcc, cardType)
  }

  private[this] def generateIntent(data: String, cardType: CardType): Ui[_] = Ui {
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
