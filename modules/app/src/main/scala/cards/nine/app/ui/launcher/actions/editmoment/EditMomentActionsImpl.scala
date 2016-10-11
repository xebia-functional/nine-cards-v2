package cards.nine.app.ui.launcher.actions.editmoment

import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.widget.TextView
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.dialogs.{AlertDialogFragment, WifiDialogFragment}
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.app.ui.components.layouts.tweaks.EditHourMomentLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.EditWifiMomentLayoutTweaks._
import cards.nine.app.ui.components.layouts.{EditHourMomentLayout, EditWifiMomentLayout}
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.{Collection, Moment, MomentTimeSlot}
import cards.nine.process.theme.models.{DrawerIconColor, DrawerTextColor}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait EditMomentActionsImpl
  extends EditMomentActions
  with Styles {

  self: TypedFindView with BaseActionFragment =>

  implicit val editPresenter: EditMomentPresenter

  val defaultIcon = R.drawable.icon_collection_default_detail

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
    val arrow = resGetDrawable(R.drawable.icon_edit_moment_arrow).colorize(iconColor)
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
      (momentCollection <~
        tvColor(textColor) <~
        tvSizeResource(R.dimen.text_large) <~
        tvCompoundDrawablesWithIntrinsicBounds(right = Option(arrow))) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(Ui(editPresenter.saveMoment()))) ~
      loadCategories(moment, collections) ~
      loadHours(moment) ~
      loadWifis(moment)
  }

  override def momentNoFound(): Ui[Any] = unreveal()

  override def success(): Ui[Any] = unreveal()

  override def showSavingMomentErrorMessage(): Ui[Any] = uiShortToast2(R.string.contactUsError)

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
    val dialog = WifiDialogFragment(wifis, editPresenter.addWifi)
    showDialog(dialog)
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

  override def showFieldErrorMessage(): Ui[Any] = uiShortToast2(R.string.contactUsError)

  def showItemDuplicatedMessage(): Ui[Any] = uiShortToast2(R.string.addDuplicateItemError)

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
    val icons = defaultIcon +: (collections map (_.getIconDetail))

    def setName(position: Int) = momentCollection <~ tvText(collectionNames.lift(position) getOrElse "NO")

    val spinnerPosition = moment.collectionId map collectionIds.indexOf getOrElse 0

    (momentCollection <~
      On.click {
        momentCollection <~
          vListThemedPopupWindowShow(
            icons = icons,
            values = collectionNames,
            onItemClickListener = (position) => {
              setName(position).run
              editPresenter.setCollectionId(collectionIds.lift(position))
            },
            height = Option(resGetDimensionPixelSize(R.dimen.height_list_popup_menu))
          )
      }) ~
      setName(spinnerPosition)
  }

  private[this] def showDialog(dialog: DialogFragment) = Ui {
    val ft = getFragmentManager.beginTransaction()
    Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
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
