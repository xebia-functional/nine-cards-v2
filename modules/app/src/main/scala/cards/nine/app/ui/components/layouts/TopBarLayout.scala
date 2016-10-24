package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout
import cards.nine.app.ui.MomentPreferences
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.ConditionWeatherOps._
import cards.nine.app.ui.commons.ops.NineCardsMomentOps._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.components.drawables.{TopBarMomentBackgroundDrawable, TopBarMomentEdgeBackgroundDrawable}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, MomentWorkSpace, WorkSpaceType}
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.launcher.actions.editmoment.EditMomentFragment
import cards.nine.app.ui.launcher.jobs.{LauncherJobs, NavigationJobs}
import cards.nine.app.ui.preferences.commons._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.commons._
import cards.nine.models.types.{ConditionWeather, NineCardsMoment, UnknownCondition}
import cards.nine.process.theme.models._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class TopBarLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends FrameLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  val hasWeatherKey = "has-weather-key"

  lazy val persistMoment = new MomentPreferences

  lazy val collectionsSearchPanel = findView(TR.launcher_search_panel)

  lazy val collectionsBurgerIcon = findView(TR.launcher_burger_icon)

  lazy val collectionsGoogleIcon = findView(TR.launcher_google_icon)

  lazy val collectionsMicIcon = findView(TR.launcher_mic_icon)

  lazy val momentContent = findView(TR.launcher_moment_content)

  lazy val momentIconContent = findView(TR.launcher_moment_icon_content)

  lazy val momentIcon = findView(TR.launcher_moment_icon)

  lazy val momentText = findView(TR.launcher_moment_text)

  // Lower to API 17
  lazy val momentDigitalClock = Option(findView(TR.launcher_moment_text_digital_clock))

  // API 17 and more
  lazy val momentClock = Option(findView(TR.launcher_moment_text_clock))

  lazy val momentUnpin = findView(TR.launcher_moment_unpin)

  lazy val momentWeather = findView(TR.launcher_moment_weather)

  lazy val momentGoogleIcon = findView(TR.launcher_moment_google_icon)

  lazy val momentMicIcon = findView(TR.launcher_moment_mic_icon)

  val collectionWorkspace = LayoutInflater.from(context).inflate(R.layout.collection_bar_view_panel, javaNull)

  val momentWorkspace = LayoutInflater.from(context).inflate(R.layout.moment_bar_view_panel, javaNull)

  (this <~ vgAddViews(Seq(momentWorkspace, collectionWorkspace)) <~ vInvisible).run

  def init(workSpaceType: WorkSpaceType)(implicit navigationJobs: NavigationJobs, theme: NineCardsTheme): Ui[Any] = {
    (this <~ vVisible) ~
      populate ~
      (workSpaceType match {
        case CollectionsWorkSpace => (momentWorkspace <~ vInvisible) ~ (collectionWorkspace <~ vVisible)
        case MomentWorkSpace => (momentWorkspace <~ vVisible) ~ (collectionWorkspace <~ vInvisible)
      })
  }

  def populate(implicit navigationJobs: NavigationJobs, theme: NineCardsTheme): Ui[Any] = {
    val iconColor = theme.get(SearchIconsColor)
    val pressedColor = theme.get(SearchPressedColor)
    val iconBackground = new TopBarMomentBackgroundDrawable
    val edgeBackground = new TopBarMomentEdgeBackgroundDrawable
    val googleLogoPref = GoogleLogo.readValue
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
        On.click(Ui(navigationJobs.openMenu().resolveAsyncServiceOr(_ => navigationJobs.navigationUiActions.showContactUsError())))) ~
      (collectionsGoogleIcon <~
        googleLogoTweaks <~
        On.click(Ui(navigationJobs.launchSearch().resolveAsyncServiceOr(_ => navigationJobs.navigationUiActions.showContactUsError())))) ~
      (collectionsMicIcon <~
        micLogoTweaks <~
        On.click(Ui(navigationJobs.launchVoiceSearch().resolveAsyncServiceOr(_ => navigationJobs.navigationUiActions.showContactUsError()))))
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

  def reloadMoment(moment: NineCardsMoment)(implicit navigationJobs: NavigationJobs, launcherJobs: LauncherJobs, theme: NineCardsTheme): Ui[Any] = {
    val showClock = ShowClockMoment.readValue
    val showMicSearch = ShowMicSearchMoment.readValue
    val text = if (showClock) {
      s"${moment.getName} ${resGetString(R.string.atHour)}"
    } else moment.getName

    def unpinTweak = if (persistMoment.getPersistMoment.contains(moment)) {
      vVisible +
        On.click(Ui {
          (momentUnpin <~ vGone).run
          launcherJobs.cleanPersistedMoment().resolveAsync()
        })
    } else {
      vGone
    }

    def weatherTweak = if (ShowWeatherMoment.readValue) {
      vVisible +
        On.click(Ui(
          navigationJobs.launchGoogleWeather().resolveAsyncServiceOr(_ => navigationJobs.navigationUiActions.showContactUsError())))
    } else {
      vGone
    }

    (momentContent <~
      On.click(Ui(
        navigationJobs.goToChangeMoment().resolveAsync())) <~
      On.longClick(Ui {
        val momentMap = Map(EditMomentFragment.momentKey -> moment.name)
        val bundle = navigationJobs.navigationUiActions.dom.createBundle(Option(momentContent), resGetColor(R.color.collection_fab_button_item_1), momentMap)
        navigationJobs.launchEditMoment(bundle).resolveAsync()
      } ~ Ui(true))) ~
      (momentDigitalClock <~ (if (showClock) vVisible else vGone)) ~
      (momentClock <~ (if (showClock) vVisible else vGone)) ~
      (momentIcon <~
        ivSrc(moment.getIconCollectionDetail)) ~
      (momentText <~
        tvText(text)) ~
      (momentUnpin <~ unpinTweak) ~
      (momentWeather <~ weatherTweak) ~
      (momentGoogleIcon <~
        On.click(Ui(
          navigationJobs.launchSearch().resolveServiceOr(_ => navigationJobs.navigationUiActions.showContactUsError())))) ~
      (momentMicIcon <~
        (if (showMicSearch) vVisible else vGone) <~
        On.click(Ui(
          navigationJobs.launchVoiceSearch().resolveServiceOr(_ => navigationJobs.navigationUiActions.showContactUsError()))))
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
    (momentWeather.getField[Boolean](hasWeatherKey), condition) match {
      case (Some(true), UnknownCondition) => Ui.nop
      case _ => momentWeather <~ ivSrc(condition.getIcon) <~ vAddField(hasWeatherKey, true)
    }

}
