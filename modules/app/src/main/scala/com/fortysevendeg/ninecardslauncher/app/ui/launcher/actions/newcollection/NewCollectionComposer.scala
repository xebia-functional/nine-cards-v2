package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection

import android.graphics.Color
import android.graphics.Paint.Style
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.EditTextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ColorsUtils, RequestCodes}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{Communication, NineCardCategory}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait NewCollectionComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  val tagDialog = "dialog"

  lazy val name = Option(findView(TR.new_collection_name))

  lazy val colorContent = Option(findView(TR.new_collection_select_color_content))

  lazy val colorImage = Option(findView(TR.new_collection_select_color_image))

  lazy val iconContent = Option(findView(TR.new_collection_select_icon_content))

  lazy val iconImage = Option(findView(TR.new_collection_select_icon_image))

  def showMessage(message: Int): Ui[_] = content <~ vSnackbarShort(message)

  def initUi(implicit presenter: NewCollectionPresenter): Ui[_] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.newCollection) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(presenter.saveCollection(getName, getCategory, getColor))) ~
      setCategory(Communication) ~
      setIndexColor(0) ~
      (colorContent <~ On.click {
        Ui {
          getColor map { color =>
            val ft = getFragmentManager.beginTransaction()
            Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
            ft.addToBackStack(javaNull)
            val dialog = new ColorDialogFragment(color)
            dialog.setTargetFragment(this, RequestCodes.selectInfoColor)
            dialog.show(ft, tagDialog)
          }
        }
      }) ~
      (iconContent <~ On.click {
        Ui {
          getCategory map { category =>
            val ft = getFragmentManager.beginTransaction()
            Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
            ft.addToBackStack(javaNull)
            val dialog = new IconDialogFragment(category)
            dialog.setTargetFragment(this, RequestCodes.selectInfoIcon)
            dialog.show(ft, tagDialog)
          }
        }
      })

  def hideKeyboard: Ui[_] = name <~ etHideKeyboard

  def setCategory(category: NineCardCategory): Ui[_] =
    iconImage <~
      vTag(category) <~
      ivSrc(ColorsUtils.colorizeDrawable(resGetDrawable(iconCollectionDetail(category.name)), Color.GRAY))

  def setIndexColor(index: Int): Ui[_] = {
    val color = resGetColor(getIndexColor(index))
    val size = resGetDimensionPixelSize(R.dimen.size_icon_select_new_collection)
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.setIntrinsicHeight(size)
    drawable.setIntrinsicWidth(size)
    drawable.getPaint.setColor(color)
    drawable.getPaint.setStyle(Style.FILL)
    drawable.getPaint.setAntiAlias(true)
    colorImage <~
      vTag(index) <~
      ivSrc(drawable)
  }

  private[this] def getName: Option[String] = (for {
    n <- name
    text <- Option(n.getText)
  } yield if (text.toString.isEmpty) None else Some(text.toString)).flatten

  private[this] def getCategory: Option[NineCardCategory] = iconImage flatMap { icon =>
    icon.getTag match {
      case c: NineCardCategory => Some(c)
      case _ => None
    }
  }

  private[this] def getColor = colorImage map (c => Int.unbox(c.getTag))

  def showGeneralError: Ui[_] = rootContent <~ vSnackbarShort(R.string.contactUsError)

}
