package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment

import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.widget.TextView
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorOps._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SpinnerTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ImageResourceNamed, RequestCodes}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.EditWifiMomentLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.adapters.ThemeArrayAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.AlertDialogFragment
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{EditHourMomentLayout, EditWifiMomentLayout}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.EditHourMomentLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment, MomentTimeSlot}
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DrawerIconColor, DrawerTextColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait EditMomentActionsImpl
  extends EditMomentActions
  with Styles{

  self: TypedFindView with BaseActionFragment =>

  implicit val editPresenter: EditMomentPresenter

  val defaultIcon = "default"

  val tagDialog = "dialog"

  lazy val momentCollection = findView(TR.edit_moment_collection)

  lazy val hourContent = findView(TR.edit_moment_hour_content)

  lazy val addHourAction = findView(TR.edit_moment_add_hour)

  lazy val wifiContent = findView(TR.edit_moment_wifi_content)

  lazy val iconLinkCollection = findView(TR.edit_moment_icon_link_collection)

  lazy val iconInfo = findView(TR.edit_moment_collection_info)

  lazy val iconHour = findView(TR.edit_moment_icon_hour)

  lazy val iconWifi = findView(TR.edit_moment_icon_wifi)

  lazy val addWifiAction = findView(TR.edit_moment_add_wifi)

  lazy val nameWifi = findView(TR.edit_moment_name_wifi)

  lazy val nameHour = findView(TR.edit_moment_name_hour)

  lazy val nameLinkCollection = findView(TR.edit_moment_name_link_collection)

  override def initialize(moment: Moment, collections: Seq[Collection]): Ui[Any] = {
    val iconColor = theme.get(DrawerIconColor)
    val textColor = theme.get(DrawerTextColor)
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.editMoment) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (iconLinkCollection <~ tivDefaultColor(iconColor)) ~
      (iconInfo <~ tivDefaultColor(iconColor) <~ On.click(showLinkCollectionMessage())) ~
      (iconWifi <~ tivDefaultColor(iconColor)) ~
      (iconHour <~ tivDefaultColor(iconColor)) ~
      (addWifiAction <~ tivDefaultColor(iconColor) <~ On.click(Ui(editPresenter.addWifi()))) ~
      (addHourAction <~ tivDefaultColor(iconColor) <~ On.click(Ui(editPresenter.addHour()))) ~
      (nameWifi <~ tvColor(textColor)) ~
      (nameHour <~ tvColor(textColor)) ~
      (nameLinkCollection <~ tvColor(textColor)) ~
      (momentCollection <~ sChangeDropdownColor(iconColor)) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(Ui(editPresenter.saveMoment()))) ~
      loadCategories(moment, collections) ~
      loadHours(moment) ~
      loadWifis(moment)
  }

  override def momentNoFound(): Ui[Any] = unreveal()

  override def success(): Ui[Any] = unreveal()

  override def showSavingMomentErrorMessage(): Ui[Any] = uiShortToast(R.string.contactUsError)

  override def reloadDays(position: Int, timeslot: MomentTimeSlot): Ui[Any] = hourContent <~ Transformer {
    case view: EditHourMomentLayout if view.getPosition.contains(position) => view <~ ehmPopulate(timeslot, position)
  }

  override def loadHours(moment: Moment): Ui[Any] = {
    val views = if (moment.timeslot.nonEmpty) {
      moment.timeslot.zipWithIndex map {
        case (slot, index) => (w[EditHourMomentLayout] <~ ehmPopulate(slot, index)).get
      }
    } else {
      Seq(createMessage(R.string.addHoursToEditMoment))
    }
    hourContent <~ vgRemoveAllViews <~ vgAddViews(views)
  }

  override def showWifiDialog(wifis: Seq[String]): Ui[Any] = {
    val dialog = WifiDialogFragment(wifis)
    showDialog(dialog, RequestCodes.selectInfoWifi)
  }

  override def loadWifis(moment: Moment): Ui[Any] = {
    val views = if (moment.wifi.nonEmpty) {
      moment.wifi.zipWithIndex map {
        case (wifi, index) => (w[EditWifiMomentLayout] <~ ewmPopulate(wifi, index)).get
      }
    } else {
      Seq(createMessage(R.string.addWifiToEditMoment))
    }
    wifiContent <~ vgRemoveAllViews <~ vgAddViews(views)
  }

  override def showFieldErrorMessage(): Ui[Any] = uiShortToast(R.string.contactUsError)

  def showItemDuplicatedMessage(): Ui[Any] = uiShortToast(R.string.addDuplicateItemError)

  private[this] def showLinkCollectionMessage() = Ui {
    val dialog = new AlertDialogFragment(
      message = R.string.linkCollectionMessage,
      showCancelButton = false
    )
    dialog.show(getFragmentManager, tagDialog)
  }

  private[this] def loadCategories(moment: Moment, collections: Seq[Collection]): Ui[Any] = {
    val collectionIds = 0 +: (collections map (_.id))
    val collectionNames = resGetString(R.string.noLinkCollectionToMoment) +: (collections map (_.name))
    val icons = (defaultIcon +: (collections map (_.icon))) map ImageResourceNamed.iconCollectionDetail
    val sa = new ThemeArrayAdapter(icons, collectionNames)

    val spinnerPosition = moment.collectionId map collectionIds.indexOf getOrElse -1

    momentCollection <~
      sAdapter(sa) <~
      sItemSelectedListener((position) => editPresenter.setCollectionId(collectionIds.lift(position))) <~
      (if (spinnerPosition > 0) sSelection(spinnerPosition) else Tweak.blank)
  }

  private[this] def showDialog(dialog: DialogFragment, requestCode: Int) = Ui {
    val ft = getFragmentManager.beginTransaction()
    Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    dialog.setTargetFragment(this, requestCode)
    dialog.show(ft, tagDialog)
  }

  private[this] def createMessage(res: Int) = {
    val textColor = theme.get(DrawerTextColor).alpha(.4f)
    val padding = resGetDimensionPixelSize(R.dimen.padding_large)
    (w[TextView] <~
      vMatchWidth <~
      vPaddings(padding) <~
      tvGravity(Gravity.CENTER_HORIZONTAL) <~
      tvText(res) <~
      tvSizeResource(R.dimen.text_default) <~
      tvColor(textColor)).get
  }

}
