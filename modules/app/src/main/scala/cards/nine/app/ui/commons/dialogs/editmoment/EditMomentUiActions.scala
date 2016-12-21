package cards.nine.app.ui.commons.dialogs.editmoment

import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.widget.{ImageView, TextView}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.dialogs.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.app.ui.commons.ops.NineCardsMomentOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.dialogs.{
  AlertDialogFragment,
  BluetoothDialogFragment,
  WifiDialogFragment
}
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.app.ui.components.layouts.tweaks.EditHourMomentLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.EditDeviceMomentLayoutTweaks._
import cards.nine.app.ui.components.layouts.{EditDeviceMomentLayout, EditHourMomentLayout}
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.types.theme.{DrawerIconColor, DrawerTextColor}
import cards.nine.models.types.{CarMoment, MusicMoment, OutAndAboutMoment}
import cards.nine.models.{Collection, Moment, MomentTimeSlot}
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.UIActionsExtras._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

trait EditMomentUiActions extends Styles {

  self: BaseActionFragment with EditMomentDOM with EditMomentListener =>

  val defaultIcon = R.drawable.icon_collection_default_detail

  val tagWifiDialog = "wifi-dialog"

  val tagBluetoothDialog = "bluetooth-dialog"

  val tagDefaultDialog = "default-dialog"

  val tagLine = "line"

  lazy val lineColor = theme.getLineColor

  def initialize(moment: Moment, collections: Seq[Collection]): TaskService[Unit] = {
    val iconColor = theme.get(DrawerIconColor)
    val textColor = theme.get(DrawerTextColor)
    val arrow =
      resGetDrawable(R.drawable.icon_edit_moment_arrow).colorize(iconColor)

    def showMessageContent =
      (hourRoot <~ vGone) ~
        (messageIcon <~ tivDefaultColor(iconColor) <~ ivSrc(
          moment.momentType.getIconCollectionDetail)) ~
        (messageName <~ tvText(
          resGetString(R.string.message_moment_name, moment.momentType.getName)))

    def loadInfoByMoment = moment.momentType match {
      case CarMoment =>
        showMessageContent ~
          (messageText <~ tvText(R.string.specially_conditions_car))
      case MusicMoment =>
        showMessageContent ~
          (messageText <~ tvText(R.string.specially_conditions_music))
      case OutAndAboutMoment =>
        showMessageContent ~
          (Seq(wifiRoot, bluetoothRoot) <~ vGone) ~
          (messageText <~ tvText(R.string.specially_conditions_out_and_about))
      case _ =>
        messageRoot <~ vGone
    }

    val init = ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(resGetString(R.string.editMomentWithName, moment.momentType.getName)) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (rootView <~ colorLines()) ~
      (iconInfo <~ tivDefaultColor(iconColor) <~ On.click(showLinkCollectionMessage())) ~
      (Seq(iconLinkCollection, iconWifi, iconBluetooth, iconHour) <~ tivDefaultColor(iconColor)) ~
      (addWifiAction <~ tivDefaultColor(iconColor) <~ On.click(Ui(addWifi()))) ~
      (addBluetoothAction <~ tivDefaultColor(iconColor) <~ On.click(Ui(addBluetooth()))) ~
      (addHourAction <~ tivDefaultColor(iconColor) <~ On.click(Ui(addHour()))) ~
      (Seq(nameWifi, nameHour, messageName, messageText, nameLinkCollection) <~ tvColor(textColor)) ~
      (momentCollection <~
        tvColor(textColor) <~
        tvSizeResource(R.dimen.text_large) <~
        tvCompoundDrawablesWithIntrinsicBounds(right = Option(arrow))) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(Ui(saveMoment()))) ~
      loadCategories(moment, collections) ~
      loadInfoByMoment).toService()
    for {
      _ <- init
      _ <- loadHours(moment)
      _ <- loadWifis(moment)
      _ <- loadBluetooth(moment)
    } yield ()
  }

  def close(): TaskService[Unit] = unreveal().toService()

  def showSavingMomentErrorMessage(): TaskService[Unit] =
    uiShortToast(R.string.contactUsError).toService()

  def reloadDays(position: Int, timeslot: MomentTimeSlot): TaskService[Unit] =
    (hourContent <~ Transformer {
      case view: EditHourMomentLayout if view.getPosition.contains(position) =>
        view <~ ehmPopulate(timeslot, position, removeHour, changeFromHour, changeToHour, swapDay)
    }).toService()

  def loadHours(moment: Moment): TaskService[Unit] = {
    val views = if (moment.timeslot.nonEmpty) {
      moment.timeslot.zipWithIndex map {
        case (slot, index) =>
          (w[EditHourMomentLayout] <~ ehmPopulate(
            slot,
            index,
            removeHour,
            changeFromHour,
            changeToHour,
            swapDay)).get
      }
    } else {
      Seq(createMessage(R.string.addHoursToEditMoment))
    }
    (hourContent <~ vgRemoveAllViews <~ vgAddViews(views)).toService()
  }

  def showWifiDialog(wifis: Seq[String]): TaskService[Unit] = {
    val dialog = WifiDialogFragment(wifis, addWifi)
    showDialog(dialog, tagWifiDialog).toService()
  }

  def showBluetoothDialog(devices: Seq[String]): TaskService[Unit] = {
    val dialog = BluetoothDialogFragment(devices, addBluetooth)
    showDialog(dialog, tagBluetoothDialog).toService()
  }

  def loadWifis(moment: Moment): TaskService[Unit] = {
    val views = if (moment.wifi.nonEmpty) {
      moment.wifi.zipWithIndex map {
        case (wifi, index) =>
          (w[EditDeviceMomentLayout] <~ edmPopulate(wifi, index, removeWifi)).get
      }
    } else {
      Seq(createMessage(R.string.addWifiToEditMoment))
    }
    (wifiContent <~ vgRemoveAllViews <~ vgAddViews(views)).toService()
  }

  def loadBluetooth(moment: Moment): TaskService[Unit] = {
    val views = if (moment.bluetooth.nonEmpty) {
      moment.bluetooth.zipWithIndex map {
        case (bluetooth, index) =>
          (w[EditDeviceMomentLayout] <~ edmPopulate(bluetooth, index, removeBluetooth)).get
      }
    } else {
      Seq(createMessage(R.string.addBluetoothToEditMoment))
    }
    (bluetoothContent <~ vgRemoveAllViews <~ vgAddViews(views)).toService()
  }

  def showFieldErrorMessage(): TaskService[Unit] =
    uiShortToast(R.string.contactUsError).toService()

  def showItemDuplicatedMessage(): TaskService[Unit] =
    uiShortToast(R.string.addDuplicateItemError).toService()

  private[this] def colorLines() = Transformer {
    case iv: ImageView if iv.getTag() == tagLine =>
      iv <~ vBackgroundColor(lineColor)
  }

  private[this] def showLinkCollectionMessage() = Ui {
    val dialog = new AlertDialogFragment(
      message = R.string.linkCollectionMessage,
      showCancelButton = false
    )
    dialog.show(getFragmentManager, tagDefaultDialog)
  }

  private[this] def loadCategories(moment: Moment, collections: Seq[Collection]): Ui[Any] = {
    val collectionIds   = 0 +: (collections map (_.id))
    val collectionNames = resGetString(R.string.noLinkCollectionToMoment) +: (collections map (_.name))
    val icons           = defaultIcon +: (collections map (_.getIconDetail))

    def setName(position: Int) =
      momentCollection <~ tvText(collectionNames.lift(position) getOrElse "NO")

    val spinnerPosition = moment.collectionId map collectionIds.indexOf getOrElse 0

    (momentCollection <~
      On.click {
        momentCollection <~
          vListThemedPopupWindowShow(
            icons = icons,
            values = collectionNames,
            onItemClickListener = (position) => {
              setName(position).run
              setCollectionId(collectionIds.lift(position))
            },
            height = Option(resGetDimensionPixelSize(R.dimen.height_list_popup_menu))
          )
      }) ~
      setName(spinnerPosition)
  }

  private[this] def showDialog(dialog: DialogFragment, tag: String) = Ui {
    val ft = getFragmentManager.beginTransaction()
    Option(getFragmentManager.findFragmentByTag(tag)) foreach ft.remove
    ft.addToBackStack(javaNull)
    dialog.show(ft, tag)
  }

  private[this] def createMessage(res: Int) = {
    val textColor = theme.get(DrawerTextColor).alpha(.4f)
    val padding   = resGetDimensionPixelSize(R.dimen.padding_large)
    (w[TextView] <~
      vMatchWidth <~
      vPaddings(padding) <~
      tvGravity(Gravity.CENTER_HORIZONTAL) <~
      tvText(res) <~
      tvSizeResource(R.dimen.text_default) <~
      tvColor(textColor)).get
  }

}
