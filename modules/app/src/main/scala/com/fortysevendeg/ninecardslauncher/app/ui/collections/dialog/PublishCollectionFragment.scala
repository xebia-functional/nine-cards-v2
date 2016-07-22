package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

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
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, PrimaryColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class PublishCollectionFragment(collection: Collection)(implicit contextWrapper: ContextWrapper, activityContext: ActivityContextWrapper, theme: NineCardsTheme)
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val scrollView = new ScrollView(getActivity)
    val rootView = new LinearLayout(getActivity)
    rootView.setOrientation(LinearLayout.VERTICAL)

    val view = new PublishCollectionWizardStartView

    ((rootView <~ vgAddView(view)) ~ (scrollView <~ vgAddView(rootView))).run

    new AlertDialog.Builder(getActivity).setView(scrollView).create()
  }

  class PublishCollectionWizardStartView
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.publish_collection_wizard_start, this)
//
//    lazy val headerAvatar = Option(findView(TR.public_collection_wizard_header))
//    lazy val headerName = Option(findView(TR.public_collection_wizard_message))
//    lazy val headerName = Option(findView(TR.public_collection_wizard_image))

  }

//  private[this] def generateIntent(data: String, cardType: CardType): Ui[_] = Ui {
//    val maybeIntent: Option[NineCardIntent] = cardType match {
//      case EmailCardType => Some(emailToNineCardIntent(data))
//      case SmsCardType => Some(smsToNineCardIntent(data))
//      case PhoneCardType => Some(phoneToNineCardIntent(data))
//      case ContactCardType => Some(contactToNineCardIntent(data))
//      case _ => None
//    }
//    maybeIntent foreach { intent =>
//      val card = AddCardRequest(
//        term = contact.name,
//        packageName = None,
//        cardType = cardType,
//        intent = intent,
//        imagePath = contact.photoUri
//      )
//      val responseIntent = new Intent
//      responseIntent.putExtra(ContactsFragment.addCardRequest, card)
//      getTargetFragment.onActivityResult(getTargetRequestCode, Activity.RESULT_OK, responseIntent)
//    }
//    dismiss()
//  }

}
