package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator, ObjectAnimator}
import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view._
import android.view.animation.DecelerateInterpolator
import android.widget.{EditText, LinearLayout, FrameLayout}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.LauncherExecutor
import com.fortysevendeg.ninecardslauncher.app.ui.components.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{SearchPressedColor, SearchIconsColor, SearchBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView, R}
import macroid._
import macroid.FullDsl._
import com.fortysevendeg.ninecardslauncher2.TypedResource._

class SearchBoxesAnimatedView(context: Context, attrs: AttributeSet, defStyle: Int)
  (implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme)
  extends FrameLayout(context, attrs, defStyle)
  with SearchBoxAnimatedController { self =>

  def this(context: Context)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, null, 0)

  def this(context: Context, attrs: AttributeSet)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, attrs, 0)

  val resetAfterAnimationListener = new AnimatorListenerAdapter() {
    var swap = false

    override def onAnimationEnd(animation: Animator) = {
      if (swap) {
        indicator.swapViews()
        listener foreach(_.onChangeBoxView(indicator.currentItem))
      }
      runUi((self <~ vLayerHardware(activate = false)) ~ reset ~ finishMovement)
      super.onAnimationEnd(animation)
    }
  }

  val mainAnimator: ObjectAnimator = new ObjectAnimator
  mainAnimator.setInterpolator(new DecelerateInterpolator())
  mainAnimator.setPropertyName("translationX")
  mainAnimator.addListener(resetAfterAnimationListener)
  mainAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    override def onAnimationUpdate(value: ValueAnimator) = {
      indicator.displacement = value.getAnimatedValue.asInstanceOf[Float]
      runUi(transformPanelCanvas())
    }
  })

  var listener: Option[SearchBoxAnimatedListener] = None

  val durationAnimation = resGetInteger(android.R.integer.config_shortAnimTime)

  lazy val indicator = SearchBoxesIndicator()

  val (touchSlop, maximumVelocity, minimumVelocity) = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    (ViewConfigurationCompat.getScaledPagingTouchSlop(configuration),
      configuration.getScaledMaximumFlingVelocity,
      configuration.getScaledMinimumFlingVelocity)
  }

  val appBox = BoxViewHolder(
    AppsView, LayoutInflater.from(getContext).inflate(TR.layout.search_box_panel, self, false))

  val contactBox = BoxViewHolder(
    ContactView, LayoutInflater.from(getContext).inflate(TR.layout.search_box_panel, self, false))

  runUi((self <~ vgAddViews(Seq(appBox.content, contactBox.content))) ~ reset)

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    super.onInterceptTouchEvent(event)
    initVelocityTracker(event)
    val x = MotionEventCompat.getX(event, 0)
    val y = MotionEventCompat.getY(event, 0)
    (MotionEventCompat.getActionMasked(event), indicator.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        true
      case (ACTION_MOVE, _) =>
        setStateIfNeeded(x, y)
        indicator.touchState != Stopped
      case (ACTION_DOWN, _) =>
        indicator.lastMotionX = x
        indicator.lastMotionY = y
        indicator.touchState != Stopped
      case (ACTION_CANCEL | ACTION_UP, _) =>
        computeFling()
        indicator.touchState != Stopped
      case _ => indicator.touchState != Stopped
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    super.onTouchEvent(event)
    initVelocityTracker(event)
    val x = MotionEventCompat.getX(event, 0)
    val y = MotionEventCompat.getY(event, 0)
    (MotionEventCompat.getActionMasked(event), indicator.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        val delta = indicator.deltaX(x)
        indicator.lastMotionX = x
        indicator.lastMotionY = y
        runUi(movementByOverScroll(delta))
      case (ACTION_MOVE, Stopped) =>
        setStateIfNeeded(x, y)
      case (ACTION_DOWN, _) =>
        indicator.lastMotionX = x
        indicator.lastMotionY = y
      case (ACTION_CANCEL | ACTION_UP, _) =>
        computeFling()
      case _ =>
    }
    true
  }

  def movementByOverScroll(delta: Float): Ui[_] = if (overScroll(delta)) {
    applyTranslation(getActiveView, 0)
  } else {
    performScroll(delta)
  }

  private[this] def performScroll(delta: Float): Ui[_] = {
    mainAnimator.cancel()
    indicator.calculateDisplacement(getWidth, delta)
    transformPanelCanvas()
  }

  private[this] def transformPanelCanvas(): Ui[_] =
    applyTranslation(getActiveView, indicator.displacement) ~
      applyTranslation(getInactiveView, initialPositionInactiveView + indicator.displacement)

  def forceAppsView = Ui (indicator.currentItem = AppsView) ~ (getActiveView <~ vVisible)

  def reset: Ui[_] =
    Ui(indicator.displacement = 0) ~
      applyTranslation(getActiveView, 0) ~
      applyTranslation(getInactiveView, 0) ~
      (getInactiveView <~ vGone)

  private[this] def applyTranslation(view: View, translate: Float): Ui[_] =
    view <~ vTranslationX(translate)

  def setStateIfNeeded(x: Float, y: Float) = {
    val xDiff = math.abs(x - indicator.lastMotionX)
    val yDiff = math.abs(y - indicator.lastMotionY)

    val xMoved = xDiff > touchSlop
    val yMoved = yDiff > touchSlop

    if (xMoved || yMoved) {
      val isScrolling = xDiff > yDiff
      if (isScrolling) runUi(startMovement)
      indicator.lastMotionX = x
      indicator.lastMotionY = y
    }
  }

  def initVelocityTracker(event: MotionEvent): Unit = {
    if (indicator.velocityTracker.isEmpty) indicator.velocityTracker = Some(VelocityTracker.obtain())
    indicator.velocityTracker foreach (_.addMovement(event))
  }

  def startMovement: Ui[_] =
    Ui(indicator.touchState = Scrolling) ~
    (self <~ vLayerHardware(activate = true)) ~
      (getInactiveView <~ vVisible) ~
      applyTranslation(getInactiveView, initialPositionInactiveView)

  private[this] def finishMovement =
    (self <~ vLayerHardware(activate = false)) ~
      (getInactiveView <~ vGone)

  def computeFling() = {
    indicator.velocityTracker foreach { tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      if (indicator.touchState == Scrolling && !overScroll(-tracker.getXVelocity)) {
        val velocity = tracker.getXVelocity
        if (math.abs(velocity) > minimumVelocity) snap(velocity) else snapDestination()
      }
      tracker.recycle()
      indicator.velocityTracker = None
    }
    indicator.touchState = Stopped
  }

  private[this] def overScroll(deltaX: Float): Boolean = (indicator.currentItem, getActiveView.getTranslationX, deltaX) match {
    case (AppsView, x, d) if x < 0 || (x == 0 && d > 0) => false
    case (ContactView, x, d) if x > 0 || (x == 0 && d < 0) => false
    case _ => true
  }

  private[this] def snap(velocity: Float) = {
    mainAnimator.cancel()
    val destiny = (velocity, indicator.displacement) match {
      case (v, d) if v > 0 && d > 0 => getWidth
      case (v, d) if v <= 0 && d < 0 => -getWidth
      case _ => 0
    }
    animateViews(destiny, calculateDurationByVelocity(velocity, durationAnimation))
  }

  private[this] def snapDestination() = {
    val destiny = indicator.displacement match {
      case d if d > getWidth * .6f => getWidth
      case d if d < -getWidth * .6f => -getWidth
      case _ => 0
    }
    animateViews(destiny, durationAnimation)
    invalidate()
  }

  private[this] def animateViews(dest: Int, duration: Int) = {
    val swap = dest != 0
    resetAfterAnimationListener.swap = swap
    mainAnimator.setFloatValues(indicator.displacement, dest)
    mainAnimator.setDuration(duration)
    mainAnimator.start()
  }

  private[this] def getActiveView = indicator.currentItem match {
    case AppsView => appBox.content
    case ContactView => contactBox.content
  }

  private[this] def getInactiveView = indicator.currentItem match {
    case AppsView => contactBox.content
    case ContactView => appBox.content
  }

  private[this] def initialPositionInactiveView = indicator.currentItem match {
    case AppsView => getWidth
    case ContactView => -getWidth
  }

}

trait SearchBoxAnimatedController {
  def initVelocityTracker(event: MotionEvent): Unit
  def computeFling(): Unit
  def startMovement: Ui[_]
  def movementByOverScroll(delta: Float): Ui[_]
}

trait SearchBoxAnimatedListener {
  def onChangeBoxView(state: BoxView)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Unit
}

object SearchBoxesAnimatedViewTweak {

  def sbavReset(implicit contextWrapper: ContextWrapper) = Tweak[SearchBoxesAnimatedView] { view =>
    runUi(view.forceAppsView ~ view.reset)
  }

  def sbavChangeListener(listener: SearchBoxAnimatedListener)= Tweak[SearchBoxesAnimatedView] { view =>
    view.listener = Some(listener)
  }

}

case class SearchBoxesIndicator(
  var currentItem: BoxView = AppsView,
  var lastMotionX: Float = 0,
  var lastMotionY: Float = 0,
  var velocityTracker: Option[VelocityTracker] = None,
  var displacement: Float = 0,
  var touchState: ViewState = Stopped)(implicit contextWrapper: ContextWrapper) {

  def swapViews() = currentItem = currentItem match {
    case AppsView => ContactView
    case ContactView => AppsView
  }

  def deltaX(x: Float): Float = lastMotionX - x

  def deltaY(y: Float): Float = lastMotionY - y

  def calculateDisplacement(width: Int, delta: Float) = displacement = math.max(-width, Math.min(width, displacement - delta))

  def calculatePercent(width: Int) = math.abs(displacement) / width
}

case class BoxViewHolder(boxView: BoxView, content: LinearLayout)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends TypedFindView
  with LauncherExecutor
  with Styles {

  lazy val editText = Option(findView(TR.launcher_search_box_text))

  lazy val icon = Option(findView(TR.launcher_search_box_icon))

  runUi(
    (content <~ searchBoxContentStyle) ~
      (editText <~ searchBoxNameStyle(boxView match {
        case AppsView => R.string.searchApps
        case ContactView => R.string.searchContacts
      })) ~
      (icon <~ iconTweak))

  private[this] def iconTweak = boxView match {
    case AppsView =>
      searchBoxButtonStyle(R.drawable.app_drawer_icon_google_play) +
        On.click (Ui(launchPlayStore))
    case ContactView =>
      searchBoxButtonStyle(R.drawable.app_drawer_icon_phone) +
        On.click (Ui(launchDial()))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}

trait Styles {
  def searchBoxContentStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[LinearLayout] =
    vBackgroundBoxWorkspace(theme.get(SearchBackgroundColor))

  def searchBoxNameStyle(res: Int)(implicit context: ContextWrapper): Tweak[EditText] =
    tvHint(res)

  def searchBoxButtonStyle(res: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    ivSrc(res) +
      tivDefaultColor(theme.get(SearchIconsColor)) +
      tivPressedColor(theme.get(SearchPressedColor))
}

sealed trait ViewState

case object Stopped extends ViewState

case object Scrolling extends ViewState

sealed trait BoxView

case object AppsView extends BoxView

case object ContactView extends BoxView