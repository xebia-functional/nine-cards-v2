package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.{Activity, Dialog}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.{LayoutInflater, View}
import android.widget.{LinearLayout, ScrollView}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types.{CardType, EmailCardType, PhoneCardType, SmsCardType}
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, PrimaryColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, ContextWrapper, Ui}

import scala.annotation.tailrec

case class SelectInfoContactDialogFragment(contact: Contact)(implicit contextWrapper: ContextWrapper, context: ActivityContextWrapper, uiContext: UiContext[_])
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val scrollView = new ScrollView(getActivity)
    val rootView = new LinearLayout(getActivity)
    rootView.setOrientation(LinearLayout.VERTICAL)

    val views = contact.info map { info =>
      generateHeaderView(contact.name, contact.photoUri) ++
        generateItemViews(info.phones map (phone => (phone.number, phone.category)), Seq.empty, PhoneCardType) ++
        generateItemViews(info.emails map (email => (email.address, email.category)), Seq.empty, EmailCardType)
    } getOrElse Seq.empty

    runUi((rootView <~ vgAddViews(views)) ~ (scrollView <~ vgAddView(rootView)))

    new AlertDialog.Builder(getActivity).setView(scrollView).create()
  }

  class HeaderView(name: String, avatarUrl: String)
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_header, this)

    lazy val headerAvatar = Option(findView(TR.contact_info_header_avatar))
    lazy val headerName = Option(findView(TR.contact_info_header_name))

    runUi(
      headerAvatar <~
        ivUriContactInfo(avatarUrl),
      headerName <~
        tvText(name)
    )
  }

  class PhoneView(data: (String, String))
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    val (phone, category) = data

    val phoneH = "PhoneHome"
    val phoneW = "PhoneWork"
    val phoneM = "PhoneMobile"
    val phoneO = "PhoneOther"

    val categoryName = category match {
      case `phoneH` => getResources.getString(R.string.phoneHome)
      case `phoneW` => getResources.getString(R.string.phoneWork)
      case `phoneM` => getResources.getString(R.string.phoneMobile)
      case `phoneO` => getResources.getString(R.string.phoneOther)
    }

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_phone_dialog, this)

    lazy val phoneContent = Option(findView(TR.contact_dialog_phone_content))
    lazy val phoneNumber = Option(findView(TR.contact_dialog_phone_number))
    lazy val phoneCategory = Option(findView(TR.contact_dialog_phone_category))
    lazy val phoneSms = Option(findView(TR.contact_dialog_sms_icon))

    runUi(
      phoneNumber <~
        tvText(phone),
      phoneCategory <~
        tvText(categoryName),
      phoneContent <~ On.click(generateIntent(phone, PhoneCardType)),
      phoneSms <~ On.click(generateIntent(phone, SmsCardType))
    )
  }

  class EmailView(data: (String, String))
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    val (email, category) = data

    val emailH = "EmailHome"
    val emailW = "EmailWork"
    val emailO = "EmailOther"

    val categoryName = category match {
      case `emailH` => getResources.getString(R.string.emailHome)
      case `emailW` => getResources.getString(R.string.emailWork)
      case `emailO` => getResources.getString(R.string.emailOther)
    }

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_email_dialog, this)

    lazy val emailContent = Option(findView(TR.contact_dialog_email_content))
    lazy val emailAddress = Option(findView(TR.contact_dialog_email_address))
    lazy val emailCategory = Option(findView(TR.contact_dialog_email_category))

    runUi(
      emailAddress <~
        tvText(email),
      emailCategory <~
        tvText(categoryName),
      emailContent <~ On.click(generateIntent(email, EmailCardType))
    )
  }

  private[this] def generateHeaderView(name: String, avatarUrl: String): Seq[View] = Seq(new HeaderView(name, avatarUrl))

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
