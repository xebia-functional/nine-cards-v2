package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, View}
import android.widget.LinearLayout
import CommonsTweak._
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.FloatingActionButtonTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.FabButtonTags._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.tweaks.PathMorphDrawableTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FabItemMenu
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FabItemMenuTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait FabButtonBehaviour
  extends FabButtonStyle {

  self: TypedFindView with Contexts[AppCompatActivity] =>

  lazy val fabButton = Option(findView(TR.fab_button))

  lazy val fabMenuContent = Option(findView(TR.fab_menu_content))

  lazy val fabMenu = Option(findView(TR.fab_menu))

  // Show/Hide FabButton Manager

  var runnableHideFabButton: Option[RunnableWrapper] = None

  val handler = new Handler()

  val timeDelayFabButton = 3000

  def updateBarsInFabMenuShow(): Ui[_]

  def updateBarsInFabMenuHide(): Ui[_]

  def initFabButton: Ui[_] =
    (fabMenuContent <~ On.click(
      swapFabMenu()
    ) <~ vClickable(false)) ~
      (fabButton <~ fabButtonMenuStyle <~ On.click(swapFabMenu()))

  def loadMenuItems(items: Seq[FabItemMenu]): Ui[_] =
    fabMenu <~ Tweak[LinearLayout] { view =>
        val param = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.END)
        items foreach (view.addView(_, 0, param))
    }

  def swapFabMenu(doUpdateBars: Boolean = true): Ui[Any] = {
    val open = isMenuOpened
    val autoHide = isAutoHide
    val ui = (fabButton <~
      vAddField(opened, !open) <~
      pmdAnimIcon(if (open) IconTypes.ADD else IconTypes.CLOSE)) ~
      (fabMenuContent <~
        animFabButton(open) <~
        colorContentDialog(!open) <~
        vClickable(!open)) ~
      (if (open && autoHide) postDelayedHideFabButton else removeDelayedHideFabButton())
    ui ~ (if (doUpdateBars) updateBars(open) else Ui.nop)
  }

  def colorContentDialog(paint: Boolean) =
    vBackgroundColorResource(if (paint) R.color.background_dialog else android.R.color.transparent)

  def isMenuOpened: Boolean = fabButton flatMap (_.getField[Boolean](opened)) getOrElse false

  def showFabButton(color: Int = 0, autoHide: Boolean = true): Ui[_] =
    if (isFabButtonVisible && autoHide) {
      resetDelayedHide
    } else {
      val colorDefault = resGetColor(color)
      val colorDark = colorDefault.dark()
      (if (autoHide) postDelayedHideFabButton else removeDelayedHideFabButton()) ~
        (fabButton <~ (if (color != 0) fbaColor(colorDefault, colorDark) else Tweak.blank) <~ showFabMenu <~ vAddField(autoHideKey, autoHide)) ~
        (if (color != 0) fabMenu <~ changeItemsColor(color) else Ui.nop)
    }

  def hideFabButton: Ui[_] =
    removeDelayedHideFabButton() ~
      (fabButton <~ hideFabMenu)

  def changeItemsColor(color: Int) = Transformer {
    case item: FabItemMenu => item <~ fimBackgroundColor(resGetColor(color))
  }

  private[this] def updateBars(opened: Boolean): Ui[_] = if (opened) {
    updateBarsInFabMenuHide()
  } else {
    updateBarsInFabMenuShow()
  }

  private[this] def animFabButton(open: Boolean) = Transformer {
    case i: FabItemMenu if i.isType(fabButtonItem) =>
      if (open) {
        i <~ vGone
      } else {
        (i <~ animFabMenuItem) ~
          (i.icon <~ animFabMenuIconItem) ~
          (i.title <~ animFabMenuTitleItem)
      }
  }

  private[this] def isFabButtonVisible: Boolean = fabButton exists (_.getVisibility == View.VISIBLE)

  private[this] def isAutoHide: Boolean = fabButton flatMap (_.getField[Boolean](autoHideKey)) getOrElse false

  private[this] def resetDelayedHide =
    removeDelayedHideFabButton() ~ postDelayedHideFabButton

  private[this] def postDelayedHideFabButton = Ui {
    val runnable = new RunnableWrapper()
    handler.postDelayed(runnable, timeDelayFabButton)
    runnableHideFabButton = Option(runnable)
  }

  private[this] def removeDelayedHideFabButton() = Ui {
    runnableHideFabButton foreach handler.removeCallbacks
  }

  class RunnableWrapper extends Runnable {
    override def run(): Unit = (fabButton <~ hideFabMenu).run
  }

}

trait FabButtonStyle {

  def fabButtonMenuStyle(implicit context: ContextWrapper): Tweak[FloatingActionButton] = {
    val iconFabButton = new PathMorphDrawable(
      defaultIcon = IconTypes.ADD,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default))
    ivSrc(iconFabButton) +
      vAddField(opened, false) +
      vGone
  }

}

object FabButtonTags {
  val fabButtonItem = "fab_button"
  val opened = "opened"
  val autoHideKey = "autoHide"
}
