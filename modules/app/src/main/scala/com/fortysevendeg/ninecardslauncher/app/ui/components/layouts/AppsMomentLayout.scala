package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.WorkSpaceButtonTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.LauncherMoment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.commons.models.Card
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DrawerBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class AppsMomentLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  LayoutInflater.from(context).inflate(R.layout.apps_moment_layout, this)

  lazy val iconContent = findView(TR.launcher_moment_icon_content)

  lazy val icon = findView(TR.launcher_moment_icon)

  lazy val appsContent = findView(TR.launcher_moment_apps)

  (Lollipop.ifSupportedThen(iconContent <~ vElevation(resGetDimensionPixelSize(R.dimen.elevation_default))) getOrElse Ui.nop).run

  def populate(moment: LauncherMoment)(implicit theme: NineCardsTheme, presenter: LauncherPresenter): Ui[Any] = {
    (for {
      collection <- moment.collection
    } yield {
      val resIcon = iconCollectionDetail(collection.icon)
      val color = resGetColor(getIndexColor(collection.themedColorIndex))
      (iconContent <~
        vBackgroundColor(color) <~
        On.click {
          Ui(presenter.goToMomentWorkspace())
        }) ~
        (icon <~
          ivSrc(resIcon)) ~
        (appsContent <~
          vBackgroundColor(theme.get(DrawerBackgroundColor)) <~
          vgRemoveAllViews <~
          vgAddViews(collection.cards map (createIconCard(_, moment.momentType))))
    }) getOrElse
      Ui.nop
  }

  def setPaddingTopAndBottom(paddingTop: Int, paddingBottom: Int) =
    (iconContent <~ vPadding(paddingTop = paddingTop)) ~
      (appsContent <~ vPadding(paddingBottom = paddingBottom))

  private[this] def createIconCard(
    card: Card, moment: Option[NineCardsMoment])(implicit presenter: LauncherPresenter, theme: NineCardsTheme): WorkSpaceButton =
    (w[WorkSpaceButton] <~
      vMatchWidth <~
      wbInit(WorkSpaceAppMomentButton) <~
      wbPopulateCard(card) <~
      On.click {
        Ui(presenter.openMomentIntent(card, moment))
      }).get

}
