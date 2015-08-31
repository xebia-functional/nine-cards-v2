package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.{View, LayoutInflater}
import android.widget.{ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Tweak, Ui}

class TextTab(context: Context, attrs: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attrs, defStyleAttr)
  with TypedFindView {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.app_drawer_tab, this)

  val text: Option[TextView] = Option(findView(TR.launcher_drawer_tab_text))

  val arrow: Option[ImageView] = Option(findView(TR.launcher_drawer_tab_arrow))

  val line: Option[View] = Option(findView(TR.launcher_drawer_tab_bottom))

}

object TextTab {

  def ttInitTab(textResource: Int, drawable: Int): Tweak[TextTab] = Tweak[TextTab] { tt =>
    runUi(tt.text <~
      tvText(textResource) <~
      tvCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0))
  }

  def ttSelect = Tweak[TextTab] { tt =>
    runUi((tt.arrow <~ vVisible) ~ (tt.line <~ vVisible))
  }

  def ttUnselect = Tweak[TextTab] { tt =>
    runUi((tt.arrow <~ vInvisible) ~ (tt.line <~ vInvisible))
  }

}