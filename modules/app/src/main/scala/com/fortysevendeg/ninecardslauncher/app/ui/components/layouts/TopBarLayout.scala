package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ConditionWeatherOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.NineCardsMomentOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.{CollectionsWorkSpace, LauncherData, MomentWorkSpace, WorkSpaceType}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{TopBarMomentBackgroundDrawable, TopBarMomentEdgeBackgroundDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons._
import cards.nine.commons._
import cards.nine.process.commons.types.NineCardsMoment
import cards.nine.process.recognition._
import cards.nine.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class TopBarLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends FrameLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  val preferenceValues = new NineCardsPreferencesValue

  lazy val collectionsSearchPanel = Option(findView(TR.launcher_search_panel))

  lazy val collectionsBurgerIcon = Option(findView(TR.launcher_burger_icon))

  lazy val collectionsGoogleIcon = Option(findView(TR.launcher_google_icon))

  lazy val collectionsMicIcon = Option(findView(TR.launcher_mic_icon))

  lazy val momentContent = Option(findView(TR.launcher_moment_content))

  lazy val momentIconContent = Option(findView(TR.launcher_moment_icon_content))

  lazy val momentIcon = Option(findView(TR.launcher_moment_icon))

  lazy val momentText = Option(findView(TR.launcher_moment_text))

  // Lower to API 17
  lazy val momentDigitalClock = Option(findView(TR.launcher_moment_text_digital_clock))

  // API 17 and more
  lazy val momentClock = Option(findView(TR.launcher_moment_text_clock))

  lazy val momentWeather = Option(findView(TR.launcher_moment_weather))

  lazy val momentGoogleIcon = Option(findView(TR.launcher_moment_google_icon))

  lazy val momentMicIcon = Option(findView(TR.launcher_moment_mic_icon))

  val collectionWorkspace = LayoutInflater.from(context).inflate(R.layout.collection_bar_view_panel, javaNull)

  val momentWorkspace = LayoutInflater.from(context).inflate(R.layout.moment_bar_view_panel, javaNull)

  (this <~ vgAddViews(Seq(momentWorkspace, collectionWorkspace))).run

  def init(workSpaceType: WorkSpaceType)(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[Any] = {
    populate ~
      (workSpaceType match {
        case CollectionsWorkSpace => (momentWorkspace <~ vInvisible) ~ (collectionWorkspace <~ vVisible)
        case MomentWorkSpace => (momentWorkspace <~ vVisible) ~ (collectionWorkspace <~ vInvisible)
      })
  }

  def populate(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[Any] = {
    val iconColor = theme.get(SearchIconsColor)
    val pressedColor = theme.get(SearchPressedColor)
    val iconBackground = new TopBarMomentBackgroundDrawable
    val edgeBackground = new TopBarMomentEdgeBackgroundDrawable
    val googleLogoPref = GoogleLogo.readValue(preferenceValues)
    val googleLogoTweaks = googleLogoPref match {
      case GoogleLogoTheme =>
        ivSrc(R.drawable.search_bar_logo_google_light) +
          tivDefaultColor(theme.get(SearchGoogleColor)) +
          tivPressedColor(pressedColor)
      case GoogleLogoColoured =>
        ivSrc(R.drawable.search_bar_logo_google_color) + tivClean
    }
    val micLogoTweaks = googleLogoPref match {
      case GoogleLogoTheme =>
        ivSrc(R.drawable.search_bar_mic_light) +
          tivDefaultColor(theme.get(SearchGoogleColor)) +
          tivPressedColor(pressedColor)
      case GoogleLogoColoured =>
        ivSrc(R.drawable.search_bar_mic_color) + tivClean
    }
    val sizeRes = FontSize.getTitleSizeResource
    (momentWorkspace <~ vBackground(edgeBackground)) ~
      (momentIconContent <~ vBackground(iconBackground)) ~
      (momentIcon <~ tivDefaultColor(iconColor) <~ tivPressedColor(iconColor)) ~
      (momentText <~ tvSizeResource(sizeRes)) ~
      (momentDigitalClock <~ tvSizeResource(sizeRes)) ~
      (momentClock <~ tvSizeResource(sizeRes)) ~
      (collectionsSearchPanel <~
        vBackgroundBoxWorkspace(theme.get(SearchBackgroundColor))) ~
      (collectionsBurgerIcon <~
        tivDefaultColor(iconColor) <~
        tivPressedColor(pressedColor) <~
        On.click(Ui(presenter.launchMenu()))) ~
      (collectionsGoogleIcon <~
        googleLogoTweaks <~
        On.click(Ui(presenter.launchSearch()))) ~
      (collectionsMicIcon <~
        micLogoTweaks <~
        On.click(Ui(presenter.launchVoiceSearch())))
  }

  def movement(from: LauncherData, to: LauncherData, isFromLeft: Boolean, fraction: Float): Unit =
    if (from.workSpaceType != to.workSpaceType) {
      val displacement = getWidth * fraction
      val fromX = if (isFromLeft) displacement else -displacement
      val toX = fromX + (if (isFromLeft) -getWidth else getWidth)
      ((getView(from.workSpaceType) <~
        (if (fraction >= 1) vInvisible + vTranslationX(0) else vVisible + vTranslationX(fromX))) ~
        (getView(to.workSpaceType) <~
          vTranslationX(toX) <~
          vVisible)).run
    }

  def reloadMoment(moment: NineCardsMoment)(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[Any] = {
    val showClock = ShowClockMoment.readValue(preferenceValues)
    val text = if (showClock) {
      s"${moment.getName} ${resGetString(R.string.atHour)}"
    } else moment.getName
    (momentContent <~
      On.click(Ui(presenter.goToChangeMoment())) <~
      On.longClick(Ui(presenter.goToEditMoment()) ~ Ui(true))) ~
      (momentDigitalClock <~ (if (showClock) vVisible else vGone)) ~
      (momentClock <~ (if (showClock) vVisible else vGone)) ~
      (momentIcon <~
        ivSrc(moment.getIconCollectionDetail)) ~
      (momentText <~
        tvText(text)) ~
      (momentWeather <~
        On.click(Ui(presenter.launchGoogleWeather()))) ~
      (momentGoogleIcon <~
        On.click(Ui(presenter.launchSearch()))) ~
      (momentMicIcon <~
        On.click(Ui(presenter.launchVoiceSearch())))
  }

  def reloadByType(workSpaceType: WorkSpaceType): Ui[Any] = workSpaceType match {
    case MomentWorkSpace if momentWorkspace.getVisibility == View.INVISIBLE =>
      (collectionWorkspace <~ applyFadeOut()) ~ (momentWorkspace <~ applyFadeIn())
    case CollectionsWorkSpace if collectionWorkspace.getVisibility == View.INVISIBLE =>
      (collectionWorkspace <~ applyFadeIn()) ~ (momentWorkspace <~ applyFadeOut())
    case _ => Ui.nop
  }

  def getView(workSpaceType: WorkSpaceType): Option[View] = workSpaceType match {
    case MomentWorkSpace => Some(momentWorkspace)
    case CollectionsWorkSpace => Some(collectionWorkspace)
    case _ => None
  }

  def setWeather(condition: ConditionWeather): Ui[Any] =
    momentWeather <~ ivSrc(condition.getIcon)

}
