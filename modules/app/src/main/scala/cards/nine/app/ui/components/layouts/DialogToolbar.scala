package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.commons._
import cards.nine.models.types.{DialogToolbarSearch, DialogToolbarTitle, DialogToolbarType}
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._
import macroid.extras.EditTextTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ToolbarTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

class DialogToolbar(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends FrameLayout(context, attr, defStyleAttr)
    with TypedFindView
    with Contexts[View] { self =>

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.toolbar_dialog, this)

  lazy val toolbar = findView(TR.actions_toolbar_widget)

  lazy val title = findView(TR.actions_toolbar_title)

  lazy val search = findView(TR.actions_toolbar_search)

  lazy val extendedContent = findView(TR.actions_toolbar_extended_content)

  val closeDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CLOSE,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  def init(color: Int, dialogToolbarType: DialogToolbarType = DialogToolbarTitle)(
      implicit contextWrapper: ContextWrapper) = {
    (dialogToolbarType match {
      case DialogToolbarTitle => (title <~ vVisible) ~ (search <~ vGone)
      case DialogToolbarSearch =>
        (search <~ vVisible <~ vClearFocus) ~ (title <~ vGone)
    }) ~
      (toolbar <~
        tbNavigationIcon(closeDrawable)) ~
      (this <~
        vBackgroundColor(color))
  }

  def changeToolbarHeight(height: Int): Ui[Any] =
    toolbar <~ tbChangeHeightLayout(height)

  def addExtendedView(view: View): Ui[Any] = extendedContent <~ vgAddView(view)

  def changeIcon(icon: Int): Ui[Any] =
    Ui {
      closeDrawable.setToTypeIcon(icon)
      closeDrawable.start()
    }

  def changeText(res: Int): Ui[Any] = title <~ tvText(res)

  def changeText(text: String): Ui[Any] = title <~ tvText(text)

  def changeSearchText(res: Int): Ui[Any] = search <~ tvText(res)

  def changeSearchText(text: String = ""): Ui[Any] = search <~ tvText(text)

  def onSearchTextChangedListener(onChanged: (String, Int, Int, Int) => Unit): Ui[Any] =
    search <~ etAddTextChangedListener(onChanged)

  def clickActionSearch(performSearch: (String) => Unit) =
    search <~ etClickActionSearch(performSearch)

  def hideKeyboardSearchText(): Ui[Any] = search <~ etHideKeyboard

  def navigationClickListener(click: (View) => Ui[Any]): Ui[Any] =
    toolbar <~ tbNavigationOnClickListener(click)

}
