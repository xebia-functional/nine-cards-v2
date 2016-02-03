package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view._
import android.widget.{EditText, FrameLayout, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.LauncherExecutor
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.{Scrolling, Stopped, TranslationAnimator, ViewState}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, SearchBackgroundColor, SearchIconsColor, SearchPressedColor}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class SearchBoxesAnimatedView(context: Context, attrs: AttributeSet, defStyle: Int)
  (implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme)
  extends FrameLayout(context, attrs, defStyle)
  with SearchBoxAnimatedController { self =>

  def this(context: Context)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, attrs, 0)

  setFocusable(true)
  setFocusableInTouchMode(true)

  val computeUnitsTracker = 1000

  var statuses = SearchBoxesStatuses()

  val mainAnimator = new TranslationAnimator(
    update = (value: Float) => {
      statuses = statuses.copy(displacement = value)
      transformPanelCanvas()
    })

  var listener: Option[SearchBoxAnimatedListener] = None

  val durationAnimation = resGetInteger(android.R.integer.config_shortAnimTime)

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
    (MotionEventCompat.getActionMasked(event), statuses.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        true
      case (ACTION_MOVE, _) =>
        setStateIfNeeded(x, y)
        statuses.touchState != Stopped
      case (ACTION_DOWN, _) =>
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        statuses.touchState != Stopped
      case (ACTION_CANCEL | ACTION_UP, _) =>
        computeFling()
        statuses.touchState != Stopped
      case _ => statuses.touchState != Stopped
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    super.onTouchEvent(event)
    initVelocityTracker(event)
    val x = MotionEventCompat.getX(event, 0)
    val y = MotionEventCompat.getY(event, 0)
    (MotionEventCompat.getActionMasked(event), statuses.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        val delta = statuses.deltaX(x)
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        runUi(movementByOverScroll(delta))
      case (ACTION_MOVE, Stopped) =>
        setStateIfNeeded(x, y)
      case (ACTION_DOWN, _) =>
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
      case (ACTION_CANCEL | ACTION_UP, _) =>
        computeFling()
      case _ =>
    }
    true
  }

  override def movementByOverScroll(delta: Float): Ui[_] = if (overScroll(delta)) {
    applyTranslation(getActiveView, 0)
  } else {
    performScroll(delta)
  }

  private[this] def performScroll(delta: Float): Ui[_] = {
    mainAnimator.cancel()
    statuses = statuses.copy(displacement = statuses.calculateDisplacement(getWidth, delta))
    transformPanelCanvas()
  }

  private[this] def transformPanelCanvas(): Ui[_] =
    applyTranslation(getActiveView, statuses.displacement) ~
      applyTranslation(getInactiveView, initialPositionInactiveView + statuses.displacement)

  def forceAppsView = Ui (statuses = statuses.copy(currentItem = AppsView)) ~ (getActiveView <~ vVisible)

  def reset: Ui[_] =
    Ui(statuses = statuses.copy(displacement = 0)) ~
      applyTranslation(getActiveView, 0) ~
      applyTranslation(getInactiveView, 0) ~
      (getInactiveView <~ vGone)

  def updateAppsIcon(resourceId: Int): Ui[_] = appBox.updateHeader(resourceId)

  def updateContactsIcon(resourceId: Int): Ui[_] = contactBox.updateHeader(resourceId)

  def addTextChangedListener(onChangeText: (String, BoxView) => Unit): Unit = {
    appBox.addTextChangedListener(onChangeText)
    contactBox.addTextChangedListener(onChangeText)
  }

  def clean: Ui[_] = appBox.clean ~ contactBox.clean

  private[this] def applyTranslation(view: View, translate: Float): Ui[_] =
    view <~ vTranslationX(translate)

  def setStateIfNeeded(x: Float, y: Float) = {
    val xDiff = math.abs(x - statuses.lastMotionX)
    val yDiff = math.abs(y - statuses.lastMotionY)

    val xMoved = xDiff > touchSlop
    val yMoved = yDiff > touchSlop

    if (xMoved || yMoved) {
      val isScrolling = (xDiff > yDiff) && !mainAnimator.isRunning && statuses.enabled
      if (isScrolling) runUi(startMovement)
      statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
    }
  }

  override def initVelocityTracker(event: MotionEvent): Unit = {
    if (statuses.velocityTracker.isEmpty) statuses = statuses.copy(velocityTracker = Some(VelocityTracker.obtain()))
    statuses.velocityTracker foreach (_.addMovement(event))
  }

  override def startMovement: Ui[_] =
    Ui(statuses = statuses.copy(touchState = Scrolling)) ~
    (self <~ vLayerHardware(activate = true)) ~
      (getInactiveView <~ vVisible) ~
      applyTranslation(getInactiveView, initialPositionInactiveView)

  private[this] def finishMovement =
    (self <~ vLayerHardware(activate = false)) ~
      (getInactiveView <~ vGone)

  override def isRunning: Boolean = mainAnimator.isRunning

  override def computeFling() = {
    statuses.velocityTracker foreach { tracker =>
      tracker.computeCurrentVelocity(computeUnitsTracker, maximumVelocity)
      if (statuses.touchState == Scrolling && !overScroll(-tracker.getXVelocity)) {
        val velocity = tracker.getXVelocity
        runUi(if (math.abs(velocity) > minimumVelocity) snap(velocity) else snapDestination())
      }
      tracker.recycle()
      statuses = statuses.copy(velocityTracker = None)
    }
    statuses = statuses.copy(touchState = Stopped)
  }

  private[this] def overScroll(deltaX: Float): Boolean = (statuses.currentItem, getActiveView.getTranslationX, deltaX) match {
    case (AppsView, x, d) if positiveTranslation(x, d) => false
    case (ContactView, x, d) if !positiveTranslation(x, d) => false
    case _ => true
  }

  private[this] def positiveTranslation(translation: Float, delta: Float): Boolean =
    translation < 0 || (translation == 0 && delta > 0)

  private[this] def snap(velocity: Float): Ui[_] = {
    mainAnimator.cancel()
    val destiny = (velocity, statuses.displacement) match {
      case (v, d) if v > 0 && d > 0 => getWidth
      case (v, d) if v <= 0 && d < 0 => -getWidth
      case _ => 0
    }
    animateViews(destiny, calculateDurationByVelocity(velocity, durationAnimation))
  }

  private[this] def snapDestination(): Ui[_] = {
    val destiny = statuses.displacement match {
      case d if d > getWidth * .6f => getWidth
      case d if d < -getWidth * .6f => -getWidth
      case _ => 0
    }
    animateViews(destiny, durationAnimation)
  }

  private[this] def animateViews(dest: Int, duration: Int): Ui[_] = {
    statuses = statuses.copy(swap = dest != 0)
    (self <~
      vInvalidate <~~
      mainAnimator.move(statuses.displacement, dest, duration)) ~~
      resetAnimationEnd
  }

  private[this] def resetAnimationEnd(): Ui[_] ={
    if (statuses.swap) {
      statuses = statuses.swapViews()
      listener foreach(_.onChangeBoxView(statuses.currentItem))
    }
    (self <~ vLayerHardware(activate = false)) ~ reset ~ finishMovement
  }

  private[this] def getActiveView = statuses.currentItem match {
    case AppsView => appBox.content
    case ContactView => contactBox.content
  }

  private[this] def getInactiveView = statuses.currentItem match {
    case AppsView => contactBox.content
    case ContactView => appBox.content
  }

  private[this] def initialPositionInactiveView = statuses.currentItem match {
    case AppsView => getWidth
    case ContactView => -getWidth
  }

}

trait SearchBoxAnimatedController {
  def initVelocityTracker(event: MotionEvent): Unit
  def computeFling(): Unit
  def isRunning: Boolean
  def startMovement: Ui[_]
  def movementByOverScroll(delta: Float): Ui[_]
}

trait SearchBoxAnimatedListener {
  def onChangeBoxView(state: BoxView)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Unit
}

case class SearchBoxesStatuses(
  currentItem: BoxView = AppsView,
  lastMotionX: Float = 0,
  lastMotionY: Float = 0,
  velocityTracker: Option[VelocityTracker] = None,
  displacement: Float = 0,
  swap: Boolean = false,
  enabled: Boolean = false,
  touchState: ViewState = Stopped) {

  def swapViews(): SearchBoxesStatuses = copy(currentItem match {
    case AppsView => ContactView
    case ContactView => AppsView
  })

  def deltaX(x: Float): Float = lastMotionX - x

  def deltaY(y: Float): Float = lastMotionY - y

  def calculateDisplacement(width: Int, delta: Float): Float = math.max(-width, Math.min(width, displacement - delta))

  def calculatePercent(width: Int) = math.abs(displacement) / width
}

case class BoxViewHolder(
  boxView: BoxView,
  content: LinearLayout)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends TypedFindView
  with LauncherExecutor
  with Styles {

  lazy val editText = Option(findView(TR.launcher_search_box_text))

  lazy val icon = Option(findView(TR.launcher_search_box_icon))

  lazy val headerIcon = Option(findView(TR.launcher_header_icon))

  runUi(
    (content <~ searchBoxContentStyle) ~
      (editText <~ searchBoxNameStyle(boxView match {
        case AppsView => R.string.searchApps
        case ContactView => R.string.searchContacts
      })) ~
      (icon <~ iconTweak))

  def updateHeader(resourceId: Int): Ui[_] = headerIcon <~ searchBoxButtonStyle(resourceId)

  def clean: Ui[_] = editText <~ (if (isEmpty) Tweak.blank else tvText("")) <~ etHideKeyboard

  def addTextChangedListener(onChangeText: (String, BoxView) => Unit): Unit =
    runUi(editText <~
      etAddTextChangedListener(
        (text: String, start: Int, before: Int, count: Int) => onChangeText(text, boxView)))

  def isEmpty: Boolean = editText exists (_.getText.toString == "")

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

  def searchBoxCharStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(SearchIconsColor))

  def searchBoxNameStyle(resourceId: Int)(implicit context: ContextWrapper): Tweak[EditText] =
    tvHint(resourceId)

  def searchBoxButtonStyle(resourceId: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    ivSrc(resourceId) +
      tivDefaultColor(theme.get(SearchIconsColor)) +
      tivPressedColor(theme.get(SearchPressedColor))
}

sealed trait BoxView

case object AppsView extends BoxView

case object ContactView extends BoxView