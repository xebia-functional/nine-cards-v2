package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.new_collection

import android.graphics.Color
import android.graphics.Paint.Style
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ActivityResult, ColorsUtils}
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.Ui

trait NewCollectionComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  val tagDialog = "dialog"

  lazy val name = Option(findView(TR.new_collection_name))

  lazy val colorContent = Option(findView(TR.new_collection_select_color_content))

  lazy val colorImage = Option(findView(TR.new_collection_select_color_image))

  lazy val iconContent = Option(findView(TR.new_collection_select_icon_content))

  lazy val iconImage = Option(findView(TR.new_collection_select_icon_image))

  def initUi: Ui[_] =
    (toolbar <~
      tbTitle(R.string.newCollection) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click {
          Ui.nop
        }) ~
      setCategory(communication) ~
      setIndexColor(0) ~
      (colorContent <~ On.click {
        Ui {
          getColor map { color =>
            val ft = getFragmentManager.beginTransaction()
            Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
            ft.addToBackStack(null)
            val dialog = new ColorDialogFragment(color)
            dialog.setTargetFragment(this, ActivityResult.selectInfoColor)
            dialog.show(ft, tagDialog)
          }
        }
      }) ~
      (iconContent <~ On.click {
        Ui {
          getCategory map { category =>
            val ft = getFragmentManager.beginTransaction()
            Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
            ft.addToBackStack(null)
            val dialog = new IconDialogFragment(category)
            dialog.setTargetFragment(this, ActivityResult.selectInfoIcon)
            dialog.show(ft, tagDialog)
          }
        }
      })

  def setCategory(category: String): Ui[_] =
    iconImage <~
      vTag2(category) <~
      ivSrc(ColorsUtils.colorizeDrawable(resGetDrawable(iconCollectionDetail(category)), Color.GRAY))

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
      vTag2(index) <~
      ivSrc(drawable)
  }

  def getCategory = iconImage map (_.getTag.toString)

  def getColor = colorImage map (c => Int.unbox(c.getTag))

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

}
