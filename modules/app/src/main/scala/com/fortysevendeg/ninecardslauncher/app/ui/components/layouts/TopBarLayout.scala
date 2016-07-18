package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class TopBarLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends FrameLayout(context, attrs, defStyle)
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  lazy val searchPanel = Option(findView(TR.launcher_search_panel))

  lazy val burgerIcon = Option(findView(TR.launcher_burger_icon))

  lazy val googleIcon = Option(findView(TR.launcher_google_icon))

  lazy val micIcon = Option(findView(TR.launcher_mic_icon))

  val collectionWorkspace = LayoutInflater.from(context).inflate(R.layout.collection_bar_view_panel, javaNull)

  val momentWorkspace = LayoutInflater.from(context).inflate(R.layout.moment_bar_view_panel, javaNull)

  (this <~ vgAddViews(Seq(momentWorkspace, collectionWorkspace))).run

  def init(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[Any] =
    (momentWorkspace <~ vGone) ~
      (searchPanel <~
        vBackgroundBoxWorkspace(theme.get(SearchBackgroundColor))) ~
      (burgerIcon <~
        tivDefaultColor(theme.get(SearchIconsColor)) <~
        tivPressedColor(theme.get(SearchPressedColor)) <~
        On.click(Ui(presenter.launchMenu()))) ~
      (googleIcon <~
        tivDefaultColor(theme.get(SearchGoogleColor)) <~
        tivPressedColor(theme.get(SearchPressedColor)) <~
        On.click(Ui(presenter.launchSearch))) ~
      (micIcon <~
        tivDefaultColor(theme.get(SearchIconsColor)) <~
        tivPressedColor(theme.get(SearchPressedColor)) <~
        On.click(Ui(presenter.launchVoiceSearch)))

}
