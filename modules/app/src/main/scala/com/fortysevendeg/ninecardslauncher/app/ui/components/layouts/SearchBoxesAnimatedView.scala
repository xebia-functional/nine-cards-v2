package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.support.v4.view.ViewConfigurationCompat
import android.util.AttributeSet
import android.view._
import android.widget.{EditText, FrameLayout, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.LauncherExecutor
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, SearchBackgroundColor, SearchIconsColor, SearchPressedColor}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class SearchBoxesAnimatedView(context: Context, attrs: AttributeSet, defStyle: Int)
  (implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme)
  extends FrameLayout(context, attrs, defStyle)
  with SearchBoxAnimatedController { self =>

  def this(context: Context)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, attrs, 0)

  var statuses = SearchBoxesStatuses()

  var listener: Option[SearchBoxAnimatedListener] = None

  val (touchSlop, maximumVelocity, minimumVelocity) = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    (ViewConfigurationCompat.getScaledPagingTouchSlop(configuration),
      configuration.getScaledMaximumFlingVelocity,
      configuration.getScaledMinimumFlingVelocity)
  }

  val appBox = BoxViewHolder(
    boxView = AppsView,
    content = LayoutInflater.from(getContext).inflate(TR.layout.search_box_panel, self, false),
    onHeaderIconClick = (boxView: BoxView) => listener foreach (_.onHeaderIconClick))

  val contactBox = BoxViewHolder(
    boxView = ContactView,
    content = LayoutInflater.from(getContext).inflate(TR.layout.search_box_panel, self, false),
    onHeaderIconClick = (boxView: BoxView) => listener foreach (_.onHeaderIconClick))

  runUi((self <~ vgAddViews(Seq(appBox.content, contactBox.content))) ~ reset)

  override def updateMovement(displacement: Float): Ui[_] =
    applyTranslation(getActiveView, displacement) ~
      applyTranslation(getInactiveView, initialPositionInactiveView + displacement)

  def forceAppsView = Ui (statuses = statuses.copy(currentItem = AppsView)) ~ (getActiveView <~ vVisible)

  def reset: Ui[_] =
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

  def isEmpty: Boolean = statuses.currentItem match {
    case AppsView => appBox.isEmpty
    case ContactView => contactBox.isEmpty
  }

  override def startMovement: Ui[_] =
    (self <~ vLayerHardware(activate = true)) ~
      (getInactiveView <~ vVisible) ~
      applyTranslation(getInactiveView, initialPositionInactiveView)

  override def overScroll(deltaX: Float): Boolean = (statuses.currentItem, getActiveView.getTranslationX, deltaX) match {
    case (AppsView, x, d) if positiveTranslation(x, d) => false
    case (ContactView, x, d) if !positiveTranslation(x, d) => false
    case _ => true
  }

  override def resetAnimationEnd(swap: Boolean): Ui[_] = {
    if (swap) {
      statuses = statuses.swapViews()
      listener foreach(_.onChangeBoxView(statuses.currentItem))
    }
    (self <~ vLayerHardware(activate = false)) ~ (getInactiveView <~ vGone) ~ reset
  }

  private[this] def positiveTranslation(translation: Float, delta: Float): Boolean =
    translation < 0 || (translation == 0 && delta > 0)

  private[this] def applyTranslation(view: View, translate: Float): Ui[_] =
    view <~ vTranslationX(translate)

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
  def startMovement: Ui[_]
  def updateMovement(displacement: Float): Ui[_]
  def overScroll(deltaX: Float): Boolean
  def resetAnimationEnd(swap: Boolean): Ui[_]
}

trait SearchBoxAnimatedListener {
  def onChangeBoxView(state: BoxView)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Unit
  def onHeaderIconClick(implicit context: ActivityContextWrapper): Unit
}

case class SearchBoxesStatuses(
  currentItem: BoxView = AppsView,
  enabled: Boolean = false) {

  def swapViews(): SearchBoxesStatuses = copy(currentItem match {
    case AppsView => ContactView
    case ContactView => AppsView
  })

}

case class BoxViewHolder(
  boxView: BoxView,
  content: LinearLayout,
  onHeaderIconClick: (BoxView) => Unit)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
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
      (icon <~ iconTweak) ~
      (headerIcon <~
        On.click {
          Ui(onHeaderIconClick(boxView))
        }))

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