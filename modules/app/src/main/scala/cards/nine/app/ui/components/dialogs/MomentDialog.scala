package cards.nine.app.ui.components.dialogs

import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.LinearLayout
import cards.nine.app.ui.MomentPreferences
import cards.nine.app.ui.commons.ops.NineCardsMomentOps._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.launcher.actions.editmoment.EditMomentFragment
import cards.nine.app.ui.launcher.jobs.{LauncherJobs, NavigationJobs}
import cards.nine.commons._
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerTextColor, PrimaryColor}
import cards.nine.models.{Moment, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

class MomentDialog(moments: Seq[Moment])
  (implicit contextWrapper: ContextWrapper, launcherJobs: LauncherJobs, navigationJobs: NavigationJobs, theme: NineCardsTheme)
  extends BottomSheetDialogFragment
  with TypedFindView { dialog =>

  lazy val persistMoment = new MomentPreferences

  lazy val selectMomentList = findView(TR.select_moment_list)

  var rootView: Option[ViewGroup] = None

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  override def getTheme: Int = R.style.AppThemeDialog

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    val baseView = LayoutInflater.from(getActivity).inflate(R.layout.select_moment_dialog, javaNull, false).asInstanceOf[ViewGroup]
    rootView = Option(baseView)
    val momentItems = moments map (moment => new MomentItem(moment.momentType, moment.id))
    (selectMomentList <~
      vBackgroundColor(theme.get(DrawerBackgroundColor)) <~
      vgAddViews(momentItems)).run

    dialog.setContentView(baseView)
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

    val colorPined = if (momentPersisted) theme.get(PrimaryColor) else theme.get(DrawerTextColor)

    val colorTheme = theme.get(DrawerTextColor)

    val pinActionTweak = if (momentPersisted) {
      tivColor(colorPined) +
        On.click(Ui{
          launcherJobs.cleanPersistedMoment().resolveAsync()
          dialog.dismiss()
        }) +
        vVisible
    } else {
      vGone
    }

    ((this <~ On.click(
      Ui {
        launcherJobs.changeMoment(id).resolveAsync()
        dialog.dismiss()
      })) ~
      (line <~ vBackgroundColor(theme.getLineColor)) ~
      (icon <~ ivSrc(moment.getIconCollectionDetail) <~ tivDefaultColor(colorPined)) ~
      (text <~ tvText(moment.getName) <~ tvColor(colorPined)) ~
      (pin <~ pinActionTweak) ~
      (edit <~
        tivColor(colorTheme) <~
        On.click(Ui {
          val momentMap = Map(EditMomentFragment.momentKey -> moment.name)
          val bundle = navigationJobs.navigationUiActions.dom.createBundle(
            resGetColor(R.color.collection_fab_button_item_1),
            momentMap)
          navigationJobs.launchEditMoment(bundle).resolveAsync()
          dialog.dismiss()
        })) ~
      (delete <~
        tivColor(colorTheme) <~
        On.click(Ui {
          launcherJobs.removeMomentDialog(moment, id).resolveAsync()
          dialog.dismiss()
        }))).run

  }

}
