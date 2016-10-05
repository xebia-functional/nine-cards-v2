package cards.nine.app.ui.launcher.actions.createoreditcollection

import android.graphics.Paint.Style
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.app.DialogFragment
import cards.nine.models.types.Communication
import com.fortysevendeg.macroid.extras.EditTextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.ui.commons.AppUtils._
import cards.nine.app.ui.commons.ops.ColorOps._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.app.ui.launcher.LauncherPresenter
import cards.nine.commons._
import cards.nine.process.commons.models.Collection
import cards.nine.process.theme.models.{DrawerIconColor, DrawerTextColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait CreateOrEditCollectionActionsImpl
  extends CreateOrEditCollectionActions
  with Styles {

  self: TypedFindView with BaseActionFragment =>

  val tagDialog = "dialog"

  val defaultIcon = Communication.name

  lazy val name = findView(TR.new_collection_name)

  lazy val collectionName = findView(TR.new_collection_name)

  lazy val colorContent = findView(TR.new_collection_select_color_content)

  lazy val colorImage = findView(TR.new_collection_select_color_image)

  lazy val colorText = findView(TR.new_collection_select_color_text)

  lazy val iconContent = findView(TR.new_collection_select_icon_content)

  lazy val iconImage = findView(TR.new_collection_select_icon_image)

  lazy val iconText = findView(TR.new_collection_select_icon_text)

  val launcherPresenter: LauncherPresenter

  val collectionPresenter: CreateOrEditCollectionPresenter

  override def initialize(): Ui[Any] = {
    val textColor = theme.get(DrawerTextColor)
    (toolbar <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (name <~ tvColor(textColor) <~ tvHintColor(textColor.alpha(0.4f))) ~
      (colorText <~ tvColor(textColor)) ~
      (iconText <~ tvColor(textColor)) ~
      (colorContent <~ On.click(Ui(collectionPresenter.changeColor(getColor)))) ~
      (iconContent <~ On.click(Ui(collectionPresenter.changeIcon(getIcon))))
  }

  override def initializeNewCollection(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.newCollection)) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(Ui(collectionPresenter.saveCollection(getName, getIcon, getColor)))) ~
      setIcon(defaultIcon) ~
      setIndexColor(0)

  override def initializeEditCollection(collection: Collection): Ui[Any] ={
    val color = resGetColor(getIndexColor(collection.themedColorIndex))
    (toolbar <~
      dtbInit(color) <~
      dtbChangeText(R.string.editCollection)) ~
      (collectionName <~ tvText(collection.name)) ~
      (fab <~
        fabButtonMenuStyle(color) <~
        On.click(Ui(collectionPresenter.editCollection(collection, getName, getIcon, getColor)))) ~
      setIcon(collection.icon) ~
      setIndexColor(collection.themedColorIndex)
  }

  override def addCollection(collection: Collection): Ui[Any] = Ui {
    launcherPresenter.addCollection(collection)
  }

  def editCollection(collection: Collection): Ui[Any] = Ui {
    launcherPresenter.updateCollection(collection)
  }

  override def showColorDialog(color: Int): Ui[Any] = {
    val dialog = ColorDialogFragment(color)
    val requestCode = RequestCodes.selectInfoColor
    showDialog(dialog, requestCode)
  }

  override def showIconDialog(icon: String) = {
    val dialog = IconDialogFragment(icon)
    val requestCode = RequestCodes.selectInfoIcon
    showDialog(dialog, requestCode)
  }

  override def showMessageContactUsError: Ui[Any] = showMessage(R.string.contactUsError)

  override def showMessageFormFieldError: Ui[Any] = showMessage(R.string.formFieldError)

  override def updateIcon(iconName: String): Ui[Any] = setIcon(iconName)

  override def updateColor(indexColor: Int): Ui[Any] = setIndexColor(indexColor)

  override def close(): Ui[Any] = hideKeyboard ~ unreveal()

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
    val color = resGetColor(getIndexColor(index))
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
