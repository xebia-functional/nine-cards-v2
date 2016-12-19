package cards.nine.app.ui.components.dialogs

import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, LayoutInflater, View, ViewGroup}
import android.widget.FrameLayout.LayoutParams
import android.widget.{LinearLayout, TextView}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.MomentPreferences
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.NineCardsMomentOps._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.widgets.TintableImageView
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.launcher.jobs.{LauncherJobs, NavigationJobs}
import cards.nine.commons._
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.types.theme.{
  DrawerBackgroundColor,
  DrawerIconColor,
  DrawerTextColor,
  PrimaryColor
}
import cards.nine.models.{Moment, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import macroid.extras.FrameLayoutTweaks._
import macroid.extras.ImageViewTweaks._
import macroid.extras.LinearLayoutTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

import scala.concurrent.ExecutionContext.Implicits.global

class MomentDialog(moments: Seq[Moment])(
    implicit contextWrapper: ContextWrapper,
    launcherJobs: LauncherJobs,
    navigationJobs: NavigationJobs,
    theme: NineCardsTheme)
    extends BottomSheetDialogFragment
    with TypedFindView { dialog =>

  lazy val persistMoment = new MomentPreferences

  lazy val selectMomentList = findView(TR.select_moment_list)

  val hideableKey = "hideable-key"

  var rootView: Option[ViewGroup] = None

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val paddingLarge = resGetDimensionPixelSize(R.dimen.padding_large)

  override protected def findViewById(id: Int): View =
    rootView.map(_.findViewById(id)).orNull

  override def getTheme: Int = R.style.AppThemeDialog

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    val baseView = LayoutInflater
      .from(getActivity)
      .inflate(R.layout.select_moment_dialog, javaNull, false)
      .asInstanceOf[ViewGroup]
    rootView = Option(baseView)
    val momentItems  = moments map (moment => new MomentItem(moment.momentType, moment.id))
    val paramsHeader = new LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    (selectMomentList <~
      vBackgroundColor(theme.get(DrawerBackgroundColor)) <~
      vgAddViews(momentItems) <~
      vgAddViewByIndexParams(createHeader(), 0, paramsHeader)).run

    dialog.setContentView(baseView)
  }

  def createHeader(): LinearLayout = {
    def swapIcons = rootView <~ Transformer {
      case image: TintableImageView
          if image.isType(hideableKey) && image.getVisibility == View.VISIBLE =>
        image <~~ applyFadeOut() <~ vGone
      case image: TintableImageView
          if image.isType(hideableKey) && image.getVisibility == View.GONE =>
        image <~ applyFadeIn()
    }

    (l[LinearLayout](
      w[TextView] <~
        llMatchWeightHorizontal <~
        tvColor(theme.get(DrawerTextColor)) <~
        tvGravity(Gravity.CENTER_VERTICAL) <~
        vPadding(paddingLeft = paddingDefault) <~
        tvBoldLight <~
        tvText(R.string.select_moment) <~
        tvSizeResource(R.dimen.text_xlarge),
      w[TintableImageView] <~
        vWrapContent <~
        vSelectableItemBackground <~
        vPaddings(paddingLarge) <~
        ivSrc(R.drawable.icon_action_bar_options) <~
        flLayoutGravity(Gravity.RIGHT) <~
        tivColor(theme.get(DrawerIconColor)) <~
        On.click(swapIcons)) <~
      vPadding(paddingLeft = paddingDefault, paddingRight = paddingDefault)).get
  }

  class MomentItem(moment: NineCardsMoment, id: Int)
      extends LinearLayout(contextWrapper.getOriginal)
      with TypedFindView {

    LayoutInflater.from(getContext).inflate(TR.layout.select_moment_item, this)

    val icon = findView(TR.select_moment_item_icon)

    val text = findView(TR.select_moment_item_text)

    val pin = findView(TR.select_moment_item_pin)

    val edit = findView(TR.select_moment_item_edit)

    val delete = findView(TR.select_moment_item_delete)

    val line = findView(TR.select_moment_item_line)

    val momentPersisted = persistMoment.getPersistMoment.contains(moment)

    val colorPined =
      if (momentPersisted) theme.get(PrimaryColor)
      else theme.get(DrawerTextColor)

    val colorTheme = theme.get(DrawerTextColor)

    val pinActionTweak = if (momentPersisted) {
      tivColor(colorPined) +
        On.click(Ui {
          launcherJobs.cleanPersistedMoment().resolveAsync()
          dialog.dismiss()
        }) +
        vVisible
    } else {
      vGone
    }

    ((this <~ On.click(Ui {
      launcherJobs.changeMoment(id).resolveAsync()
      dialog.dismiss()
    })) ~
      (line <~ vBackgroundColor(theme.getLineColor)) ~
      (icon <~ ivSrc(moment.getIconCollectionDetail) <~ tivDefaultColor(colorPined)) ~
      (text <~ tvText(moment.getName) <~ tvColor(colorPined)) ~
      (pin <~ pinActionTweak) ~
      (edit <~
        vSetType(hideableKey) <~
        vGone <~
        tivColor(colorTheme) <~
        On.click(Ui {
          navigationJobs.launchEditMoment(moment.name).resolveAsync()
          dialog.dismiss()
        })) ~
      (delete <~
        vSetType(hideableKey) <~
        vGone <~
        tivColor(colorTheme) <~
        On.click(Ui {
          launcherJobs.removeMomentDialog(moment, id).resolveAsync()
          dialog.dismiss()
        }))).run

  }

}
