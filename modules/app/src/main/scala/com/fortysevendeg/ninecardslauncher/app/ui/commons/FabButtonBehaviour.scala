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
import com.fortysevendeg.ninecardslauncher.app.ui.components.{PathMorphDrawable, FabItemMenu, IconTypes}
import com.fortysevendeg.ninecardslauncher.app.ui.components.PathMorphDrawableTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherTags
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherTags._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

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
      (fabButton <~ fabButtonMenuStyle <~ On.click(
        swapFabButton
      ))

  def loadMenuItems(items: Seq[FabItemMenu]): Ui[_] =
    fabMenu <~ Tweak[LinearLayout] {
      view =>
        val param = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.END)
        items foreach {
          menuItem =>
            view.addView(menuItem, 0, param)
        }
    }

  def swapFabButton(implicit context: ActivityContextWrapper) = {
    val isOpen = fabButton map (tagEquals(_, R.id.fab_menu_opened, open))
    isOpen map {
      opened =>
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
    case i: FabItemMenu if tagEquals(i, R.id.`type`, LauncherTags.fabButton) =>
      i <~ (if (open) hideFabMenuItem else showFabMenuItem)
  }

  def fabMenuOpened = fabButton exists (tagValue(_, R.id.fab_menu_opened).equals(open))

  def isFabMenuVisible = fabButton exists (_.getVisibility == View.VISIBLE)

  def startScroll(implicit context: ActivityContextWrapper) = runUi(
    if (!isFabMenuVisible) {
      postDelayedHideFabButton ~ (fabButton <~ showFabMenu)
    } else {
      resetDelayedHide
    }
  )

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
    override def run(): Unit = runUi(
      fabButton <~ hideFabMenu
    )
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
