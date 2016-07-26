package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.{Checkable, LinearLayout, RadioButton}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsExcerpt._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._

class HeaderRadioButton(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Checkable
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  LayoutInflater.from(context).inflate(R.layout.header_radio_button, this)

  lazy val check = findView(TR.item_check)

  lazy val title = findView(TR.item_title)

  lazy val subtitle = findView(TR.item_subtitle)

  override def isChecked: Boolean = check.isChecked

  override def setChecked(checked: Boolean): Unit = check.setChecked(checked)

  override def toggle(): Unit = check.toggle()

  def setTitle(text: String): Ui[_] = title <~ tvText(text)

  def setSubtitle(text: String): Ui[_] = subtitle <~ tvText(text)
}

object HeaderRadioButtonTweaks {

  def hrbChecked(checked: Boolean)(implicit contextWrapper: ContextWrapper): Tweak[HeaderRadioButton] =
    Tweak[HeaderRadioButton](_.setChecked(checked))

  def hrbTitle(text: String): Tweak[HeaderRadioButton] = Tweak[HeaderRadioButton](_.setTitle(text).run)

  def hrbSubTitle(text: String): Tweak[HeaderRadioButton] = Tweak[HeaderRadioButton](_.setSubtitle(text).run)

}
