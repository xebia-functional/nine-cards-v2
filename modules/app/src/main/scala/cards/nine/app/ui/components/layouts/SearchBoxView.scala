package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view._
import android.widget.{EditText, FrameLayout, LinearLayout}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.components.widgets.{AppsView, ContactView, ContentView, TintableImageView}
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models._
import com.fortysevendeg.macroid.extras.EditTextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class SearchBoxView(context: Context, attrs: AttributeSet, defStyle: Int)
  extends FrameLayout(context, attrs, defStyle)
  with TypedFindView
  with Contexts[View]
  with Styles { self =>

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  var listener: Option[SearchBoxAnimatedListener] = None

  val content = LayoutInflater.from(getContext).inflate(TR.layout.search_box_panel, self)

  lazy val editText = Option(findView(TR.launcher_search_box_text))

  lazy val icon = Option(findView(TR.launcher_search_box_icon))

  lazy val headerIcon = Option(findView(TR.launcher_header_icon))

  (self <~ vgAddView(content)).run

  def updateContentView(contentView: ContentView)(implicit theme: NineCardsTheme): Ui[_] =
    (icon <~ iconTweak(contentView)) ~
      (editText <~ searchBoxNameStyle(contentView match {
        case AppsView => R.string.searchApps
        case ContactView => R.string.searchContacts
      })) ~
      (headerIcon <~
        On.click {
          Ui(listener foreach (_.onHeaderIconClick()))
        }) ~
      (content <~ searchBoxContentStyle)

  def updateHeaderIcon(resourceId: Int)(implicit theme: NineCardsTheme): Ui[_] = headerIcon <~ searchBoxButtonStyle(resourceId)

  def showKeyboard: Ui[_] = editText <~ etShowKeyboard

  def clean: Ui[_] = editText <~ (if (isEmpty) Tweak.blank else tvText("")) <~ etHideKeyboard

  def enableSearch: Ui[_] = editText <~ Tweak[EditText] { view =>
    view.setEnabled(true)
    view.setFocusable(true)
    view.setFocusableInTouchMode(true)
  }

  def disableSearch: Ui[_] = editText <~ Tweak[EditText] { view =>
    view.setEnabled(false)
    view.setFocusable(false)
  }

  def addTextChangedListener(onChangeText: (String) => Unit): Unit =
    (editText <~
      etAddTextChangedListener(
        (text: String, start: Int, before: Int, count: Int) => onChangeText(text))).run

  def isEmpty: Boolean = editText exists (_.getText.toString == "")

  private[this] def iconTweak(contentView: ContentView)(implicit theme: NineCardsTheme) = contentView match {
    case AppsView =>
      searchBoxButtonStyle(R.drawable.app_drawer_icon_google_play) +
        On.click {
          Ui(listener foreach (_.onAppStoreIconClick()))
        }
    case ContactView =>
      searchBoxButtonStyle(R.drawable.app_drawer_icon_phone) +
        On.click {
          Ui(listener foreach (_.onContactsIconClick()))
        }
  }

}

case class SearchBoxAnimatedListener(
  onHeaderIconClick: () => Unit = () => {},
  onAppStoreIconClick: () => Unit = () => {},
  onContactsIconClick: () => Unit = () => {})

trait Styles {
  def searchBoxContentStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[LinearLayout] =
    vBackgroundBoxWorkspace(theme.get(SearchBackgroundColor))

  def searchBoxNameStyle(resourceId: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[EditText] =
    tvHint(resourceId) +
      tvColor(theme.get(SearchTextColor)) +
      tvHintColor(theme.get(SearchTextColor).alpha(0.8f))

  def searchBoxButtonStyle(resourceId: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    ivSrc(resourceId) +
      tivDefaultColor(theme.get(SearchIconsColor)) +
      tivPressedColor(theme.get(SearchPressedColor))
}
