package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.{LayoutInflater, View}
import android.widget.{LinearLayout, TextView}
import com.fortysevendeg.ninecardslauncher.process.device.models.ContactPhone
import com.fortysevendeg.ninecardslauncher.process.device.models.ContactEmail
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher2.R
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import macroid.ContextWrapper
import macroid.FullDsl._

import scala.annotation.tailrec

case class SelectInfoContactDialogFragment(contact: Contact)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment {

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

  private[this] def createViewItem(text: String) = {
    val view = LayoutInflater.from(getActivity).inflate(R.layout.contact_info_item_dialog, null)
    view.findViewById(R.id.contact_dialog_item_text) match {
      case t: TextView => t.setText(text)
    }
    view
  }

  @tailrec
  private[this] def generateEmailsViews(emails: Seq[ContactEmail], acc: Seq[View]): Seq[View] = emails match {
    case Nil => acc
    case h :: t =>
      val maybeViewCategory: Option[View] = if (acc.isEmpty) {
        Option(createViewCategory(R.string.emails))
      } else None
      val viewItem = createViewItem(h.address)
      val newAcc = maybeViewCategory map { viewCategory =>
        acc ++ Seq(viewCategory, viewItem)
      } getOrElse acc :+ viewItem
      generateEmailsViews(t, newAcc)
  }

  @tailrec
  private[this] def generatePhonesViews(emails: Seq[ContactPhone], acc: Seq[View]): Seq[View] = emails match {
    case Nil => acc
    case h :: t =>
      val maybeViewCategory: Option[View] = if (acc.isEmpty) {
        Option(createViewCategory(R.string.phones))
      } else None
      val viewItem = createViewItem(h.number)
      val newAcc = maybeViewCategory map { viewCategory =>
        acc ++ Seq(viewCategory, viewItem)
      } getOrElse acc :+ viewItem
      generatePhonesViews(t, newAcc)
  }

  @tailrec
  private[this] def generateSmsViews(emails: Seq[ContactPhone], acc: Seq[View]): Seq[View] = emails match {
    case Nil => acc
    case h :: t =>
      val maybeViewCategory: Option[View] = if (acc.isEmpty) {
        Option(createViewCategory(R.string.sms))
      } else None
      val viewItem = createViewItem(h.number)
      val newAcc = maybeViewCategory map { viewCategory =>
        acc ++ Seq(viewCategory, viewItem)
      } getOrElse acc :+ viewItem
      generateSmsViews(t, newAcc)
  }

}
