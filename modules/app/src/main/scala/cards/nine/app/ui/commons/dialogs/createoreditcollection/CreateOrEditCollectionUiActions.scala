package cards.nine.app.ui.commons.dialogs.createoreditcollection

import android.graphics.Paint.Style
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.app.DialogFragment
import android.widget.ImageView
import cards.nine.app.ui.commons.dialogs.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{AppUtils, RequestCodes}
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.theme.{DrawerIconColor, DrawerTextColor}
import cards.nine.models.types.{Communication, DialogToolbarTitle}
import cards.nine.models.{Collection, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import macroid.extras.EditTextTweaks._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._

trait CreateOrEditCollectionUiActions
  extends Styles {

  self: BaseActionFragment with CreateOrEditCollectionDOM with CreateOrEditCollectionListener =>

  val tagDialog = "create-or-edit-dialog"

  val tagLine = "line"

  lazy val lineColor = theme.getLineColor

  val defaultIcon = Communication.name

  var statuses = CreateOrEditCollectionStatuses()

  def initialize(theme: NineCardsTheme): TaskService[Unit] = {
    statuses = statuses.copy(theme = theme)
    val textColor = statuses.theme.get(DrawerTextColor)
    ((toolbar <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (rootView <~ colorLines()) ~
      (name <~ tvColor(textColor) <~ tvHintColor(textColor.alpha(0.4f))) ~
      (colorText <~ tvColor(textColor)) ~
      (iconText <~ tvColor(textColor)) ~
      (colorContent <~ On.click(Ui(changeColor(getColor)))) ~
      (iconContent <~ On.click(Ui(changeIcon(getIcon))))).toService()
  }

  def initializeNewCollection(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.newCollection)) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(Ui(saveCollection(getName, getIcon, getColor)))) ~
      setIcon(defaultIcon) ~
      setIndexColor(0)).toService()

  def initializeEditCollection(collection: Collection): TaskService[Unit] = {
    val color = theme.getIndexColor(collection.themedColorIndex)
    ((toolbar <~
      dtbInit(color) <~
      dtbChangeText(R.string.editCollection)) ~
      (collectionName <~ tvText(collection.name)) ~
      (fab <~
        fabButtonMenuStyle(color) <~
        On.click(Ui(editCollection(collection, getName, getIcon, getColor)))) ~
      setIcon(collection.icon) ~
      setIndexColor(collection.themedColorIndex)).toService()
  }

  def showColorDialog(color: Int): TaskService[Unit] = {
    val dialog = ColorDialogFragment(color)
    val requestCode = RequestCodes.selectInfoColor
    showDialog(dialog, requestCode).toService()
  }

  def showIconDialog(icon: String): TaskService[Unit] = {
    val dialog = IconDialogFragment(icon)
    val requestCode = RequestCodes.selectInfoIcon
    showDialog(dialog, requestCode).toService()
  }

  def showMessageContactUsError: TaskService[Unit] = showMessage(R.string.contactUsError).toService()

  def showMessageFormFieldError: TaskService[Unit] = showMessage(R.string.formFieldError).toService()

  def updateIcon(iconName: String): TaskService[Unit] = setIcon(iconName).toService()

  def updateColor(indexColor: Int): TaskService[Unit] = setIndexColor(indexColor).toService()

  def close(): TaskService[Unit] = (hideKeyboard ~ unreveal()).toService()

  private[this] def colorLines() = Transformer {
    case iv: ImageView if iv.getTag() == tagLine => iv <~ vBackgroundColor(lineColor)
  }

  private[this] def hideKeyboard: Ui[Any] = name <~ etHideKeyboard

  private[this] def showDialog(dialog: DialogFragment, requestCode: Int) = Ui {
    val ft = getFragmentManager.beginTransaction()
    Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    dialog.setTargetFragment(this, requestCode)
    dialog.show(ft, tagDialog)
  }

  private[this] def setIcon(iconName: String): Ui[Any] =
    iconImage <~
      vTag(iconName) <~
      ivSrc(resGetDrawable(iconName.getIconDetail).colorize(theme.get(DrawerIconColor)))

  private[this] def setIndexColor(index: Int): Ui[Any] = {
    val color = theme.getIndexColor(index)
    val size = resGetDimensionPixelSize(R.dimen.size_icon_select_new_collection)
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.setIntrinsicHeight(size)
    drawable.setIntrinsicWidth(size)
    drawable.getPaint.setColor(color)
    drawable.getPaint.setStyle(Style.FILL)
    drawable.getPaint.setAntiAlias(true)
    (toolbar <~
      dtbInit(color)) ~
      (fab <~
        fabButtonMenuStyle(color)) ~
      (colorImage <~
        vTag(index) <~
        ivSrc(drawable))
  }

  private[this] def showMessage(message: Int): Ui[Any] = content <~ vSnackbarShort(message)

  private[this] def getName: Option[String] = name.getText.toString match {
    case s if s.nonEmpty => Some(s)
    case _ => None
  }

  private[this] def getIcon: Option[String] =
    Option(iconImage.getTag) flatMap {
      case s: String => Some(s)
      case _ => None
    }

  private[this] def getColor: Option[Int] = Option(colorImage.getTag) map Int.unbox


}

case class CreateOrEditCollectionStatuses(
  theme: NineCardsTheme = AppUtils.getDefaultTheme)