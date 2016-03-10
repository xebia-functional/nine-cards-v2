package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view._
import android.widget.{EditText, FrameLayout, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.LauncherExecutor
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.{AppsView, ContactView, ContentView, TintableImageView}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, SearchBackgroundColor, SearchIconsColor, SearchPressedColor}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class SearchBoxView(context: Context, attrs: AttributeSet, defStyle: Int)
  (implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme)
  extends FrameLayout(context, attrs, defStyle)
  with TypedFindView
  with LauncherExecutor
  with Styles { self =>

  def this(context: Context)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, attrs, 0)

  var listener: Option[SearchBoxAnimatedListener] = None

  val content = LayoutInflater.from(getContext).inflate(TR.layout.search_box_panel, self)

  lazy val editText = Option(findView(TR.launcher_search_box_text))

  lazy val icon = Option(findView(TR.launcher_search_box_icon))

  lazy val headerIcon = Option(findView(TR.launcher_header_icon))

  (self <~ vgAddView(content)).run

  def updateContentView(contentView: ContentView): Ui[_] =
    (icon <~ iconTweak(contentView)) ~
      (editText <~ searchBoxNameStyle(contentView match {
        case AppsView => R.string.searchApps
        case ContactView => R.string.searchContacts
      })) ~
      (headerIcon <~
        On.click {
          Ui(listener foreach (_.onHeaderIconClick))
        }) ~
      (content <~ searchBoxContentStyle)

  def updateHeaderIcon(resourceId: Int): Ui[_] = headerIcon <~ searchBoxButtonStyle(resourceId)

  def clean: Ui[_] = editText <~ (if (isEmpty) Tweak.blank else tvText("")) <~ etHideKeyboard

  def addTextChangedListener(onChangeText: (String) => Unit): Unit =
    (editText <~
      etAddTextChangedListener(
        (text: String, start: Int, before: Int, count: Int) => onChangeText(text))).run

  def isEmpty: Boolean = editText exists (_.getText.toString == "")

  private[this] def iconTweak(contentView: ContentView) = contentView match {
    case AppsView =>
      searchBoxButtonStyle(R.drawable.app_drawer_icon_google_play) +
        On.click (Ui(launchPlayStore))
    case ContactView =>
      searchBoxButtonStyle(R.drawable.app_drawer_icon_phone) +
        On.click (Ui(launchDial()))
  }

}

trait SearchBoxAnimatedListener {
  def onHeaderIconClick(implicit context: ActivityContextWrapper): Unit
}

trait Styles {
  def searchBoxContentStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[LinearLayout] =
    vBackgroundBoxWorkspace(theme.get(SearchBackgroundColor))

  def searchBoxCharStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(SearchIconsColor))

  def searchBoxNameStyle(resourceId: Int)(implicit context: ContextWrapper): Tweak[EditText] =
    tvHint(resourceId)

  def searchBoxButtonStyle(resourceId: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    ivSrc(resourceId) +
      tivDefaultColor(theme.get(SearchIconsColor)) +
      tivPressedColor(theme.get(SearchPressedColor))
}
