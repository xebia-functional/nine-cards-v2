package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view._
import android.widget.{EditText, FrameLayout, LinearLayout}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.widgets.{AppsView, ContactView, ContentView}
import cards.nine.app.ui.components.drawables.tweaks.PathMorphDrawableTweaks._
import cards.nine.app.ui.launcher.LauncherActivity
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models._
import cards.nine.models.types.theme.{SearchBackgroundColor, SearchIconsColor, SearchPressedColor, SearchTextColor}
import com.fortysevendeg.macroid.extras.EditTextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class SearchBoxView(context: Context, attrs: AttributeSet, defStyle: Int)
  extends FrameLayout(context, attrs, defStyle)
  with TypedFindView
  with Contexts[View] { self =>

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  var listener: Option[SearchBoxAnimatedListener] = None

  val content = LayoutInflater.from(getContext).inflate(TR.layout.search_box_panel, self)

  lazy val editText = Option(findView(TR.launcher_search_box_text))

  lazy val icon = Option(findView(TR.launcher_search_box_icon))

  lazy val headerIcon = Option(findView(TR.launcher_header_icon))

  val appDrawerJobs = context match {
    case activity: LauncherActivity => activity.appDrawerJobs
  }

  val headerIconDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.BURGER,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_default))

  (self <~ vgAddView(content)).run

  def updateContentView(contentView: ContentView)(implicit theme: NineCardsTheme): Ui[_] = {
    headerIconDrawable.setColor(theme.get(SearchIconsColor))
    (icon <~
      tivDefaultColor(theme.get(SearchIconsColor)) <~
      tivPressedColor(theme.get(SearchPressedColor)) <~
      On.click {
        Ui(listener foreach (_.onOptionsClick()))
      }) ~
      (editText <~
        searchBoxNameStyle(contentView match {
          case AppsView => R.string.searchApps
          case ContactView => R.string.searchContacts
        }) <~
        etClickActionSearch((query) => {
          appDrawerJobs.loadSearch(query).resolveAsync()
        })) ~
      (headerIcon <~
        ivSrc(headerIconDrawable) <~
        On.click {
          Ui(listener foreach (_.onHeaderIconClick()))
        }) ~
      (content <~ searchBoxContentStyle)
  }

  def updateHeaderIcon(icon: Int)(implicit theme: NineCardsTheme): Ui[_] =
    headerIcon <~ pmdAnimIcon(icon)

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

  private[this] def searchBoxContentStyle(implicit theme: NineCardsTheme): Tweak[LinearLayout] =
    vBackgroundBoxWorkspace(theme.get(SearchBackgroundColor))

  private[this] def searchBoxNameStyle(resourceId: Int)(implicit theme: NineCardsTheme): Tweak[EditText] =
    tvHint(resourceId) +
      tvColor(theme.get(SearchTextColor)) +
      tvHintColor(theme.get(SearchTextColor).alpha(0.8f))

}

case class SearchBoxAnimatedListener(
  onHeaderIconClick: () => Unit = () => {},
  onOptionsClick: () => Unit = () => {})
