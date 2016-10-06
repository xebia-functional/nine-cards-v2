package cards.nine.app.ui.wizard.jobs

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.app.{DialogFragment, Fragment, FragmentManager}
import android.text.Html
import android.view.ViewGroup.LayoutParams._
import android.view.{LayoutInflater, View}
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import cards.nine.app.ui.commons.CommonsTweak._
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
import cards.nine.models.types._
import cards.nine.process.collection.models.PackagesByCategory
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

class NewConfigurationUiActions(dom: WizardDOM with WizardUiListener)
  (implicit
    context: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends WizardStyles
  with ImplicitsUiExceptions {

  val numberOfScreens = 6

  val tagDialog = "dialog"

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
      createPagers() ~
      selectPager(firstStep, resColor) ~
      (dom.newConfigurationNext <~
        On.click(Ui(dom.onLoadBetterCollections())) <~
        tvColorResource(resColor))).toService
  }

  def loadSecondStep(numberOfApps: Int, collections: Seq[PackagesByCategory]): TaskService[Unit] = {
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
      selectPager(thirdStep, resColor) ~
      (dom.newConfigurationNext <~
        On.click(Ui(dom.onLoadWifiByMoment())) <~
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

}
