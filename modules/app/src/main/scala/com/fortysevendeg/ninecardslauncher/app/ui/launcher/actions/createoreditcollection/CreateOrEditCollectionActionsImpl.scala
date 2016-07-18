package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.createoreditcollection

import android.graphics.Color
import android.graphics.Paint.Style
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import com.fortysevendeg.macroid.extras.EditTextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.Communication
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait CreateOrEditCollectionActionsImpl
  extends CreateOrEditCollectionActions
  with Styles {

  self: TypedFindView with BaseActionFragment =>

  val tagDialog = "dialog"

  val defaultIcon = Communication.name

  lazy val name = Option(findView(TR.new_collection_name))

  lazy val collectionName = Option(findView(TR.new_collection_name))

  lazy val colorContent = Option(findView(TR.new_collection_select_color_content))

  lazy val colorImage = Option(findView(TR.new_collection_select_color_image))

  lazy val iconContent = Option(findView(TR.new_collection_select_icon_content))

  lazy val iconImage = Option(findView(TR.new_collection_select_icon_image))

  val launcherPresenter: LauncherPresenter

  val presenter: CreateOrEditCollectionPresenter

  override def initialize(): Ui[Any] =
    (toolbar <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (colorContent <~ On.click(Ui(presenter.changeColor(getColor)))) ~
      (iconContent <~ On.click(Ui(presenter.changeIcon(getIcon))))

  override def initializeNewCollection(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.newCollection)) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(Ui(presenter.saveCollection(getName, getIcon, getColor)))) ~
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
        On.click(Ui(presenter.editCollection(collection, getName, getIcon, getColor)))) ~
      setIcon(collection.icon) ~
      setIndexColor(collection.themedColorIndex)
  }

  override def addCollection(collection: Collection): Ui[Any] = Ui {
    launcherPresenter.addCollection(collection)
  }

  def editCollection(collection: Collection): Ui[Any] = Ui {
    launcherPresenter.updateCollection(collection)
  }

  override def showColorDialog(color: Int): Ui[Any] = Ui {
    val ft = getFragmentManager.beginTransaction()
    Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    val dialog = new ColorDialogFragment(color)
    dialog.setTargetFragment(this, RequestCodes.selectInfoColor)
    dialog.show(ft, tagDialog)
  }

  override def showIconDialog(icon: String) = Ui {
    val ft = getFragmentManager.beginTransaction()
    Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    val dialog = new IconDialogFragment(icon)
    dialog.setTargetFragment(this, RequestCodes.selectInfoIcon)
    dialog.show(ft, tagDialog)
  }

  override def showMessageContactUsError: Ui[Any] = showMessage(R.string.contactUsError)

  override def showMessageFormFieldError: Ui[Any] = showMessage(R.string.formFieldError)

  override def updateIcon(iconName: String): Ui[Any] = setIcon(iconName)

  override def updateColor(indexColor: Int): Ui[Any] = setIndexColor(indexColor)

  override def close(): Ui[Any] = hideKeyboard ~ unreveal()

  private[this] def hideKeyboard: Ui[Any] = name <~ etHideKeyboard

  private[this] def setIcon(iconName: String): Ui[Any] =
    iconImage <~
      vTag(iconName) <~
      ivSrc(resGetDrawable(iconCollectionDetail(iconName)).colorize(Color.GRAY))

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

  private[this] def getName: Option[String] = (for {
    n <- name
    text <- Option(n.getText)
  } yield if (text.toString.isEmpty) None else Some(text.toString)).flatten

  private[this] def getIcon: Option[String] = iconImage flatMap { icon =>
    icon.getTag match {
      case c: String => Some(c)
      case _ => None
    }
  }

  private[this] def getColor = colorImage map (c => Int.unbox(c.getTag))

}
