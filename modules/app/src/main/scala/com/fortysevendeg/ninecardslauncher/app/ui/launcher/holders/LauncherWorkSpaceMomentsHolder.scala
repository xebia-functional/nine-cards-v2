package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.{LayoutInflater, View}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{GenericUiContext, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.WorkSpaceMomentMenuTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.LauncherMoment
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Card
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.process.theme.models.{AppDrawerPressedColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.google.android.flexbox.FlexboxLayout
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceMomentsHolder(context: Context, presenter: LauncherPresenter, theme: NineCardsTheme, parentDimen: Dimen)
  extends LauncherWorkSpaceHolder(context)
    with Contexts[View]
    with TypedFindView {

  implicit val uiContext: UiContext[Context] = GenericUiContext(context)

  LayoutInflater.from(context).inflate(R.layout.moment_workspace_layout, this)

  val numApps = 5

  val background = Option(findView(TR.launcher_moment_background))

  val iconMoment = Option(findView(TR.launcher_moment_icon))

  val message = Option(findView(TR.launcher_moment_message))

  val appsBox = Option(findView(TR.launcher_moment_apps_layout))

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val sizeApp = (parentDimen.width - (paddingDefault * 4)) / numApps

  val drawable = {
    val s = 0 until 8 map (_ => radius.toFloat)
    val d = new ShapeDrawable(new RoundRectShape(s.toArray, javaNull, javaNull))
    d.getPaint.setColor(resGetColor(R.color.moment_workspace_background))
    d
  }

  (background <~ vBackground(drawable)).run

  def populate(moment: LauncherMoment): Ui[Any] = {
    (for {
      collection <- moment.collection
    } yield {
      val resIcon = iconCollectionWorkspace(collection.icon)
      val color = resGetColor(getIndexColor(collection.themedColorIndex))
      (iconMoment <~
        wmmPopulate(color, resIcon, None) <~
        vVisible) ~
        (message <~ vGone) ~
        (appsBox <~
          vVisible <~
          vgRemoveAllViews <~
          vgAddViews(collection.cards map (createIconCard(_, moment.momentType))))
    }) getOrElse
      ((message <~ vVisible) ~
        (iconMoment <~ vGone) ~
        (appsBox <~ vGone))
  }

  private[this] def createIconCard(card: Card, moment: Option[NineCardsMoment]): TintableImageView =
    (w[TintableImageView] <~
      lp[FlexboxLayout](sizeApp, sizeApp) <~
      tivPressedColor(theme.get(AppDrawerPressedColor)) <~
      vPaddings(paddingLeftRight = paddingDefault, 0) <~
      ivUri(card.imagePath) <~
      On.click {
        Ui(presenter.openMomentIntent(card, moment))
      }).get

}