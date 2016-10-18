package cards.nine.app.ui.wizard.jobs

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.app.{DialogFragment, Fragment, FragmentManager}
import android.text.Html
import android.view.ViewGroup.LayoutParams._
import android.view.animation.DecelerateInterpolator
import android.view.{LayoutInflater, View}
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.{AppUtils, ImplicitsUiExceptions, SystemBarsTint, UiContext}
import cards.nine.app.ui.components.dialogs.WifiDialogFragment
import cards.nine.app.ui.components.widgets.tweaks.WizardCheckBoxTweaks._
import cards.nine.app.ui.components.widgets.tweaks.WizardMomentCheckBoxTweaks._
import cards.nine.app.ui.components.widgets.tweaks.WizardWifiCheckBoxTweaks._
import cards.nine.app.ui.components.widgets.{WizardCheckBox, WizardMomentCheckBox, WizardWifiCheckBox}
import cards.nine.commons.javaNull
import cards.nine.commons.services.TaskService._
import cards.nine.models.PackagesByCategory
import cards.nine.models.types._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class NewConfigurationUiActions(dom: WizardDOM with WizardUiListener)
  (implicit
    context: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends WizardStyles
  with ImplicitsUiExceptions {

  val numberOfScreens = 6

  val tagDialog = "dialog"

  val padding = resGetDimensionPixelSize(R.dimen.padding_large)

  lazy val defaultInterpolator = new DecelerateInterpolator(.7f)

  lazy val systemBarsTint = new SystemBarsTint

  val firstStep = 0
  val secondStep = 1
  val thirdStep = 2
  val fourthStep = 3
  val fifthStep = 4

  def loadFirstStep(): TaskService[Unit] = {
    val stepView = LayoutInflater.from(context.bestAvailable).inflate(R.layout.wizard_new_conf_step_0, javaNull)
    val resColor = R.color.wizard_new_conf_accent_1
    ((dom.newConfigurationStep <~
      vgAddView(stepView)) ~
      systemBarsTint.updateStatusColor(resGetColor(resColor)) ~
      systemBarsTint.defaultStatusBar() ~
      firstStepChoreographyIn ~
      createPagers() ~
      selectPager(firstStep, resColor) ~
      (dom.newConfigurationNext <~
        On.click(Ui(dom.onLoadBetterCollections())) <~
        tvColorResource(resColor))).toService
  }

  def loadSecondStep(collections: Seq[PackagesByCategory]): TaskService[Unit] = {
    val numberOfApps = collections.foldLeft(0)(_ + _.packages.size)
    val stepView = LayoutInflater.from(context.bestAvailable).inflate(R.layout.wizard_new_conf_step_1, javaNull)
    val resColor = R.color.wizard_new_conf_accent_1
    val description = resGetString(R.string.wizard_new_conf_desc_step_1, numberOfApps.toString, collections.length.toString)
    val counter = resGetString(R.string.wizard_new_conf_collection_counter_step_1, collections.length.toString, collections.length.toString)

    val collectionViews = collections map { collection =>
      (w[WizardCheckBox] <~
        vWrapContent <~
        wcbInitializeCollection(collection) <~
        FuncOn.click { view: View =>
          val itemCheckBox = view.asInstanceOf[WizardCheckBox]
          (itemCheckBox <~ wcbSwap()) ~
            (dom.newConfigurationStep1AllApps <~ wcbDoCheck(dom.areAllCollectionsChecked())) ~
            {
              val (checked, items) = dom.countCollectionsChecked()
              val counter = resGetString(R.string.wizard_new_conf_collection_counter_step_1, checked.toString, items.toString)
              dom.newConfigurationStep1CollectionCount <~ tvText(counter)
            }
        }).get
    }

    val params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT)

    ((dom.newConfigurationStep <~
      vgAddView(stepView)) ~
      selectPager(secondStep, resColor) ~
      systemBarsTint.updateStatusColor(resGetColor(resColor)) ~
      systemBarsTint.defaultStatusBar() ~
      (dom.newConfigurationStep1AllApps <~
        wcbInitialize(R.string.all_apps) <~
        FuncOn.click { view: View =>
          if (dom.areAllCollectionsChecked()) {
            Ui.nop
          } else {
            val counter = resGetString(R.string.wizard_new_conf_collection_counter_step_1, collections.length.toString, collections.length.toString)
            (view.asInstanceOf[WizardCheckBox] <~ wcbCheck()) ~
              (dom.newConfigurationStep1CollectionCount <~ tvText(counter)) ~
              checkAllCollections()
          }
        }) ~
      (dom.newConfigurationStep1Best9 <~
        wcbInitialize(R.string.wizard_new_conf_best9_step_1, defaultCheck = false) <~
        FuncOn.click { view: View =>
          val best9Item = view.asInstanceOf[WizardCheckBox]
          (best9Item <~ wcbSwap()) ~ best9Apps(best9Item.isCheck)
        }) ~
      (dom.newConfigurationStep1CollectionCount <~ tvText(counter)) ~
      (dom.newConfigurationStep1CollectionsContent <~ vgAddViews(collectionViews, params)) ~
      (dom.newConfigurationStep1Description <~ tvText(Html.fromHtml(description))) ~
      (dom.newConfigurationStep <~~ applyFadeIn()) ~~
      (dom.newConfigurationNext <~
        On.click(Ui(dom.onSaveCollections(dom.getCollectionsSelected, best9Apps = dom.newConfigurationStep1Best9.isCheck))) <~
        tvColorResource(resColor))).toService
  }

  def loadThirdStep(): TaskService[Unit] = {
    val stepView = LayoutInflater.from(context.bestAvailable).inflate(R.layout.wizard_new_conf_step_2, javaNull)
    val resColor = R.color.wizard_new_conf_accent_2
    ((dom.newConfigurationStep <~
      vgAddView(stepView)) ~
      (dom.newConfigurationStep2Description <~ tvText(Html.fromHtml(resGetString(R.string.wizard_new_conf_desc_step_2)))) ~
      systemBarsTint.updateStatusColor(resGetColor(resColor)) ~
      systemBarsTint.defaultStatusBar() ~
      thirdStepChoreographyIn ~
      selectPager(thirdStep, resColor) ~
      (dom.newConfigurationNext <~
        On.click(Ui(dom.onLoadMomentWithWifi())) <~
        tvColorResource(resColor))).toService
  }

  def loadFourthStep(wifis: Seq[String], moments: Seq[(NineCardsMoment, Boolean)]): TaskService[Unit] = {
    val stepView = LayoutInflater.from(context.bestAvailable).inflate(R.layout.wizard_new_conf_step_3, javaNull)
    val resColor = R.color.wizard_new_conf_accent_2

    val momentViews = moments map {
      case (moment, selected) =>
        (w[WizardWifiCheckBox] <~
          wwcbInitialize(moment, onWifiClick = () => {
            val dialog = WifiDialogFragment(wifis, (wifi) => {
              changeWifiName(moment, wifi).run
            })(context, AppUtils.getDefaultTheme)
            showDialog(dialog).run
          }, selected) <~
          FuncOn.click { view: View =>
            val itemCheckBox = view.asInstanceOf[WizardWifiCheckBox]
            itemCheckBox <~ wwcbSwap()
          }).get
    }
    val params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT)

    ((dom.newConfigurationStep <~
      vgAddView(stepView)) ~
      systemBarsTint.updateStatusColor(resGetColor(resColor)) ~
      systemBarsTint.defaultStatusBar() ~
      selectPager(fourthStep, resColor) ~
      (dom.newConfigurationStep3WifiContent <~ vgAddViews(momentViews, params)) ~
      (dom.newConfigurationNext <~
        On.click(Ui(dom.onSaveMomentsWithWifi(dom.getWifisSelected))) <~
        tvColorResource(resColor))).toService
  }

  def loadFifthStep(): TaskService[Unit] = {

    def momentTweak(moment: NineCardsMoment, defaultCheck: Boolean = true) =
      wmcbInitialize(moment, defaultCheck) +
        FuncOn.click { view: View =>
          view.asInstanceOf[WizardMomentCheckBox] <~ wmcbSwap()
        }

    val stepView = LayoutInflater.from(context.bestAvailable).inflate(R.layout.wizard_new_conf_step_4, javaNull)
    val resColor = R.color.wizard_new_conf_accent_3
    ((dom.newConfigurationStep <~
      vgAddView(stepView)) ~
      systemBarsTint.updateStatusColor(resGetColor(resColor)) ~
      systemBarsTint.defaultStatusBar() ~
      (dom.newConfigurationStep4Music <~
        momentTweak(MusicMoment)) ~
      (dom.newConfigurationStep4Car <~
        momentTweak(CarMoment, defaultCheck = false)) ~
      (dom.newConfigurationStep4Running <~
        momentTweak(RunningMoment, defaultCheck = false)) ~
      (dom.newConfigurationStep4Bike <~
        momentTweak(BikeMoment, defaultCheck = false)) ~
      selectPager(fifthStep, resColor) ~
      (dom.newConfigurationNext <~
        On.click(Ui(dom.onSaveMoments(dom.getMomentsSelected))) <~
        tvColorResource(resColor))).toService
  }

  def loadSixthStep(): TaskService[Unit] = {
    val stepView = LayoutInflater.from(context.bestAvailable).inflate(R.layout.wizard_new_conf_step_5, javaNull)
    val resColor = R.color.wizard_new_conf_accent_4

    ((dom.newConfigurationStep <~
        vgAddView(stepView)) ~
      systemBarsTint.updateStatusColor(resGetColor(resColor)) ~
      systemBarsTint.defaultStatusBar() ~
      (dom.newConfigurationStep5GoTo9Cards <~ On.click(Ui(dom.onClickFinishWizardButton()))) ~
      sixthStepChoreographyIn ~
      (dom.newConfigurationPagers <~ vGone) ~
      (dom.newConfigurationNext <~ vGone)).toService
  }

  private[this] def changeWifiName(moment: NineCardsMoment, wifi: String) = dom.newConfigurationStep3WifiContent <~ Transformer {
    case view: WizardWifiCheckBox if view.getMoment.contains(moment) => view <~ wwcbWifiName(wifi)
  }

  private[this] def checkAllCollections() = dom.newConfigurationStep1CollectionsContent <~ Transformer {
    case view: WizardCheckBox if !view.isCheck => view <~ wcbCheck()
  }

  private[this] def best9Apps(filter9: Boolean) = dom.newConfigurationStep1CollectionsContent <~ Transformer {
    case view: WizardCheckBox => view <~ wcbBest9(filter9)
  }

  private[this] def createPagers(): Ui[Any] = {
    val views = (0 until numberOfScreens) map { position =>
      (w[ImageView] <~
        paginationItemStyle <~
        vSetPosition(position)).get
    }
    dom.newConfigurationPagers <~ vgAddViews(views)
  }

  private[this] def selectPager(position: Int, resColor: Int): Ui[Any] = dom.newConfigurationPagers <~ Transformer {
    case i: ImageView if i.getPosition.contains(position) => i <~ vBackground(circleDrawable(resGetColor(resColor)))
    case i: ImageView => i <~ vBackground(circleDrawable())
  }

  private[this] def circleDrawable(color: Int = Color.LTGRAY): ShapeDrawable = {
    val drawable = new ShapeDrawable(new OvalShape())
    drawable.getPaint.setColor(color)
    drawable.getPaint.setAntiAlias(true)
    drawable
  }

  private[this] def showDialog(dialog: DialogFragment) = Ui {
    val ft = fragmentManagerContext.manager.beginTransaction()
    Option(fragmentManagerContext.manager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    dialog.show(ft, tagDialog)
  }

  private[this] def firstStepChoreographyIn = {
    (dom.newConfigurationStep0HeaderImage <~ vInvisible) ~
      (dom.newConfigurationStep0Title <~ vInvisible) ~
      (dom.newConfigurationStep0Description <~ vInvisible) ~
      (dom.newConfigurationStep0HeaderContent <~
        vPivotY(0) <~
        vAlpha(0) <~
        vScaleY(0) <~~
        applyAnimation(alpha = Some(1), scaleY = Some(1), interpolator = Some(defaultInterpolator))) ~~
      (dom.newConfigurationStep0HeaderImage <~~ slideUp) ~~
      (dom.newConfigurationStep0Title <~~ slideUp) ~~
      (dom.newConfigurationStep0Description <~ slideUp)
  }

  private[this] def thirdStepChoreographyIn = {
    (dom.newConfigurationStep2HeaderImage1 <~ vInvisible) ~
      (dom.newConfigurationStep2HeaderImage2 <~ vInvisible) ~
      (dom.newConfigurationStep2Title <~ vInvisible) ~
      (dom.newConfigurationStep2Description <~ vInvisible) ~
      (dom.newConfigurationStep2HeaderContent <~
        vPivotY(0) <~
        vAlpha(0) <~
        vScaleY(0) <~~
        applyAnimation(alpha = Some(1), scaleY = Some(1), interpolator = Some(defaultInterpolator))) ~~
      (dom.newConfigurationStep2HeaderImage1 <~~ slideLeft) ~~
      (dom.newConfigurationStep2HeaderImage2 <~~ slideRight) ~~
      (dom.newConfigurationStep2Title <~~ slideUp) ~~
      (dom.newConfigurationStep2Description <~ slideUp)
  }

  private[this] def sixthStepChoreographyIn = {
    (dom.newConfigurationStep5HeaderImage <~ vInvisible) ~
      (dom.newConfigurationStep5Title <~ vInvisible) ~
      (dom.newConfigurationStep5Description <~ vInvisible) ~
      (dom.newConfigurationStep5GoTo9Cards <~ vInvisible) ~
      (dom.newConfigurationStep5HeaderContent <~
        vPivotY(0) <~
        vAlpha(0) <~
        vScaleY(0) <~~
        applyAnimation(alpha = Some(1), scaleY = Some(1), interpolator = Some(defaultInterpolator))) ~~
      (dom.newConfigurationStep5HeaderImage <~~ slideUp) ~~
      (dom.newConfigurationStep5Title <~~ slideUp) ~~
      (dom.newConfigurationStep5Description <~ slideUp) ~~
      (dom.newConfigurationStep5GoTo9Cards <~ slideUp)
  }

  private[this] def slideUp: Snail[View] =
    vVisible + vAlpha(0) + vTranslationY(padding) ++ applyAnimation(alpha = Some(1), y = Some(0), interpolator = Some(defaultInterpolator))

  private[this] def slideLeft: Snail[View] =
    vVisible + vAlpha(0) + vTranslationX(-padding) ++ applyAnimation(alpha = Some(1), x = Some(0), interpolator = Some(defaultInterpolator))

  private[this] def slideRight: Snail[View] =
    vVisible + vAlpha(0) + vTranslationX(padding) ++ applyAnimation(alpha = Some(1), x = Some(0), interpolator = Some(defaultInterpolator))

}
