package com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs

import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.NineCardsMomentOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class MomentDialog()(implicit contextWrapper: ContextWrapper, presenter: LauncherPresenter)
  extends BottomSheetDialog(contextWrapper.getOriginal)
  with TypedFindView { dialog =>

  lazy val selectMomentList = findView(TR.select_moment_list)

  val sheetView = getLayoutInflater.inflate(TR.layout.select_moment_dialog)

  setContentView(sheetView)

  val views = NineCardsMoment.moments map (moment => new MomentItem(moment))

  (selectMomentList <~
    vgAddViews(views)).run

  class MomentItem(moment: NineCardsMoment)
    extends LinearLayout(contextWrapper.getOriginal)
    with TypedFindView {

    LayoutInflater.from(getContext).inflate(TR.layout.select_moment_item, this)

    val icon = findView(TR.select_moment_item_icon)

    val text = findView(TR.select_moment_item_text)

    val colorIcon = resGetColor(R.color.item_list_popup_moments_menu)

    ((this <~ On.click(
      Ui {
        presenter.changeMoment(moment)
        dialog.dismiss()
      })) ~
      (icon <~ ivSrc(moment.getIconCollectionDetail) <~ tivDefaultColor(colorIcon)) ~
      (text <~ tvText(moment.getName))).run

  }

}
