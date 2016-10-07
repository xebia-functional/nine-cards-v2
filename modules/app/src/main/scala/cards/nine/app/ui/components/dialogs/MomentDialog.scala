package cards.nine.app.ui.components.dialogs

import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import cards.nine.models.types.NineCardsMoment
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.ui.MomentPreferences
import cards.nine.app.ui.commons.ops.NineCardsMomentOps._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.launcher.LauncherPresenter
import cards.nine.process.commons.models.Moment
import cards.nine.process.theme.models.{DrawerBackgroundColor, DrawerTextColor, NineCardsTheme, PrimaryColor}
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class MomentDialog(moments: Seq[Moment])(implicit contextWrapper: ContextWrapper, presenter: LauncherPresenter, theme: NineCardsTheme)
  extends BottomSheetDialog(contextWrapper.getOriginal)
  with TypedFindView { dialog =>

  lazy val persistMoment = new MomentPreferences

  lazy val selectMomentList = findView(TR.select_moment_list)

  val sheetView = getLayoutInflater.inflate(TR.layout.select_moment_dialog)

  setContentView(sheetView)

  val momentItems = moments flatMap {
    _.momentType match {
      case Some(moment) => Some(new MomentItem(moment))
      case _ => None
    }
  }

  (selectMomentList <~
    vBackgroundColor(theme.get(DrawerBackgroundColor)) <~
    vgAddViews(momentItems)).run

  class MomentItem(moment: NineCardsMoment)
    extends LinearLayout(contextWrapper.getOriginal)
    with TypedFindView {

    LayoutInflater.from(getContext).inflate(TR.layout.select_moment_item, this)

    val icon = findView(TR.select_moment_item_icon)

    val text = findView(TR.select_moment_item_text)

    val close = findView(TR.select_moment_item_close)

    val momentPersisted = persistMoment.getPersistMoment.contains(moment)

    val colorText = if (momentPersisted) theme.get(PrimaryColor) else theme.get(DrawerTextColor)

    val actionTweak = if (momentPersisted) {
      val iconIndicatorDrawable = PathMorphDrawable(
        defaultIcon = IconTypes.CLOSE,
        defaultColor = colorText,
        defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
        padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))
      ivSrc(iconIndicatorDrawable) +
        On.click(Ui{
          presenter.cleanPersistedMoment()
          dialog.dismiss()
        }) +
        vVisible
    } else {
      vGone
    }

    ((this <~ On.click(
      Ui {
        presenter.changeMoment(moment)
        dialog.dismiss()
      })) ~
      (icon <~ ivSrc(moment.getIconCollectionDetail) <~ tivDefaultColor(colorText)) ~
      (text <~ tvText(moment.getName) <~ tvColor(colorText)) ~
      (close <~ actionTweak)).run

  }

}
