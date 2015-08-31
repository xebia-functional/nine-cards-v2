package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.view.{Gravity, View}
import android.view.ViewGroup.LayoutParams._
import android.widget.{LinearLayout, FrameLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.components.FabItemMenuTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{PathMorphDrawable, FabItemMenu, IconTypes}
import com.fortysevendeg.ninecardslauncher.app.ui.components.PathMorphDrawableTweaks._
import FabButtonTags._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._

trait FabButtonBehaviour
  extends FabButtonStyle {

  self: TypedFindView =>

  lazy val fabButton = Option(findView(TR.launcher_fab_button))

  lazy val fabMenuContent = Option(findView(TR.launcher_menu_content))

  lazy val fabMenu = Option(findView(TR.launcher_menu))

  // Show/Hide FabButton Manager

  var runnableHideFabButton: Option[RunnableWrapper] = None

  val handler = new Handler()

  val timeDelayFabButton = 3000

  def initFabButton(implicit context: ActivityContextWrapper): Ui[_] =
    (fabMenuContent <~ On.click(
      swapFabButton
    ) <~ fabContentStyle(false)) ~
      (fabButton <~ fabButtonMenuStyle <~ On.click(swapFabButton))

  def loadMenuItems(items: Seq[FabItemMenu]): Ui[_] =
    fabMenu <~ Tweak[LinearLayout] { view =>
        val param = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.END)
        items foreach (view.addView(_, 0, param))
    }

  def swapFabButton(implicit context: ActivityContextWrapper) = {
    val isOpen = fabButton map (tagEquals(_, R.id.fab_menu_opened, open))
    isOpen map { opened =>
      (fabButton <~
        vTag(R.id.fab_menu_opened, if (opened) close else open) <~
        pmdAnimIcon(if (opened) IconTypes.ADD else IconTypes.CLOSE)) ~
        (fabMenuContent <~
          animFabButton(opened) <~
          fadeBackground(!opened) <~
          fabContentStyle(!opened)) ~
        (if (opened) postDelayedHideFabButton else removeDelayedHideFabButton)
    } getOrElse Ui.nop
  }

  private[this] def animFabButton(open: Boolean)(implicit context: ActivityContextWrapper) = Transformer {
    case i: FabItemMenu if tagEquals(i, R.id.`type`, fabButtonItem) =>
      i <~ (if (open) hideFabMenuItem else showFabMenuItem)
  }

  def fabMenuOpened: Boolean = fabButton exists (tagValue(_, R.id.fab_menu_opened).equals(open))

  def isFabMenuVisible: Boolean = fabButton exists (_.getVisibility == View.VISIBLE)

  def showFabButton(color: Int = 0)(implicit context: ActivityContextWrapper): Ui[_] = if (!isFabMenuVisible) {
    postDelayedHideFabButton ~
      (fabButton <~ (if (color != 0) fbaColor(color) else Tweak.blank) <~ showFabMenu) ~
      (if (color != 0) fabMenu <~ changeItemsColor(color) else Ui.nop)
  } else {
    resetDelayedHide
  }

  def hideFabButton(implicit context: ActivityContextWrapper): Ui[_] =
    removeDelayedHideFabButton ~
      (fabButton <~ hideFabMenu)

  def changeItemsColor(color: Int)(implicit context: ActivityContextWrapper) = Transformer {
    case item: FabItemMenu => item <~ fimBackgroundColor(resGetColor(color))
  }

  private[this] def postDelayedHideFabButton(implicit context: ActivityContextWrapper) = Ui {
    val runnable = new RunnableWrapper()
    handler.postDelayed(runnable, timeDelayFabButton)
    runnableHideFabButton = Option(runnable)
  }

  private[this] def removeDelayedHideFabButton(implicit context: ActivityContextWrapper) = Ui {
    runnableHideFabButton foreach handler.removeCallbacks
  }

  private[this] def resetDelayedHide(implicit context: ActivityContextWrapper) =
    removeDelayedHideFabButton ~ postDelayedHideFabButton

  protected def tagEquals(view: View, id: Int, value: String) =
    Option(view.getTag(id)).isDefined && view.getTag(id).equals(value)

  protected def tagValue(view: View, id: Int) =
    Option(view.getTag(id)) map (_.toString) getOrElse ""

  class RunnableWrapper(implicit context: ActivityContextWrapper) extends Runnable {
    override def run(): Unit = runUi(fabButton <~ hideFabMenu)
  }

}

trait FabButtonStyle {

  def fabContentStyle(clickable: Boolean): Tweak[FrameLayout] = Tweak[View]( _.setClickable(clickable))

  def fabButtonMenuStyle(implicit context: ContextWrapper): Tweak[FloatingActionButton] = {
    val iconFabButton = new PathMorphDrawable(
      defaultIcon = IconTypes.ADD,
      defaultStroke = resGetDimensionPixelSize(R.dimen.default_stroke))
    ivSrc(iconFabButton) +
      vTag(R.id.fab_menu_opened, close) +
      vGone
  }

}

object FabButtonTags {
  val fabButtonItem = "fab_button"
  val open = "open"
  val close = "close"
}
