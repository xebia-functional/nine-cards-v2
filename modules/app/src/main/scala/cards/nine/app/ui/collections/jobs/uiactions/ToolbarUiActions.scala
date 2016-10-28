package cards.nine.app.ui.collections.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.widget.Toolbar
import android.view.View
import cards.nine.app.ui.collections.jobs._
import cards.nine.app.ui.collections.snails.CollectionsSnails._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, UiContext}
import cards.nine.app.ui.components.commons.{TranslationAnimator, TranslationY}
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class ToolbarUiActions(val dom: GroupCollectionsDOM, listener: GroupCollectionsUiListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends ImplicitsUiExceptions {

  private[this] var statuses = ToolbarUiActionsStatuses()

  val resistanceDisplacement = .2f

  val resistanceScale = .05f

  lazy val iconIndicatorDrawable = PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val maxHeightToolbar = resGetDimensionPixelSize(R.dimen.height_toolbar_collection_details)

  lazy val toolbarAnimation = new TranslationAnimator(
    translation = TranslationY,
    update = (translationY) => {
      val move = math.min(0, math.max(translationY, -spaceMove))
      val dy = if (statuses.lastScrollYInMovement == 0) 0 else -(translationY - statuses.lastScrollYInMovement)
      statuses = statuses.copy(lastScrollYInMovement = translationY)
      moveToolbar(move.toInt) ~
        Ui(listener.updateScroll(dy.toInt))
    }
  )

  def translationScrollY(dy: Int): TaskService[Unit] = (if (toolbarAnimation.isRunning) {
    Ui.nop
  } else {
    val translationY = dom.tabs.getTranslationY.toInt
    val move = math.min(0, math.max(translationY - dy, -spaceMove))
    (dom.tabs <~ vTranslationY(move)) ~ moveToolbar(move)
  }).toService

  def scrollIdle(): TaskService[Unit] = {
    val scrollY = dom.tabs.getTranslationY.toInt
    val sType = if (scrollY < -spaceMove / 2) ScrollUp else ScrollDown
    val betweenUpAndDown = scrollY < 0 && scrollY > -spaceMove
    ((betweenUpAndDown match {
      case true =>
        statuses = statuses.reset()
        val to = if (sType == ScrollUp) -spaceMove else 0
        dom.tabs <~ toolbarAnimation.move(scrollY, to, attachTarget = true)
      case _ => Ui.nop
    }) ~ notifyScroll(sType)).toService
  }

  def forceScrollType(scrollType: ScrollType): TaskService[Unit] =
    ((scrollType match {
      case ScrollDown =>
        val scrollY = dom.tabs.getTranslationY.toInt
        dom.tabs <~ toolbarAnimation.move(scrollY, 0, attachTarget = true)
      case ScrollUp => Ui.nop
    }) ~ notifyScroll(scrollType)).toService

  def pullCloseScrollY(scroll: Int, scrollType: ScrollType, close: Boolean): TaskService[Unit] = {
    val displacement = scroll * resistanceDisplacement
    val distanceToValidClose = resGetDimension(R.dimen.distance_to_valid_action)
    val scale = 1f + ((scroll / distanceToValidClose) * resistanceScale)
    ((dom.tabs <~ (scrollType match {
      case ScrollDown => vTranslationY(displacement)
      case _ => Tweak.blank
    })) ~
      (dom.toolbar <~ (scrollType match {
        case ScrollDown => tbReduceLayout(-displacement.toInt)
        case _ => Tweak.blank
      })) ~
    (dom.iconContent <~ vScaleX(scale) <~ vScaleY(scale) <~ vTranslationY(displacement)) ~
      Ui {
        val newIcon = if (close) IconTypes.CLOSE else IconTypes.BACK
        if (iconIndicatorDrawable.currentTypeIcon != newIcon && !iconIndicatorDrawable.isRunning) {
          iconIndicatorDrawable.setToTypeIcon(newIcon)
          iconIndicatorDrawable.start()
        }
      }).toService
  }

  private[this] def moveToolbar(move: Int) = {
    val ratio: Float = move.toFloat / spaceMove.toFloat
    val scale = 1 + (ratio / 2)
    val isTop = ratio <= -1
    val alpha = 1 + ratio
    (dom.tabs <~ vAlpha(alpha)) ~
      (dom.toolbar <~ tbReduceLayout(-move)) ~
      (dom.iconContent <~ vScaleX(scale) <~ vScaleY(scale) <~ vAlpha(alpha)).ifUi(listener.isNormalMode) ~
      ((isTop, dom.titleContent.getVisibility, listener.isNormalMode) match {
        case (true, View.GONE, true) =>
          (dom.titleContent <~~ animationEnterTitle) ~~
            (dom.selector <~ (vVisible + vAlpha(0) ++ applyAnimation(alpha = Some(1)))) ~
            (dom.tabs <~ vGone)
        case (false, View.VISIBLE, true) =>
          (dom.titleContent <~ animationOutTitle) ~
            (dom.selector <~~ applyAnimation(alpha = Some(0))) ~~
            (dom.selector <~ vGone <~ vAlpha(1)) ~
            (dom.tabs <~ vVisible)
        case _ => Ui.nop
      })
  }

  private[this] def tbReduceLayout(reduce: Int) = Tweak[Toolbar] { view =>
    view.getLayoutParams.height = maxHeightToolbar - reduce
    view.requestLayout()
  }

  private[this] def notifyScroll(sType: ScrollType): Ui[Any] = (for {
    adapter <- dom.getAdapter
  } yield {
    adapter.setScrollType(sType)
    adapter.notifyChanged(dom.viewPager.getCurrentItem)
  }) getOrElse Ui.nop

}

case class ToolbarUiActionsStatuses(
  lastScrollYInMovement: Float = 0) {
  def reset() = copy(lastScrollYInMovement = 0)
}