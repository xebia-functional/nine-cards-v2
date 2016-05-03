package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.{Activity, Dialog}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.{LayoutInflater, View}
import android.widget.{LinearLayout, ScrollView}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, PrimaryColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.annotation.tailrec

case class SelectInfoContactDialogFragment(contact: Contact)(implicit contextWrapper: ContextWrapper, activityContext: ActivityContextWrapper, theme: NineCardsTheme, uiContext: UiContext[_])
  extends DialogFragment
  with NineCardIntentConversions {

  val primaryColor = theme.get(PrimaryColor)

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val scrollView = new ScrollView(getActivity)
    val rootView = new LinearLayout(getActivity)
    rootView.setOrientation(LinearLayout.VERTICAL)

    val views = contact.info map { info =>
      generateHeaderView(contact.name, contact.photoUri) ++
        generateGeneralInfoView(contact.lookupKey, contact.photoUri) ++
        generatePhoneViews(info.phones map (phone => (phone.number, phone.category)), Seq.empty) ++
        generateEmailViews(info.emails map (email => (email.address, email.category)), Seq.empty)
    } getOrElse Seq.empty

    ((rootView <~ vgAddViews(views)) ~ (scrollView <~ vgAddView(rootView))).run

    new AlertDialog.Builder(getActivity).setView(scrollView).create()
  }

  class HeaderView(name: String, avatarUrl: String)
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_header, this)

    lazy val headerAvatar = Option(findView(TR.contact_info_header_avatar))
    lazy val headerName = Option(findView(TR.contact_info_header_name))

    ((headerAvatar <~
      ivUriContactInfo(avatarUrl, header = true) <~
      vBackgroundColor(primaryColor)) ~
      (headerName <~ tvText(name))).run
  }

  class GeneralInfoView(lookupKey: String, avatarUrl: String)
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_general_dialog, this)

    lazy val generalContent = Option(findView(TR.contact_dialog_general_content))
    lazy val icon = Option(findView(TR.contact_dialog_general_icon))
    lazy val generalInfo= Option(findView(TR.contact_dialog_general_info))

    ((icon <~
      ivUriContactInfo(avatarUrl, header = false) <~
      (Lollipop ifSupportedThen vCircleOutlineProvider() getOrElse Tweak.blank) <~
      vBackgroundColor(primaryColor)) ~
      (generalInfo <~
      tvText(getResources.getString(R.string.generalInfo))) ~
      (generalContent <~ On.click(generateIntent(lookupKey, ContactCardType)))).run
  }

  class PhoneView(data: (String, PhoneCategory))
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    val (phone, category) = data

    val categoryName = category match {
      case PhoneHome => getResources.getString(R.string.phoneHome)
      case PhoneWork => getResources.getString(R.string.phoneWork)
      case PhoneMobile => getResources.getString(R.string.phoneMobile)
      case PhoneMain => getResources.getString(R.string.phoneMain)
      case PhoneFaxWork => getResources.getString(R.string.phoneFaxWork)
      case PhoneFaxHome => getResources.getString(R.string.phoneFaxHome)
      case PhonePager => getResources.getString(R.string.phonePager)
      case PhoneOther => getResources.getString(R.string.phoneOther)
    }

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_phone_dialog, this)

    lazy val phoneContent = Option(findView(TR.contact_dialog_phone_content))
    lazy val phoneNumber = Option(findView(TR.contact_dialog_phone_number))
    lazy val phoneCategory = Option(findView(TR.contact_dialog_phone_category))
    lazy val phoneSms = Option(findView(TR.contact_dialog_sms_icon))

    ((phoneNumber <~
      tvText(phone)) ~
      (phoneCategory <~
      tvText(categoryName)) ~
      (phoneContent <~ On.click(generateIntent(phone, PhoneCardType))) ~
      (phoneSms <~ On.click(generateIntent(phone, SmsCardType)))).run
  }

  class EmailView(data: (String, EmailCategory))
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    val (email, category) = data

    val categoryName = category match {
      case EmailHome => getResources.getString(R.string.emailHome)
      case EmailWork => getResources.getString(R.string.emailWork)
      case EmailOther => getResources.getString(R.string.emailOther)
    }

    LayoutInflater.from(getActivity).inflate(R.layout.contact_info_email_dialog, this)

    lazy val emailContent = Option(findView(TR.contact_dialog_email_content))
    lazy val emailAddress = Option(findView(TR.contact_dialog_email_address))
    lazy val emailCategory = Option(findView(TR.contact_dialog_email_category))

    ((emailAddress <~
      tvText(email)) ~
      (emailCategory <~
      tvText(categoryName)) ~
      (emailContent <~ On.click(generateIntent(email, EmailCardType)))).run
  }

  private[this] def generateHeaderView(name: String, avatarUrl: String): Seq[View] = Seq(new HeaderView(name, avatarUrl))

  private[this] def generateGeneralInfoView(lookupKey: String, avatarUrl: String): Seq[View] = Seq(new GeneralInfoView(lookupKey, avatarUrl))

  @tailrec
  private[this] def generatePhoneViews(
    items: Seq[(String, PhoneCategory)],
    acc: Seq[View]): Seq[View] = items match {
    case Nil => acc
    case h :: t =>
      val viewItem = new PhoneView(h)
      val newAcc = acc :+ viewItem
      generatePhoneViews(t, newAcc)
  }

  @tailrec
  private[this] def generateEmailViews(
    items: Seq[(String, EmailCategory)],
    acc: Seq[View]): Seq[View] = items match {
    case Nil => acc
    case h :: t =>
      val viewItem = new EmailView(h)
      val newAcc = acc :+ viewItem
      generateEmailViews(t, newAcc)
  }

  private[this] def generateIntent(data: String, cardType: CardType): Ui[_] = Ui {
    val maybeIntent: Option[NineCardIntent] = cardType match {
      case EmailCardType => Some(emailToNineCardIntent(data))
      case SmsCardType => Some(smsToNineCardIntent(data))
      case PhoneCardType => Some(phoneToNineCardIntent(data))
      case ContactCardType => Some(contactToNineCardIntent(data))
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
