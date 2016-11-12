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

  lazy val toolbar = Option(findView(TR.actions_toolbar_widget))

  lazy val title = Option(findView(TR.actions_toolbar_title))

  lazy val search = Option(findView(TR.actions_toolbar_search))

  lazy val extendedContent = Option(findView(TR.actions_toolbar_extended_content))

  val closeDrawable = new PathMorphDrawable(
    defaultIcon = IconTypes.CLOSE,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  def init(color: Int, dialogToolbarType: DialogToolbarType = DialogToolbarTitle)(implicit contextWrapper: ContextWrapper) = {
    (dialogToolbarType match {
      case DialogToolbarTitle => (title <~ vVisible) ~ (search <~ vGone)
      case DialogToolbarSearch => (title <~ vGone) ~ (search <~ vVisible)
    }) ~
    (toolbar <~
      tbNavigationIcon(closeDrawable)) ~
    (this <~
      vBackgroundColor(color))
  }

  def changeToolbarHeight(height: Int): Ui[_] = toolbar <~ tbChangeHeightLayout(height)

  def addExtendedView(view: View): Ui[_] = extendedContent <~ vgAddView(view)

  def changeIcon(icon: Int): Ui[_] =
    Ui {
      closeDrawable.setToTypeIcon(icon)
      closeDrawable.start()
    }

  def changeText(res: Int): Ui[_] = title <~ tvText(res)

  def changeText(text: String): Ui[_] = title <~ tvText(text)

  def changeSearchHintColor(color: Int): Ui[_] = search <~ etHintColor(color)

  def changeSearchText(res: Int): Ui[_] = search <~ tvText(res)

  def changeSearchText(text: String = ""): Ui[_] = search <~ tvText(text)

  def onSearchTextChangedListener(onChanged: (String, Int, Int, Int) ⇒ Unit): Ui[_] = search <~ etAddTextChangedListener(onChanged)

  def clickActionSearch(performSearch: (String) => Unit) = search <~ etClickActionSearch(performSearch)

  def showKeyboardSearchText(): Ui[_] = search <~ etShowKeyboard

  def hideKeyboardSearchText(): Ui[_] = search <~ etHideKeyboard

  def navigationClickListener(click: (View) => Ui[_]): Ui[_] = toolbar <~ tbNavigationOnClickListener(click)

}
