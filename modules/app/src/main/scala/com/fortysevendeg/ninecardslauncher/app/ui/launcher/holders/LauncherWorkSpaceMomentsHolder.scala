package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.ViewGroup.LayoutParams._
import android.view.{LayoutInflater, View}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.{NineCardsPreferencesValue, NumberOfAppsInHorizontalMoment, NumberOfRowsMoment, ShowBackgroundMoment}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.WorkSpaceMomentMenuTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder, WorkSpaceMomentIcon}
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.LauncherMoment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.google.android.flexbox.FlexboxLayout
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceMomentsHolder(context: Context, presenter: LauncherPresenter, theme: NineCardsTheme, parentDimen: Dimen)
  extends LauncherWorkSpaceHolder(context)
  with Contexts[View]
  with TypedFindView {

  LayoutInflater.from(context).inflate(R.layout.moment_workspace_layout, this)

  val preferenceValues = new NineCardsPreferencesValue

  val content = Option(findView(TR.launcher_moment_content))

  val widgets = Option(findView(TR.launcher_moment_widgets))

  val message = Option(findView(TR.launcher_moment_message))

  val appsBox = Option(findView(TR.launcher_moment_apps_layout))

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val drawable = {
    val s = 0 until 8 map (_ => radius.toFloat)
    val d = new ShapeDrawable(new RoundRectShape(s.toArray, javaNull, javaNull))
    d.getPaint.setColor(resGetColor(R.color.moment_workspace_background))
    d
  }

  def populate(moment: LauncherMoment): Ui[Any] = {
    (for {
      collection <- moment.collection
    } yield {
      val numApps = preferenceValues.getInt(NumberOfAppsInHorizontalMoment)
      val rows = preferenceValues.getInt(NumberOfRowsMoment)
      val showBackground = preferenceValues.getBoolean(ShowBackgroundMoment)
      val sizeApp = (parentDimen.width - (paddingDefault * 2)) / numApps
      val maxApps = (rows * numApps) - 1

      (appsBox <~ (if (showBackground) vBackground(drawable) else vBlankBackground)) ~
        (message <~ vGone) ~
        (content <~ vVisible) ~
        (appsBox  <~
          vgRemoveAllViews <~
          vgAddViews(createCollection(collection, sizeApp) +: (collection.cards.take(maxApps) map (createIconCard(_, moment.momentType, sizeApp)))))
    }) getOrElse
      ((message <~ vVisible) ~
        (content <~ vGone))
  }

  private[this] def createCollection(collection: Collection, sizeApp: Int) = {
    (w[WorkSpaceMomentIcon] <~
      lp[FlexboxLayout](sizeApp, WRAP_CONTENT) <~
      wmmPopulateCollection(collection) <~
      FuncOn.click { view: View =>
        val (x, y) = PositionsUtils.calculateAnchorViewPosition(view)
        val point = new Point(x + (view.getWidth / 2), y + (view.getHeight / 2))
        Ui(presenter.goToCollection(Some(collection), point))
      }).get
  }

  private[this] def createIconCard(card: Card, moment: Option[NineCardsMoment], sizeApp: Int): WorkSpaceMomentIcon =
    (w[WorkSpaceMomentIcon] <~
      lp[FlexboxLayout](sizeApp, WRAP_CONTENT) <~
      wmmPopulateCard(card) <~
      On.click {
        Ui(presenter.openMomentIntent(card, moment))
      }).get

}