package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.{ImageView, TextView, LinearLayout}
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView, R}
import macroid.Ui
import macroid.FullDsl._

class TextTab(context: Context, attrs: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attrs, defStyleAttr)
  with TypedFindView {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  setOrientation(LinearLayout.HORIZONTAL)

  setClipChildren(false)

  LayoutInflater.from(context).inflate(R.layout.app_drawer_tab, this)

  private[this] val text: Option[TextView] = Option(findView(TR.launcher_drawer_tab_text))

  private[this] val arrow: Option[ImageView] = Option(findView(TR.launcher_drawer_tab_arrow))

  private[this] val nameSpace = "http://schemas.android.com/apk/res/android"

  private[this] val textAttr = "text"

  private[this] val iconAttr = "src"

  attrs.getAttributeIntValue(nameSpace, textAttr, 0) match {
    case 0 =>
    case string => setText(string)
  }

  attrs.getAttributeIntValue(nameSpace, iconAttr, 0) match {
    case 0 =>
    case drawable => setIcon(drawable)
  }

  def setText(text: Int): Ui[_] = text <~ tvText(text)

  def setIcon(drawable: Int): Ui[_] = text <~ tvCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)

  def showArrow: Ui[_] = arrow <~ vVisible

  def hideArrow: Ui[_] = arrow <~ vInvisible
}
