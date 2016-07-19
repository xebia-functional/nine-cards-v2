package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.{CollectionsWorkSpace, MomentWorkSpace, WorkSpaceType}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class TopBarLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends FrameLayout(context, attrs, defStyle)
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  lazy val collectionsSearchPanel = Option(findView(TR.launcher_search_panel))

  lazy val collectionsBurgerIcon = Option(findView(TR.launcher_burger_icon))

  lazy val collectionsGoogleIcon = Option(findView(TR.launcher_google_icon))

  lazy val collectionsMicIcon = Option(findView(TR.launcher_mic_icon))

  lazy val momentContent = Option(findView(TR.launcher_moment_content))

  lazy val momentIcon = Option(findView(TR.launcher_moment_icon))

  lazy val momentText = Option(findView(TR.launcher_moment_text))

  lazy val momentGoogleIcon = Option(findView(TR.launcher_moment_google_icon))

  lazy val momentMicIcon = Option(findView(TR.launcher_moment_mic_icon))

  val collectionWorkspace = LayoutInflater.from(context).inflate(R.layout.collection_bar_view_panel, javaNull)

  val momentWorkspace = LayoutInflater.from(context).inflate(R.layout.moment_bar_view_panel, javaNull)

  (this <~ vgAddViews(Seq(momentWorkspace, collectionWorkspace))).run

  def init(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[Any] =
    (momentWorkspace <~ vInvisible) ~
      (collectionsSearchPanel <~
        vBackgroundBoxWorkspace(theme.get(SearchBackgroundColor))) ~
      (collectionsBurgerIcon <~
        tivDefaultColor(theme.get(SearchIconsColor)) <~
        tivPressedColor(theme.get(SearchPressedColor)) <~
        On.click(Ui(presenter.launchMenu()))) ~
      (collectionsGoogleIcon <~
        tivDefaultColor(theme.get(SearchGoogleColor)) <~
        tivPressedColor(theme.get(SearchPressedColor)) <~
        On.click(Ui(presenter.launchSearch))) ~
      (collectionsMicIcon <~
        tivDefaultColor(theme.get(SearchIconsColor)) <~
        tivPressedColor(theme.get(SearchPressedColor)) <~
        On.click(Ui(presenter.launchVoiceSearch)))

  def reloadMoment(collection: Collection)(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[Any] = {
    val resIcon = iconCollectionDetail(collection.icon)
    (momentContent <~
      On.click(goToCollection(collection))) ~
      (momentIcon <~
        vBackgroundCollection(collection.themedColorIndex) <~
        ivSrc(resIcon)) ~
      (momentText <~
        tvText(collection.name)) ~
      (momentGoogleIcon <~
        On.click(Ui(presenter.launchSearch))) ~
      (momentMicIcon <~
        On.click(Ui(presenter.launchVoiceSearch)))
  }

  def reloadByType(workSpaceType: WorkSpaceType)(implicit context: ContextWrapper): Ui[Any] = workSpaceType match {
    case MomentWorkSpace if momentWorkspace.getVisibility == View.INVISIBLE =>
      (collectionWorkspace <~ applyFadeOut()) ~ (momentWorkspace <~ applyFadeIn())
    case CollectionsWorkSpace if collectionWorkspace.getVisibility == View.INVISIBLE =>
      (collectionWorkspace <~ applyFadeIn()) ~ (momentWorkspace <~ applyFadeOut())
    case _ => Ui.nop
  }

  private[this] def goToCollection(collection: Collection)(implicit presenter: LauncherPresenter) = {
    val point = momentIcon map { view =>
      val (x, y) = PositionsUtils.calculateAnchorViewPosition(view)
      new Point(x + (view.getWidth / 2), y + (view.getHeight / 2))
    } getOrElse new Point(0, 0)
    Ui(presenter.goToCollection(Some(collection), point))
  }

}
